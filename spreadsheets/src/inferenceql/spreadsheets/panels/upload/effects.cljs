(ns inferenceql.spreadsheets.panels.upload.effects
  (:require [re-frame.core :as rf]
            [cljs.core.async :as async :refer [go put! <!]]
            [goog.string :as gstring]
            [clojure.string :as str]
            [ajax.core]
            [ajax.edn]
            [lambdaisland.uri :as uri]
            [clojure.edn :as edn]
            [goog.crypt.base64 :as b64]
            [medley.core :as medley]))

(defn read-files-effect [params]
  (let [{:keys [files on-success on-failure]} params
        file-reads (async/chan)
        file-reads-batched (async/into [] (async/take (count files) file-reads))]

    (doseq [[f-name f] files]
      (let [rdr (js/FileReader.)
            on-load (fn [_]
                      (this-as this
                        (let [contents (.-result this)]
                          (put! file-reads [::success f-name contents]))))
            on-error (fn [error]
                       (this-as this
                         (put! file-reads [::failure f-name error])))]

        (set! (.-onload rdr) on-load)
        (set! (.-onerror rdr) on-error)

        (try
          (.readAsText rdr f)
          (catch js/Error error
            ;; This catches when no file has been selected.
            (put! file-reads [::failure f-name error])))))

    (go
     (let [reads (<! file-reads-batched)
           success? #(= (first %) ::success)]
       (if (every? success? reads)
         (let [;; Create a map of file name to contents.
               read-map (zipmap (map #(nth % 1) reads)
                                (map #(nth % 2) reads))]
           (rf/dispatch (conj on-success read-map)))
         (let [failure? #(= (first %) ::failure)
               failures (filter failure? reads)
               failure-messages (->> (for [[_ f-name error] failures]
                                       (gstring/format "Failed reading %s." f-name))
                                     (str/join "\n"))]
           (rf/dispatch (conj on-failure failure-messages))))))))

(rf/reg-fx :upload/read-files-effect read-files-effect)

(defn read-url-effect [params]
  (let [{:keys [url username password on-success on-failure]} params
        config-edn-url (uri/join url "config.edn")
        config-read (async/chan)

        file-reads (async/chan)
        file-reads-batched (async/into [] (async/take 3 file-reads))]
    (ajax.core/ajax-request
      {:uri config-edn-url
       :method :get
       :handler #(put! config-read %)
       :response-format (ajax.core/text-response-format)})

    (go
     (let [[status data] (<! config-read)]
       (if (false? status)
         (let [failure-msg (str "Could not read config.edn at " config-edn-url)]
           (rf/dispatch (conj on-failure failure-msg)))

         (let [config (edn/read-string data)
               data-to-fetch (select-keys config [:model :dataset :dataset-schema])]
           (doseq [[key filename] data-to-fetch]
             (let [file-url (uri/join url filename)]
               (ajax.core/ajax-request
                {:uri file-url
                 :method :get
                 :handler (fn [[status data]]
                            (put! file-reads {:success status
                                              :file-key key
                                              :filename filename
                                              :file-url file-url
                                              :data data})) ;; May be file data or failure data.
                 :response-format (ajax.core/text-response-format)})))))))

    (go
     (let [reads (<! file-reads-batched)]
       (if (every? :success reads)
         (let [name-map  {:dataset-name "data" :model-name "model"}
               reads (medley/index-by :file-key reads)]
           (rf/dispatch (conj on-success name-map reads)))
         (let [failures (remove :success reads)
               failure-messages (->> (for [{:keys [file-key filename data]} failures]
                                       (gstring/format "Failed reading %s -- %s." file-key filename))
                                     (str/join "\n"))]
           (rf/dispatch (conj on-failure failure-messages))))))))


(rf/reg-fx :upload/read-url-effect read-url-effect)
