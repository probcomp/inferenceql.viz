(ns inferdb.spreadsheets.subs
  (:require [re-frame.core :as rf]
            [inferdb.spreadsheets.db :as db]
            [inferdb.spreadsheets.views :as views]))

(rf/reg-sub :scores
            (fn [db _]
              (db/scores db)))

(defn table-headers
  [db _]
  (db/table-headers db))
(rf/reg-sub :table-headers table-headers)

(rf/reg-sub :computed-headers
            (fn [_ _]
              (rf/subscribe [:table-headers]))
            (fn [headers]
              (into ["score"] headers)))

(rf/reg-sub :computed-rows
            (fn [_ _]
              {:rows (rf/subscribe [:table-rows])
               :scores (rf/subscribe [:scores])})
            (fn [{:keys [rows scores]}]
              (cond->> rows
                scores (mapv (fn [score row]
                               (assoc row "score" score))
                             scores))))

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
  [{:keys [headers rows]} _]
  (let [data (cell-vector headers rows)]
    (-> views/default-hot-settings
        (assoc-in [:settings :data] data)
        (assoc-in [:settings :colHeaders] headers))))
(rf/reg-sub :hot-props
            (fn [_ _]
              {:headers (rf/subscribe [:computed-headers])
               :rows    (rf/subscribe [:computed-rows])})
            hot-props)

(defn selections
  [db _]
  (db/selections db))
(rf/reg-sub :selections selections)

(defn selected-columns
  [db _]
  (db/selected-columns db))
(rf/reg-sub :selected-columns selected-columns)

(defn vega-lite-spec
  [{:keys [selections selected-columns]}]
  (when-let [selection (first selections)]
    (clj->js
     (if (= 1 (count selected-columns))
       {:$schema
        "https://vega.github.io/schema/vega-lite/v3.json",
        :data {:values selection},
        :mark "bar",
        :encoding
        {:x {:bin true,
             :field (first selected-columns),
             :type "quantitative"},
         :y {:aggregate "count", :type "quantitative"}}}
       {:$schema
        "https://vega.github.io/schema/vega-lite/v3.json"
        :data {:values selection}
        :mark "point"
        :encoding
        (reduce (fn [acc [k field]]
                  (assoc acc k {:field field, :type "quantitative"}))
                {}
                (map vector
                     [:x :y]
                     (take 2 selected-columns)))}))))
(rf/reg-sub :vega-lite-spec
            (fn [_ _]
              {:selections (rf/subscribe [:selections])
               :selected-columns (rf/subscribe [:selected-columns])})
            vega-lite-spec)
