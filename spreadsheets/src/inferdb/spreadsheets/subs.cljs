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

(rf/reg-sub :table-last-clicked
            (fn [db _]
              (db/table-last-clicked db)))

(rf/reg-sub :table-not-last-clicked
            (fn [db _]
              (db/table-not-last-clicked db)))

(rf/reg-sub :table-header-clicked
            (fn [db [_sub-name table-id]]
              (db/table-header-clicked db table-id)))

(rf/reg-sub-raw :selection-info-active
                (fn [app-db event]
                  (reaction
                    (let [table-id @(rf/subscribe [:table-last-clicked])
                          selections @(rf/subscribe [:selections table-id])
                          selected-columns @(rf/subscribe [:selected-columns table-id])
                          row-at-selection-start @(rf/subscribe [:row-at-selection-start table-id])]
                      {:selections selections
                       :selected-columns selected-columns
                       :row-at-selection-start row-at-selection-start}))))

(rf/reg-sub-raw :selection-info-inactive
                (fn [app-db event]
                  (reaction
                    (let [table-id @(rf/subscribe [:table-not-last-clicked])
                          selections @(rf/subscribe [:selections table-id])
                          selected-columns @(rf/subscribe [:selected-columns table-id])
                          row-at-selection-start @(rf/subscribe [:row-at-selection-start table-id])]
                      {:selections selections
                       :selected-columns selected-columns
                       :row-at-selection-start row-at-selection-start}))))

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

(defn vega-lite-spec
   [{:keys [s-info-active s-info-inactive t-clicked t-not-clicked]}]
  (let [{selects-1 :selections cols-1 :selected-columns row-1 :row-at-selection-start}
        s-info-active

        {selects-2 :selections cols-2 :selected-columns _row-2 :row-at-selection-start}
        s-info-inactive]
    (when (first selects-1)
      (clj->js
       (cond (and (= 1 (count cols-1))
                  (= 1 (count (first selects-1)))
                  (not (contains? #{"geo_fips" "NAME" "probability" "🏷"}
                                  (first cols-1))))
             (vega/gen-simulate-plot cols-1 row-1)

             (= 1 (count cols-1))
             (vega/gen-histogram selects-1 cols-1 selects-2 cols-2)

             (some #{"geo_fips"} cols-1)
             (vega/gen-choropleth selects-1 cols-1)

             :else
             (vega/gen-comparison-plot selects-1 cols-1 selects-2 cols-2))))))
(rf/reg-sub :vega-lite-spec
            (fn [_ _]
              {:s-info-active (rf/subscribe [:selection-info-active])
               :s-info-inactive (rf/subscribe [:selection-info-inactive])
               :t-clicked (rf/subscribe [:table-last-clicked])
               :t-not-clicked (rf/subscribe [:table-not-last-clicked])})
            (fn [data-for-spec]
              (vega-lite-spec data-for-spec)))

(rf/reg-sub :one-cell-selected
            (fn [_ _]
              (rf/subscribe [:selection-info-active]))
            (fn [{:keys [selections]}]
              (= 1
                 (count selections)
                 (count (first selections))
                 (count (keys (first selections))))))

(rf/reg-sub :generator
            (fn [_ _]
              {:selection-info (rf/subscribe [:selection-info-active])
               :one-cell-selected (rf/subscribe [:one-cell-selected])})
            (fn [{:keys [selection-info one-cell-selected]}]
              (let [row (:row-at-selection-start selection-info)
                    columns (:selected-columns selection-info)]
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
                    #(take 1 (remove negative-salary? (repeatedly gen-fn))))))))
