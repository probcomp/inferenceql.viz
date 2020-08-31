(ns inferenceql.spreadsheets.panels.upload.effects
  (:require [re-frame.core :as rf]))

(defn read [reads]
  (doseq [{:keys [file on-success on-failure]} reads]
    (let [rdr (js/FileReader.)
          on-load (fn [_]
                    (this-as this
                      (let [contents (.-result this)]
                        (rf/dispatch (conj on-success contents)))))

          on-error (fn [error]
                     (this-as this
                       (let [contents (.-result this)]
                         (rf/dispatch (conj on-failure error)))))]
      (set! (.-onload rdr) on-load)
      (set! (.-onerror rdr) on-error)
      (.readAsText rdr file))))

(rf/reg-fx :upload/read read)

