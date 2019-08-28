(ns inferdb.spreadsheets.subs
  (:require [clojure.string :as str]
            [re-frame.core :as rf]
            [metaprob.distributions :as dist]
            [metaprob.prelude :as mp]
            [inferdb.spreadsheets.db :as db]
            [inferdb.spreadsheets.views :as views]
            [inferdb.multimixture :as mmix]
            [inferdb.multimixture.search :as search]
            [inferdb.spreadsheets.model :as model]
            [inferdb.spreadsheets.vega :as vega])
  (:require-macros [reagent.ratom :refer [reaction]]))

(rf/reg-sub :scores
            (fn [db _]
              (db/scores db)))

(rf/reg-sub :virtual-scores
            (fn [db _]
              (db/virtual-scores db)))

(rf/reg-sub :labels
            (fn [db _]
              (db/labels db)))

(defn table-headers
  [db _]
  (db/table-headers db))
(rf/reg-sub :table-headers table-headers)


(rf/reg-sub :row-at-selection-start
            (fn [db [_sub-name table-id]]
              (db/row-at-selection-start db table-id)))

(rf/reg-sub :selected-row-index
            (fn [db [_sub-name table-id]]
              (db/selected-row-index db table-id)))

(rf/reg-sub :selections
            (fn [db [_sub-name table-id]]
              (db/selections db table-id)))

(rf/reg-sub :selected-columns
            (fn [db [_sub-name table-id]]
              (db/selected-columns db table-id)))

(rf/reg-sub :table-last-selected
            (fn [db _]
              (db/table-last-selected db)))

(rf/reg-sub-raw :selections-activated
                (fn [app-db event]
                  (reaction
                    (let [table-id @(rf/subscribe [:table-last-selected])
                          selections @(rf/subscribe [:selections table-id])]
                      selections))))

(rf/reg-sub-raw :selected-columns-activated
                (fn [app-db event]
                  (reaction
                    (let [table-id @(rf/subscribe [:table-last-selected])
                          selected-columns @(rf/subscribe [:selected-columns table-id])]
                      selected-columns))))

(rf/reg-sub-raw :row-at-selection-start-activated
                (fn [app-db event]
                  (reaction
                    (let [table-id @(rf/subscribe [:table-last-selected])
                          row-at-selection-start @(rf/subscribe [:row-at-selection-start table-id])]
                      row-at-selection-start))))

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

(rf/reg-sub :virtual-computed-rows
  (fn [_ _]
    {:rows (rf/subscribe [:virtual-rows])
     :scores (rf/subscribe [:virtual-scores])})
  (fn [{:keys [rows scores]}]
    (let [num-missing-scores (- (count rows) (count scores))
          dummy-scores (repeat num-missing-scores nil)
          scores (concat dummy-scores scores)]

      ;; Creation of dummy scores allows correct attaching of old scores to
      ;; rows even when new rows are generated after a scoring event.
      (mapv (fn [score row] (assoc row "probability" score))
            scores rows))))

(defn table-rows
  [db _]
  (db/table-rows db))
(rf/reg-sub :table-rows table-rows)

(defn virtual-rows
  [db _]
  (db/virtual-rows db))
(rf/reg-sub :virtual-rows virtual-rows)

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
               :rows    (rf/subscribe [:virtual-computed-rows])})
            virtual-hot-props)

(def clean-label
  (fnil (comp str/upper-case str/trim) ""))

(defn- pos-label? [label-str]
  (let [f (clean-label label-str)]
    ; TODO add more truthy values
    (or (= f "TRUE")
        (= f "1"))))

(defn- neg-label? [label-str]
  (let [f (clean-label label-str)]
    ; TODO add more falsey values
    (or (= f "FALSE")
        (= f "0"))))

(defn- unlabeled? [label-str]
  (and (not (pos-label? label-str))
       (not (neg-label? label-str))))

(defn row-ids-labeled-pos
  [{:keys [labels]} _]
  (let [labels-with-ids (map vector labels (range))
        ids (->> (filter #(pos-label? (first %)) labels-with-ids)
                 (map second))]
    ids))
(rf/reg-sub :row-ids-labeled-pos
            (fn [_ _]
              {:labels (rf/subscribe [:labels])})
            row-ids-labeled-pos)

(defn row-ids-labeled-neg
  [{:keys [labels]} _]
  (let [labels-with-ids (map vector labels (range))
        ids (->> (filter #(neg-label? (first %)) labels-with-ids)
                 (map second))]
    ids))
(rf/reg-sub :row-ids-labeled-neg
            (fn [_ _]
              {:labels (rf/subscribe [:labels])})
            row-ids-labeled-neg)

(defn row-ids-unlabeled
  [{:keys [labels]} _]
  (let [labels-with-ids (map vector labels (range))
        ids (->> (filter #(unlabeled? (first %)) labels-with-ids)
                 (map second))]
    ids))
(rf/reg-sub :row-ids-unlabeled
            (fn [_ _]
              {:labels (rf/subscribe [:labels])})
            row-ids-unlabeled)

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
           (vega/gen-simulate-plot selections selected-columns row-at-selection-start)

           (= 1 (count selected-columns))
           ;; Histogram
           (vega/gen-histogram selections selected-columns row-at-selection-start)

           (some #{"geo_fips"} selected-columns)
           ;; Choropleth
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
                         :color color}})

           :else
           ;; Comparison plot
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
               {}))))))
(rf/reg-sub :vega-lite-spec
            (fn [_ _]
              {:selections (rf/subscribe [:selections-activated])
               :selected-columns (rf/subscribe [:selected-columns-activated])
               :row-at-selection-start (rf/subscribe [:row-at-selection-start-activated])})
            (fn [data-for-spec]
              (vega-lite-spec data-for-spec)))

(rf/reg-sub :one-cell-selected
            (fn [_ _]
              {:selections (rf/subscribe [:selections-activated])})
            (fn [{:keys [selections]}]
              (= 1
                 (count selections)
                 (count (first selections))
                 (count (keys (first selections))))))

(rf/reg-sub :generator
            (fn [_ _]
              {:row (rf/subscribe [:row-at-selection-start-activated])
               :columns (rf/subscribe [:selected-columns-activated])
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
