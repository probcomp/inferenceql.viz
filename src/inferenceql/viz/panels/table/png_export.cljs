(ns inferenceql.viz.panels.table.png-export
  (:require [re-frame.core :as rf]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]
            [inferenceql.viz.panels.control.db :as control-db]
            [inferenceql.viz.util :as util]
            [inferenceql.viz.config :refer [config]]
            [clojure.core.async :refer [go-loop go <! >! put! chan close!]]
            [cljs.core.async.interop :refer-macros [<p!]]
            [goog.string :refer [format]]
            [medley.core :as medley]))

(defn clean
  "Rounds all non-integer numbers to 2 decimals and removes symbols."
  [row]
  (medley/map-vals (fn [val]
                     (cond
                       (string? val) val
                       (integer? val) val
                       (symbol? val) nil
                       (float? val) (format "%.2f" val)
                       :else val))
                   row))

(defn timeout [ms]
  (let [c (chan)]
    (js/setTimeout (fn [] (close! c)) ms)
    c))

(def table-set (chan))

(defn table-set-done []
  (put! table-set true))

(defn render-table-pngs []
  ;; So puppeteer stays open.
  (set! js/downloads_done false)

  (go (loop [tables (map-indexed vector (:tables config))]
        (if (seq tables)
          (let [[[table-idx table] & tables-rest] tables
                {:keys [column-names rows]} table
                rows (mapv clean rows)]

            (rf/dispatch-sync [:table/set rows column-names])
            (<! table-set)

            (let [table (.querySelector js/document "#table-container")
                  canvas (<p! (js/html2canvas table))
                  blob-channel (chan)
                  filename (format "table%03d.png" (inc table-idx))]
              (.toBlob canvas #(put! blob-channel %))
              (js/saveAs (<! blob-channel) filename))
              ;;(set! js/blob (<! blob-channel)))
            (recur tables-rest))

          (do
            ;; Wait 5 seconds
            (<! (timeout 5000))
            ;; Allow puppeteer to close.
            (set! js/downloads_done true))))))


