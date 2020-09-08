(ns inferenceql.spreadsheets.panels.upload.effects
  (:require [re-frame.core :as rf]
            [cljs.core.async :as async :refer [go put! <!]]
            [goog.string :as gstring]
            [clojure.string :as str]))

(defn read [params]
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

(rf/reg-fx :upload/read read)

