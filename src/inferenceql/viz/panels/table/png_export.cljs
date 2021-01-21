(ns inferenceql.viz.panels.table.png-export
  (:require [re-frame.core :as rf]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]
            [inferenceql.viz.panels.control.db :as control-db]
            [inferenceql.viz.util :as util]
            [clojure.core.async :refer [go-loop <! put! chan]]))

(defn render-table-pngs []
  ;; Not sure why I have to use dispatch and dispatch-sync.
  (doseq [i (range 5)]
    (let [filename (str "image" i ".png")
          data (repeat i {:foo i :bar i})]
      (rf/dispatch-sync [:table/set data [:foo :bar]])

      (let [table (.querySelector js/document "#table-container")]
        (doto (js/html2canvas table)
          (.then (fn [canvas]
                   (let [save-fn (fn [blob]
                                   (js/saveAs blob filename))]
                     (.toBlob canvas save-fn))))))))


(defn render-table-pngs []
  (go-loop [idxs (range 5)]
    (let [[i & ixs] idxs
          filename (str "image" i ".png")
          data (repeat i {:foo i :bar i})]
      (rf/dispatch-sync [:table/set data [:foo :bar]])

      (let [table (.querySelector js/document "#table-container")
            canvas (<! (js/html2canvas table))
            blob-channel (chan)] ;; fix this
        (.toBlob canvas #(put! blob-channel %))))))

(let [blob (<! blob-channel)]
  (js/saveAs blob filename))

  #_(js/setTimeout (fn []
                     (rf/dispatch-sync [:table/export-png])
                     (.log js/console :here2))
                   0))

(rf/reg-event-fx
 :table/export-png
 event-interceptors
 (fn [{:keys [db]} [_ filename]]
   (let [table (.querySelector js/document "#table-container")]
     (doto (js/html2canvas table)
       (.then (fn [canvas]
                (let [save-fn (fn [blob]
                                (js/saveAs blob filename))]
                  (.toBlob canvas save-fn))))))
   ;; noop map
   {}))

