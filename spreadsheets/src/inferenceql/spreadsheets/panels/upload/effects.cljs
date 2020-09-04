(ns inferenceql.spreadsheets.panels.upload.effects
  (:require [re-frame.core :as rf]
            [cljs.core.async :as async :refer [go put! <!]]))

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
        (.readAsText rdr f)))
        ;; TODO: catch exception here and put failure on channel.

    (go
      (let [reads (<! file-reads-batched)]
        (if (every-pred #(= (nth % 0) ::success))
          (let [read-map (zipmap (map #(nth % 1) reads)
                                 (map #(nth % 2) reads))]
            (rf/dispatch (conj on-success read-map)))
          (rf/dispatch (conj on-failure "TODO: some failure message for file read.")))))))

(rf/reg-fx :upload/read read)

