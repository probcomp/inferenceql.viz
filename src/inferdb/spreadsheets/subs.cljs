(ns inferdb.spreadsheets.subs
  (:require [re-frame.core :as rf]
            [inferdb.spreadsheets.db :as db]
            [inferdb.spreadsheets.views :as views]))

(defn- cell-vector
  "Takes tabular data represented as a sequence of maps and reshapes the data as a
  2D vector of cells and a vector of headers."
  [headers rows]
  (into []
        (map (fn [row]
               (into []
                     (map #(get row %))
                     headers)))
        rows))

(defn hot-props
  [{:keys [::db/headers ::db/rows] :as db} _]
  (-> views/default-hot-settings
      (assoc-in [:settings :data] (cell-vector headers rows))
      (assoc-in [:settings :colHeaders] headers)))
(rf/reg-sub :hot-props hot-props)
