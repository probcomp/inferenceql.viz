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

(defn handle-reads [file-reads num-reads on-success on-failure]
  (let [file-reads-batched (async/into [] (async/take num-reads file-reads))]
    (go
     (let [reads (<! file-reads-batched)]
       (if (every? :success reads)
         (let [reads (medley/index-by :file-key reads)]
           (rf/dispatch (conj on-success reads)))
         (let [failures (remove :success reads)
               failure-messages (->> (for [{:keys [file-key filename data]} failures]
                                       (gstring/format "Failed reading %s -- %s." file-key filename))
                                     (str/join "\n"))]
           (rf/dispatch (conj on-failure failure-messages))))))))

(defn handle-reads-2 [file-reads num-reads config on-success on-failure]
  (let [file-reads-batched (async/into [] (async/take num-reads file-reads))]
    (go
     (let [reads (<! file-reads-batched)]
       (if (every? :success reads)
         (rf/dispatch (conj on-success reads config))
         (let [failures (remove :success reads)
               failure-messages (->> (for [{:keys [filename file-url]} failures]
                                       (gstring/format "Failed reading %s at %s." filename file-url))
                                  (str/join "\n"))]
           (rf/dispatch (conj on-failure failure-messages))))))))

(defn read-files-effect [params]
  (let [{:keys [files on-success on-failure]} params
        file-reads (async/chan)]


    ;; TODO: add a fake config file here.
    ;; TODO: create a map form :file-keys to :config-path in fake config

    (doseq [[file-key file-obj] files]
      (let [put-map {:file-key file-key
                     :filename (.-name file-obj)
                     :file-type (.-type file-obj)
                     :success nil ; To be set.
                     :data nil} ; To be set.

            ;; TODO we need to deal with :config-path instead of :file-key

            rdr (js/FileReader.)
            on-load (fn [_]
                      (this-as this
                        (let [contents (.-result this)]
                          (put! file-reads (merge put-map {:success true
                                                           :data contents})))))
            on-error (fn [error]
                       (this-as this
                         (put! file-reads (merge put-map {:success false
                                                          :data error}))))]
        (set! (.-onload rdr) on-load)
        (set! (.-onerror rdr) on-error)

        (try
          (.readAsText rdr file-obj)
          (catch js/Error error
            ;; This catches when no file has been selected.
            (put! file-reads (merge put-map {:success false
                                             :data error}))))))
    (handle-reads file-reads (count files) on-success on-failure)))

(rf/reg-fx :upload/read-files-effect read-files-effect)

(defn read-url-effect [params]
  (let [{:keys [url username password on-success on-failure]} params
        config-read (async/chan)
        config-edn-url (uri/join url "config.edn")]

    (ajax.core/ajax-request
      {:uri config-edn-url
       :method :get
       :handler #(put! config-read %)
       :response-format (ajax.core/text-response-format)})

    (go
     (let [[status data] (<! config-read)]

       ;; TODO: maybe put this config file on the reads channel as well.

       (if (false? status)
         (let [failure-msg (str "Could not read config.edn at " config-edn-url)]
           (rf/dispatch (conj on-failure failure-msg)))

         (let [config (edn/read-string data)
               data-to-fetch (-> (for [category [:datasets :models :geodata]]
                                   (let [items-in-category (get config category)]
                                     (for [[item-key item] items-in-category]
                                       (case category
                                         :datasets [{:config-path [category item-key :data]
                                                     :filename (:filename item)}
                                                    ;; Datasets have this extra path for the schema
                                                    ;; that we have to fetch.
                                                    {:config-path [category item-key :schema]
                                                     :filename (:schema-filename item)}]
                                         {:config-path [category item-key :data]
                                          :filename (:filename item)}))))
                                 (flatten))

               file-reads (async/chan)]

           (doseq [{:keys [config-path filename]} data-to-fetch]
             (let [file-url (uri/join url filename)]
               (ajax.core/ajax-request
                {:uri file-url
                 :method :get
                 :handler (fn [[status data]]
                            (put! file-reads {:success status
                                              :config-path config-path
                                              :filename filename
                                              :file-url file-url
                                              :data data})) ;; May be file data or failure data.
                 :response-format (ajax.core/text-response-format)})))

           (handle-reads-2 file-reads (count data-to-fetch) config on-success on-failure)))))))

(rf/reg-fx :upload/read-url-effect read-url-effect)
