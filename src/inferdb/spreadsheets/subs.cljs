(ns inferdb.spreadsheets.subs
  (:require [clojure.walk :as walk]
            [re-frame.core :as rf]
            [inferdb.spreadsheets.db :as db]
            [inferdb.spreadsheets.views :as views]
            [metaprob.examples.cgpm :as cgpm]
            [metaprob.examples.nyt :as nyt]))

(rf/reg-sub :scores
            (fn [db _]
              (db/scores db)))

(defn table-headers
  [db _]
  (db/table-headers db))
(rf/reg-sub :table-headers table-headers)

(rf/reg-sub :selected-row
            (fn [db _]
              (db/selected-row db)))

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

(def ^:private topojson-feature "cb_2017_us_cd115_20m")

(defn- left-pad
  [s n c]
  (str (apply str (repeat (max 0 (- n (count s)))
                          c))
       s))

(defn vega-lite-spec
  [{:keys [selected-row selections selected-columns]}]
  (when-let [selection (first selections)]
    (clj->js
     (cond (and (= 1 (count selected-columns))
                (= 1 (count (keys (first selection)))))
           (let [selected-row-kw (walk/keywordize-keys selected-row)
                 selected-column-kw (keyword (first selected-columns))
                 values (cgpm/cgpm-simulate nyt/census-cgpm
                                            [selected-column-kw]
                                            (dissoc selected-row-kw
                                                    selected-column-kw
                                                    :district_name
                                                    :geo_fips)
                                            {} ; no arguments
                                            200)]
             {:$schema
              "https://vega.github.io/schema/vega-lite/v3.json"
              :data {:values values}
              :mark "bar"
              :encoding {:x {:bin true
                             :field selected-column-kw
                             :type "quantitative"}
                         :y {:aggregate "count"
                             :type "quantitative"
                             :axis {:title "Distribution of probable values"}}}})

           (= 1 (count selected-columns))
           {:$schema
            "https://vega.github.io/schema/vega-lite/v3.json",
            :data {:values selection},
            :mark "bar",
            :encoding
            {:x {:bin true,
                 :field (first selected-columns),
                 :type "quantitative"},
             :y {:aggregate "count", :type "quantitative"}}}

           (some #{"geo_fips"} selected-columns)
           (let [map-column (first (filter #(not= "geo_fips" %) selected-columns))
                 transformed-selection (mapv (fn [row]
                                               (update row "geo_fips" #(left-pad (str %) 4 \0)))
                                             selection)
                 name {:field "NAME"
                       :type "nominal"}
                 color {:field map-column
                        :type "quantitative"}]
             {:$schema "https://vega.github.io/schema/vega-lite/v3.json"
              :width 500
              :height 300
              :data {:values js/topojson
                     :format {:type "topojson"
                              :feature topojson-feature}}
              :transform [{:lookup "properties.GEOID"
                           :from {:data {:values transformed-selection}
                                  :key "geo_fips"
                                  "fields" [(:field name) (:field color)]}}]
              :projection {:type "albersUsa"}
              :mark "geoshape"
              :encoding {:tooltip [name color]
                         :color color}})

           :else
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
              {:selected-row (rf/subscribe [:selected-row])
               :selections (rf/subscribe [:selections])
               :selected-columns (rf/subscribe [:selected-columns])})
            vega-lite-spec)

(rf/reg-sub :whole-db
            (fn [db]
              db))
