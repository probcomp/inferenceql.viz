(ns inferenceql.viz.panels.table.png-export
  (:require [re-frame.core :as rf]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]
            [inferenceql.viz.panels.control.db :as control-db]
            [inferenceql.viz.util :as util]
            [clojure.core.async :refer [go-loop <! put! chan]]
            [cljs.core.async.interop :refer-macros [<p!]]))

(defn render-table-pngs []
  (go-loop [indicies (range 1 5)]
    (let [[i & i-rest] indicies
          filename (str "image" i ".png")
          data (repeat i {:foo i :bar i})]

      (rf/dispatch-sync [:table/set data [:foo :bar]])

      (let [table (.querySelector js/document "#table-container")
            canvas (<p! (js/html2canvas table))
            blob-channel (chan)] ;; fix this
        (.toBlob canvas #(put! blob-channel %)
          (let [blob (<! blob-channel)]
            (js/saveAs blob filename)))))))



