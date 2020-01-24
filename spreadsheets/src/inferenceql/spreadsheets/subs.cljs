(ns inferenceql.spreadsheets.subs
  (:require [clojure.string :as str]
            [clojure.spec.alpha :as s]
            [re-frame.core :as rf]
            [metaprob.prelude :as mp]
            [inferenceql.spreadsheets.db :as db]
            [inferenceql.multimixture :as mmix]
            [inferenceql.multimixture.search :as search]
            [inferenceql.spreadsheets.model :as model]
            [inferenceql.spreadsheets.modal :as modal]
            [inferenceql.spreadsheets.column-overrides :as co]
            [medley.core :as medley])
  (:require-macros [reagent.ratom :refer [reaction]]))

;;; Specs related to subscriptions for missing cell values

(s/def :ms/values-map-for-row (s/map-of ::db/column-name :ms/value))
(s/def :ms/missing-cells-values (s/coll-of :ms/values-map-for-row))

(rf/reg-sub :scores
            (fn [db _]
              (db/scores db)))

(rf/reg-sub :virtual-scores
            (fn [db _]
              (db/virtual-scores db)))


(rf/reg-sub :modal
            (fn [db _]
              (::db/modal db)))

(rf/reg-sub :column-override-fns
            (fn [db _]
              (get db ::db/column-override-fns)))

(rf/reg-sub :column-overrides
            (fn [db _]
              (get db ::db/column-overrides)))

(rf/reg-sub
  :row-likelihoods-normed
  (fn [db _]
    (get db ::db/row-likelihoods)))

(rf/reg-sub
  ;; Returns values and scores of missing-cells.
  :missing-cells
  (fn [db _]
    (get db ::db/missing-cells)))

(rf/reg-sub
 :missing-cells-flagged
 ;; This is like the :missing-cells sub, but it includes an extra flag for whether the
 ;; imputed value for each cell meets the set confidence threshold.
 :<- [:missing-cells]
 :<- [:confidence-threshold]
 (fn [[missing-cells confidence-threshold] _]
   ;; validate output using spec
   {:post [(s/valid? ::db/missing-cells %)]}
   (let [add-threshold-flags (fn [val-and-score-map]
                               (let [meets-threshold (>= (:score val-and-score-map) confidence-threshold)]
                                 (assoc val-and-score-map :meets-threshold meets-threshold)))
         ;; Missing cell info with :meets-threshold flags added.
         with-flags (map (fn [row] (medley/map-vals add-threshold-flags row))
                         missing-cells)]
     (vec with-flags))))

(rf/reg-sub
 :missing-cells-vals-above-thresh
 ;; This is like the :missing-cells sub, but it only includes cells that have meet the confidence
 ;; threshold. And it only has missing cells values, no score info.
 :<- [:missing-cells-flagged]
 (fn [missing-cells-flagged _]
   ;; validate output using spec
   {:post [(s/valid? :ms/missing-cells-values %)]}
   (for [row missing-cells-flagged]
     (->> row
          (medley/filter-vals :meets-threshold)
          (medley/map-vals :value)))))
