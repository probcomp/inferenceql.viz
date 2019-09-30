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

(rf/reg-sub :labels
            (fn [db _]
              (db/labels db)))

(defn table-headers
  [db _]
  (db/table-headers db))
(rf/reg-sub :table-headers table-headers)

(rf/reg-sub :table-last-clicked
            (fn [db _]
              (get db ::db/table-last-clicked)))

(rf/reg-sub :other-table
            (fn [db [_sub-name table-picked]]
              (let [[table-1-id table-2-id] (keys (get db ::db/hot-state))]
                (condp = table-picked
                  table-1-id table-2-id
                  table-2-id table-1-id))))

(rf/reg-sub :table-state
            (fn [db [_sub-name table-id]]
              (get-in db [::db/hot-state table-id])))

(rf/reg-sub :both-table-states
            (fn [db [_sub-name]]
              (get db ::db/hot-state)))

(rf/reg-sub-raw :table-state-active
                (fn [app-db event]
                  (reaction
                    (let [table-id @(rf/subscribe [:table-last-clicked])
                          table-state @(rf/subscribe [:table-state table-id])]
                      table-state))))

(rf/reg-sub :computed-headers
            (fn [_ _]
              (rf/subscribe [:table-headers]))
            (fn [headers]
              (into [vega/label-col-header vega/score-col-header] headers)))

(rf/reg-sub :computed-rows
            (fn [_ _]
              {:rows (rf/subscribe [:table-rows])
               :scores (rf/subscribe [:scores])
               :labels (rf/subscribe [:labels])})
            (fn [{:keys [rows scores labels]}]
              (cond->> rows
                scores (mapv (fn [score row]
                               (assoc row vega/score-col-header score))
                             scores)
                labels (mapv (fn [label row]
                               (assoc row vega/label-col-header label))
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

(def clean-label
  "Prepares the user-typed label for checking."
  (fnil (comp str/upper-case str/trim) ""))

(defn- pos-label? [label-str]
  (let [f (clean-label label-str)]
    ;; TODO: add more truthy values
    (or (= f "TRUE")
        (= f "1"))))

(defn- neg-label? [label-str]
  (let [f (clean-label label-str)]
    ;; TODO: add more falsey values
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
  [{:keys [table-states t-clicked]}]
  (let [{selections :selections cols :selected-columns row :row-at-selection-start}
        (table-states t-clicked)]
    (when (first selections)
      (clj->js
       (cond (and (= 1 (count cols))
                  (= 1 (count (first selections)))
                  (not (contains? #{"geo_fips" "NAME" vega/label-col-header vega/score-col-header}
                                  (first cols))))
             (vega/gen-simulate-plot cols row t-clicked)

             (= 1 (count cols))
             (vega/gen-histogram table-states t-clicked)

             (some #{"geo_fips"} cols)
             (vega/gen-choropleth selections cols)

             :else
             (vega/gen-comparison-plot table-states t-clicked))))))
(rf/reg-sub :vega-lite-spec
            (fn [_ _]
              {:table-states (rf/subscribe [:both-table-states])
               :t-clicked (rf/subscribe [:table-last-clicked])})
            (fn [data-for-spec]
              (vega-lite-spec data-for-spec)))

(rf/reg-sub :one-cell-selected
            (fn [_ _]
              (rf/subscribe [:table-state-active]))
            (fn [{:keys [selections]}]
              (= 1
                 (count selections)
                 (count (first selections))
                 (count (keys (first selections))))))

(rf/reg-sub :generator
            (fn [_ _]
              {:selection-info (rf/subscribe [:table-state-active])
               :one-cell-selected (rf/subscribe [:one-cell-selected])})
            (fn [{:keys [selection-info one-cell-selected]}]
              (let [row (:row-at-selection-start selection-info)
                    columns (:selected-columns selection-info)
                    col-to-sample (first columns)]
                (when (and one-cell-selected
                           ;; TODO clean up this check
                           (not (contains? #{"geo_fips" "NAME" vega/score-col-header vega/label-col-header} col-to-sample)))
                  (let [constraints (mmix/with-row-values {} (-> row
                                                                 (select-keys (keys (:vars model/spec)))
                                                                 (dissoc col-to-sample)))
                        gen-fn #(first (mp/infer-and-score :procedure (search/optimized-row-generator model/spec)
                                                           :observation-trace constraints))
                        negative-salary? #(neg? (% "salary_usd"))]
                    ;; returns the first result of gen-fn that doesn't have a negative salary
                    ;; TODO: This is dataset-specific
                    #(take 1 (remove negative-salary? (repeatedly gen-fn))))))))
