(ns inferenceql.viz.panels.table.png-export
  (:require [re-frame.core :as rf]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]
            [inferenceql.viz.panels.control.db :as control-db]
            [inferenceql.viz.util :as util]
            [clojure.core.async :refer [go-loop go <! >! put! chan close!]]
            [cljs.core.async.interop :refer-macros [<p!]]))

(defn timeout [ms]
  (let [c (chan)]
    (js/setTimeout (fn [] (close! c)) ms)
    c))

(def table-set (chan))

(defn table-set-done []
  (put! table-set true))

(defn render-table-pngs []
  (go-loop [indicies (range 1 5)]
    (when (seq indicies)
      (let [[i & i-rest] indicies
            filename (str "foo" i ".png")
            data (repeat i {:foo i :bar i})]

        (rf/dispatch-sync [:table/set data [:foo :bar]])

        (<! table-set)

        (.log js/console :after)
        (let [table (.querySelector js/document "#table-container")
              canvas (<p! (js/html2canvas table))
              blob-channel (chan)] ;; fix this
          (.toBlob canvas #(put! blob-channel %))
          (let [blob (<! blob-channel)]
            (js/saveAs blob filename)))
        (recur i-rest)))))


