(ns inferdb.spreadsheets.subs
  (:require [clojure.walk :as walk]
            [clojure.string :as str]
            [re-frame.core :as rf]
            [metaprob.distributions :as dist]
            [metaprob.prelude :as mp]
            [inferdb.spreadsheets.db :as db]
            [inferdb.spreadsheets.views :as views]
            [inferdb.multimixture :as mmix]
            [inferdb.multimixture.search :as search]
            [inferdb.spreadsheets.model :as model]))

(rf/reg-sub :scores
            (fn [db _]
              (db/scores db)))

(rf/reg-sub :labels
            (fn [db _]
              (db/labels db)))

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
              (into ["🏷" "probability"] headers)))

(rf/reg-sub :computed-rows
            (fn [_ _]
              {:rows (rf/subscribe [:table-rows])
               :scores (rf/subscribe [:scores])
               :labels (rf/subscribe [:labels])})
            (fn [{:keys [rows scores labels]}]
              (cond->> rows
                scores (mapv (fn [score row]
                               (assoc row "probability" score))
                             scores)
                labels (mapv (fn [label row]
                               (assoc row "🏷" label))
                             labels))))

(rf/reg-sub :virtual-rows
            (fn [db _]
              (db/virtual-rows db)))

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

(defn real-hot-props
  [{:keys [headers rows]} _]
  (let [data (cell-vector headers rows)
        num-columns (count headers)

        initial-column-setting {:readOnly false}
        rem-column-settings (repeat (dec num-columns) {})
        all-column-settings (cons initial-column-setting rem-column-settings)]

    (-> views/real-hot-settings
        (assoc-in [:settings :data] data)
        (assoc-in [:settings :colHeaders] headers)
        (assoc-in [:settings :columns] all-column-settings))))
(rf/reg-sub :real-hot-props
            (fn [_ _]
              {:headers (rf/subscribe [:computed-headers])
               :rows    (rf/subscribe [:computed-rows])})
            real-hot-props)

(defn virtual-hot-props
  [{:keys [headers rows]} _]
  (let [data (cell-vector headers rows)
        num-columns (count headers)
        column-settings (repeat num-columns {})]
    (-> views/virtual-hot-settings
        (assoc-in [:settings :data] data)
        (assoc-in [:settings :colHeaders] headers)
        (assoc-in [:settings :columns] column-settings))))
(rf/reg-sub :virtual-hot-props
            (fn [_ _]
              {:headers (rf/subscribe [:computed-headers])
               :rows    (rf/subscribe [:virtual-rows])})
            virtual-hot-props)

(defn selections
  [db _]
  (db/selections db))
(rf/reg-sub :selections selections)

(defn selected-columns
  [db _]
  (db/selected-columns db))
(rf/reg-sub :selected-columns selected-columns)

;; TODO helper functions might be better located elsewhere
(def clean-label
  (fnil (comp str/upper-case str/trim) ""))
(defn- pos-label? [label-str]
  (let [f (clean-label label-str)]
    (or (= f "TRUE")
        (= f "1"))))
(defn- neg-label? [label-str]
  (let [f (clean-label label-str)]
    (or (= f "FALSE")
        (= f "0"))))

(defn rows-labeled-pos
  [{:keys [labels rows]} _]
  (let [rows-with-ids (map vector (range) rows)
        pos-rows (map (fn [label row]
                        (if (pos-label? label) row))
                      labels rows-with-ids)
        pos-rows (remove nil? pos-rows)]
    pos-rows))
(rf/reg-sub :rows-labeled-pos
            (fn [_ _]
              {:labels (rf/subscribe [:labels])
               :rows (rf/subscribe [:table-rows])})
            rows-labeled-pos)

(defn rows-labeled-neg
  [{:keys [labels rows]} _]
  (let [rows-with-ids (map vector (range) rows)
        neg-rows (map (fn [label row]
                        (if (neg-label? label) row))
                      labels rows-with-ids)
        neg-rows (remove nil? neg-rows)]
    neg-rows))
(rf/reg-sub :rows-labeled-neg
            (fn [_ _]
              {:labels (rf/subscribe [:labels])
               :rows (rf/subscribe [:table-rows])})
            rows-labeled-neg)

(defn rows-not-labeled
  [{:keys [labels rows]} _]
  (let [rows-with-ids (map vector (range) rows)
        unlabeled-rows (map (fn [label row]
                               (if (and (not (pos-label? label))
                                        (not (neg-label? label)))
                                 row))
                            labels rows-with-ids)
        unlabeled-rows (remove nil? unlabeled-rows)]
    unlabeled-rows))
(rf/reg-sub :rows-not-labeled
            (fn [_ _]
              {:labels (rf/subscribe [:labels])
               :rows (rf/subscribe [:table-rows])})
            rows-not-labeled)

(def ^:private topojson-feature "cb_2017_us_cd115_20m")

(defn- left-pad
  [s n c]
  (str (apply str (repeat (max 0 (- n (count s)))
                          c))
       s))

(defn stattype
  [column]
  (let [stattype-kw (if (or (= "probability" column) (= "🏷" column))
                      :gaussian
                      (get-in model/spec [:vars column]))]
    (case stattype-kw
      :gaussian dist/gaussian
      :categorical dist/categorical)))

(defn vega-lite-spec
  [{:keys [selections selected-columns row-at-selection-start]}]
  (when-let [selection (first selections)]
    (clj->js
     (cond (and (= 1 (count selected-columns))
                (= 1 (count (first selections)))
                (not (contains? #{"geo_fips" "NAME" "probability" "🏷"}
                                (first selected-columns))))
           ;; Simulate plot
           (do
             (.log js/console "vega: Simulate Plot")
             (let [selected-row-kw (walk/keywordize-keys row-at-selection-start)
                   selected-column-kw (keyword (first selected-columns))
                   y-axis {:title "distribution of probable values"
                           :grid false
                           :labels false
                           :ticks false}
                   y-scale {:nice false}]
               {:$schema
                "https://vega.github.io/schema/vega-lite/v3.json"
                :width 400
                :height 400
                :data {:name "data"}
                :autosize {:resize true}
                :layer (cond-> [{:mark "bar"
                                 :encoding (condp = (stattype (first selected-columns))
                                             dist/gaussian {:x {:bin true
                                                                :field selected-column-kw
                                                                :type "quantitative"}
                                                            :y {:aggregate "count"
                                                                :type "quantitative"
                                                                :axis y-axis
                                                                :scale y-scale}}
                                             dist/categorical {:x {:field selected-column-kw
                                                                   :type "nominal"}
                                                               :y {:aggregate "count"
                                                                   :type "quantitative"
                                                                   :axis y-axis
                                                                   :scale y-scale}})}]
                         (get row-at-selection-start (first selected-columns))
                         (conj {:data {:values [{selected-column-kw (-> row-at-selection-start (get (first selected-columns)))
                                                 :label "Selected row"}]}
                                :mark {:type "rule"
                                       :color "red"}
                                :encoding {:x {:field selected-column-kw
                                               :type (condp = (stattype (first selected-columns))
                                                       dist/gaussian "quantitative"
                                                       dist/categorical "nominal")}}}))}))

           (= 1 (count selected-columns))
           ;; Histogram
           (do
             (.log js/console "vega: Histogram")
             (let [selected-column (first selected-columns)]
               {:$schema
                "https://vega.github.io/schema/vega-lite/v3.json",
                :width 400
                :height 400
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
                  {})}))

           (some #{"geo_fips"} selected-columns)
           ;; Choropleth
           (do
             (.log js/console "vega: Choropleth")
             (let [map-column (first (filter #(not= "geo_fips" %) selected-columns))
                   transformed-selection (mapv (fn [row]
                                                 (update row "geo_fips" #(left-pad (str %) 4 \0)))
                                               selection)
                   name {:field "NAME"
                         :type "nominal"}
                   color {:field map-column
                          :type (condp = (stattype map-column)
                                  dist/gaussian "quantitative"
                                  dist/categorical "nominal")}]
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
                           :color color}}))

           :else
           ;; Comparison plot
           (do
             (.log js/console "vega: Comparison plot")
             (let [types (into #{}
                               (map stattype)
                               (take 2 selected-columns))]
               (condp = types
                 ;; Scatterplot
                 #{dist/gaussian} {:$schema
                                   "https://vega.github.io/schema/vega-lite/v3.json"
                                   :width 400
                                   :height 400
                                   :data {:values selection}
                                   :mark "circle"
                                   :encoding {:x {:field (first selected-columns)
                                                  :type "quantitative"}
                                              :y {:field (second selected-columns)
                                                  :type "quantitative"}}}
                 ;; Heatmap
                 #{dist/categorical} {:$schema
                                      "https://vega.github.io/schema/vega-lite/v3.json"
                                      :width 400
                                      :height 400
                                      :data {:values selection}
                                      :mark "rect"
                                      :encoding {:x {:field (first selected-columns)
                                                     :type "nominal"}
                                                 :y {:field (second selected-columns)
                                                     :type "nominal"}
                                                 :color {:aggregate "count"
                                                         :type "quantitative"}}}
                 ;; Bot-and-line
                 #{dist/gaussian
                   dist/categorical} {:$schema
                                      "https://vega.github.io/schema/vega-lite/v3.json"
                                      :width 400
                                      :height 400
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
                 {})))))))
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
              (when (and one-cell-selected
                         ;; TODO clean up this check
                         (not (contains? #{"geo_fips" "NAME" "probability" "🏷"} (first columns))))
                (let [sampled-column (first columns) ; columns that will be sampled
                      constraints (mmix/with-row-values {} (-> row
                                                               (select-keys (keys (:vars model/spec)))
                                                               (dissoc sampled-column)))
                      gen-fn #(first (mp/infer-and-score :procedure (search/optimized-row-generator model/spec)
                                                         :observation-trace constraints))
                      negative-salary? #(< (% "salary_usd") 0)]
                  ;; returns the first result of gen-fn that doesn't have a negative salary
                  #(take 1 (remove negative-salary? (repeatedly gen-fn)))))))
