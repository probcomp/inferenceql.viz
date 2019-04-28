(ns inferdb.spreadsheets.subs
  (:require [re-frame.core :as rf]))

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

(def default-hot-settings
  {:settings {:data         []
              :row-headers  true
              :colHeaders   []
              :filters      true
              :dropdownMenu true}})

(defn hot-props
  [{:keys [headers rows] :as db} _]
  (-> default-hot-settings
      (assoc-in [:settings :data] (cell-vector headers rows))
      (assoc-in [:settings :colHeaders] headers)))
(rf/reg-sub :hot-props hot-props)
