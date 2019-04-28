(ns inferdb.spreadsheets.subs
  (:require [re-frame.core :as rf]
            [inferdb.spreadsheets.db :as db]
            [inferdb.spreadsheets.views :as views]))

(defn table-headers
  [db _]
  (db/table-headers db))
(rf/reg-sub :table-headers table-headers)

(defn table-rows
  [db _]
  (db/table-rows db))
(rf/reg-sub :table-rows table-rows)

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
  [{:keys [::db/headers ::db/rows]} _]
  (-> views/default-hot-settings
      (assoc-in [:settings :data] (cell-vector headers rows))
      (assoc-in [:settings :colHeaders] headers)))
(rf/reg-sub :hot-props
            (fn [_ _]
              {::db/headers (rf/subscribe [:table-headers])
               ::db/rows    (rf/subscribe [:table-rows])})
            hot-props)

(defn selections
  [db _]
  (db/table-selections db))
(rf/reg-sub :selections selections)

(defn selected-map
  [headers rows [row col row2 col2 selection-layer-level]]
  (let [selected-headers (mapv headers (if (<= col col2)
                                         (range col (inc col2)  1)
                                         (range col (dec col2) -1)))
        selected-rows    (subvec rows row (inc row2))]
    (mapv (fn [row]
            (into (sorted-map-by (fn [header1 header2]
                                   (< (.indexOf selected-headers header1)
                                      (.indexOf selected-headers header2))))
                  (select-keys row selected-headers)))
          selected-rows)))

(defn selected-maps
  [{:keys [::db/headers ::db/rows ::db/selections]} _]
  (mapv #(selected-map headers rows %) selections))
(rf/reg-sub :selected-maps
            (fn [_ _]
              {::db/headers    (rf/subscribe [:table-headers])
               ::db/rows       (rf/subscribe [:table-rows])
               ::db/selections (rf/subscribe [:selections])})
            selected-maps)
