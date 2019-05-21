(ns inferdb.spreadsheets.subs
  (:require [clojure.walk :as walk]
            [re-frame.core :as rf]
            [inferdb.spreadsheets.db :as db]
            [inferdb.spreadsheets.views :as views]
            [inferdb.cgpm.main :as cgpm]
            [inferdb.spreadsheets.model :as model]
            [metaprob.distributions :as dist]))

(rf/reg-sub :scores
            (fn [db _]
              (db/scores db)))

(defn table-headers
  [db _]
  (db/table-headers db))
(rf/reg-sub :table-headers table-headers)

(rf/reg-sub :selected-row-index
            (fn [db _]
              (db/selected-row-index db)))

(rf/reg-sub :selected-row
            (fn [_ _]
              {:computed-rows (rf/subscribe [:computed-rows])
               :selected-row-index (rf/subscribe [:selected-row-index])})
            (fn [{:keys [selected-row-index computed-rows]} _]
              (js/console.log "computed-rows" (take 10 computed-rows))
              (js/console.log "reloading selected row")
              (when selected-row-index
                (nth computed-rows selected-row-index))))

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
              (js/console.log "reloading computed rows")
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
  (js/console.log "reloading vega-lite")
  (when-let [selection (first selections)]
    (clj->js
     (cond (and (= 1 (count selected-columns))
                (= 1 (count (first selections)))
                (not (contains? #{"geo_fips" "district_name" "score"}
                                (first selected-columns))))
           (let [selected-row-kw (walk/keywordize-keys selected-row)
                 selected-column-kw (keyword (first selected-columns))
                 values (cgpm/cgpm-simulate model/census-cgpm
                                            [selected-column-kw]
                                            (reduce-kv (fn [acc k v]
                                                         (cond-> acc
                                                           v (assoc k v)))
                                                       {}
                                                       (dissoc selected-row-kw
                                                               selected-column-kw
                                                               :score
                                                               :NAME
                                                               :geo_fips))
                                            {}
                                            100)]
             {:$schema
              "https://vega.github.io/schema/vega-lite/v3.json"
              :data {:values values}
              :layer [{:mark "bar"
                       :encoding {:x {:bin true
                                      :field selected-column-kw
                                      :type "quantitative"}
                                  :y {:aggregate "count"
                                      :type "quantitative"
                                      :axis {:title "distribution of probable values"}}}}
                      {:data {:values [{selected-column-kw (-> selected-row (get (first selected-columns)))
                                        :label "Selected row"}]}
                       :mark {:type "rule"
                              :color "red"}
                       :encoding {:x {:field selected-column-kw
                                      :type "quantitative"}}}]})

           (= 1 (count selected-columns))
           (let [selected-column (first selected-columns)]
             {:$schema
              "https://vega.github.io/schema/vega-lite/v3.json",
              :data {:values selection},
              :mark "bar",
              :encoding
              (condp = (get model/stattypes selected-column)
                dist/gaussian
                {:x {:bin true,
                     :field selected-column
                     :type "quantitative"}
                 :y {:aggregate "count"
                     :type "quantitative"}}

                dist/categorical
                {:x {:field selected-column
                     :type "ordinal"}
                 :y {:aggregate "count"
                     :type "quantitative"}
                 :color {:field selected-column}})})

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
            :mark "circle"
            :encoding
            (reduce (fn [acc [k field]]
                      (assoc acc k {:field field, :type "quantitative"}))
                    {}
                    (map vector
                         [:x :y]
                         (take 2 selected-columns)))}))))
(rf/reg-sub :vega-lite-spec
            (fn [_ _]
              {:selected-row     (rf/subscribe [:selected-row])
               :selections       (rf/subscribe [:selections])
               :selected-columns (rf/subscribe [:selected-columns])})
            vega-lite-spec)

(rf/reg-sub :whole-db
            (fn [db]
              db))
