(ns inferenceql.viz.panels.table.png-export
  (:require [re-frame.core :as rf]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]
            [inferenceql.viz.panels.control.db :as control-db]
            [inferenceql.viz.util :as util]
            [inferenceql.viz.config :refer [config]]
            [clojure.core.async :refer [go-loop go <! >! put! chan close!]]
            [cljs.core.async.interop :refer-macros [<p!]]
            [goog.string :refer [format]]))

(defn timeout [ms]
  (let [c (chan)]
    (js/setTimeout (fn [] (close! c)) ms)
    c))

(def table-set (chan))

(defn table-set-done []
  (put! table-set true))

(defn render-table-pngs []
  (go (loop [tables (map-indexed vector (:tables config))]
        (when (seq tables)
          (let [[[table-idx table] & tables-rest] tables
                {:keys [column-names rows]} table]

            (rf/dispatch-sync [:table/set rows column-names])
            (<! table-set)

            (let [table (.querySelector js/document "#table-container")
                  canvas (<p! (js/html2canvas table))
                  blob-channel (chan)
                  filename (format "table%03d.png" (inc table-idx))]
              (.toBlob canvas #(put! blob-channel %))
              (js/saveAs (<! blob-channel) filename))
            (recur tables-rest))))))


