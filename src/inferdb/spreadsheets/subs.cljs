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

(rf/reg-sub :row-at-selection-start
            (fn [db _]
              (db/row-at-selection-start db)))

(rf/reg-sub :selected-row-index
            (fn [db _]
              (db/selected-row-index db)))

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

(defn stattype
  [column]
  (if (= "score" column)
    dist/gaussian
    (get model/stattypes column)))

(defn vega-lite-spec
  [{:keys [selections selected-columns row-at-selection-start]}]
  (when-let [selection (first selections)]
    (clj->js
     (cond (and (= 1 (count selected-columns))
                (= 1 (count (first selections)))
                (not (contains? #{"geo_fips" "district_name" "score"}
                                (first selected-columns))))
           (let [selected-row-kw (walk/keywordize-keys row-at-selection-start)
                 selected-column-kw (keyword (first selected-columns))
                 y-axis {:title "distribution of probable values"
                         :grid false
                         :labels false
                         :ticks false}]
             {:$schema
              "https://vega.github.io/schema/vega-lite/v3.json"
              :data {:name "data"}
              :autosize {:resize true}
              :layer (cond-> [{:mark "bar"
                               :encoding (condp = (stattype (first selected-columns))
                                           dist/gaussian {:x {:bin true
                                                              :field selected-column-kw
                                                              :type "quantitative"}
                                                          :y {:aggregate "count"
                                                              :type "quantitative"
                                                              :axis y-axis}}
                                           dist/categorical {:x {:field selected-column-kw
                                                                 :type "nominal"}
                                                             :y {:aggregate "count"
                                                                 :type "quantitative"
                                                                 :axis y-axis}})}]
                       (get row-at-selection-start (first selected-columns))
                       (conj {:data {:values [{selected-column-kw (-> row-at-selection-start (get (first selected-columns)))
                                               :label "Selected row"}]}
                              :mark {:type "rule"
                                     :color "red"}
                              :encoding {:x {:field selected-column-kw
                                             :type (condp = (stattype (first selected-columns))
                                                     dist/gaussian "quantitative"
                                                     dist/categorical "nominal")}}}))})

           (= 1 (count selected-columns))
           (let [selected-column (first selected-columns)]
             {:$schema
              "https://vega.github.io/schema/vega-lite/v3.json",
              :data {:values selection},
              :mark "bar"
              :encoding
              (condp = (stattype selected-column)
                dist/gaussian {:x {:bin true,
                                   :field selected-column
                                   :type "quantitative"}
                               :y {:aggregate "count"
                                   :type "quantitative"}}

                dist/categorical {:x {:field selected-column
                                      :type "nominal"}
                                  :y {:aggregate "count"
                                      :type "quantitative"}}

                nil
                {})})

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
           (let [types (into #{}
                             (map stattype)
                             (take 2 selected-columns))]
             (condp = types
               #{dist/gaussian} {:$schema
                                 "https://vega.github.io/schema/vega-lite/v3.json"
                                 :data {:values selection}
                                 :mark "circle"
                                 :encoding {:x {:field (first selected-columns)
                                                :type "quantitative"}
                                            :y {:field (second selected-columns)
                                                :type "quantitative"}}}
               #{dist/categorical} {:$schema
                                    "https://vega.github.io/schema/vega-lite/v3.json"
                                    :data {:values selection}
                                    :mark "rect"
                                    :encoding {:x {:field (first selected-columns)
                                                   :type "nominal"}
                                               :y {:field (second selected-columns)
                                                   :type "nominal"}
                                               :color {:aggregate "count"
                                                       :type "quantitative"}}}
               #{dist/gaussian
                 dist/categorical} {:$schema
                                    "https://vega.github.io/schema/vega-lite/v3.json"
                                    :data {:values selection}
                                    :mark {:type "boxplot"
                                           :extent "min-max"}
                                    :encoding {:x {:field (first selected-columns)
                                                   :type (condp = (stattype (first selected-columns))
                                                           dist/gaussian "quantitative"
                                                           dist/categorical "nominal")}
                                               :y {:field (second selected-columns)
                                                   :type (condp = (stattype (second selected-columns))
                                                           dist/gaussian "quantitative"
                                                           dist/categorical "nominal")}
                                               :color {:aggregate "count"
                                                       :type "quantitative"}}}
               {}))))))
(rf/reg-sub :vega-lite-spec
            (fn [_ _]
              {:selections             (rf/subscribe [:selections])
               :selected-columns       (rf/subscribe [:selected-columns])
               :row-at-selection-start (rf/subscribe [:row-at-selection-start])})
            vega-lite-spec)

(rf/reg-sub :one-cell-selected
            (fn [_ _]
              {:selections (rf/subscribe [:selections])})
            (fn [{:keys [selections]}]
              (= 1
                 (count selections)
                 (count (first selections))
                 (count (keys (first selections))))))

(rf/reg-sub :generator
            (fn [_ _]
              {:row (rf/subscribe [:row-at-selection-start])
               :columns (rf/subscribe [:selected-columns])
               :one-cell-selected (rf/subscribe [:one-cell-selected])})
            (fn [{:keys [row columns one-cell-selected]}]
              (when one-cell-selected
                (let [sampled-column (first columns) ; columns that will be sampled
                      constraints (reduce-kv (fn [acc k v]
                                               (cond-> acc
                                                 v (assoc k v)))
                                             {}
                                             (-> row
                                                 (select-keys (keys model/stattypes))
                                                 (dissoc sampled-column)
                                                 (walk/keywordize-keys)))]
                  #(cgpm/cgpm-simulate model/census-cgpm
                                       [(keyword sampled-column)]
                                       constraints
                                       {}
                                       1)))))
