(ns inferenceql.spreadsheets.components.highlight.subs
  (:require [clojure.spec.alpha :as s]
            [re-frame.core :as rf]
            [medley.core :as medley]
            [inferenceql.spreadsheets.components.highlight.db :as db]))

;;; Specs related to subscriptions for missing cell values

(s/def ::values-map-for-row (s/map-of ::db/column-name ::db/value))
(s/def ::missing-cells-values (s/coll-of ::values-map-for-row))

(rf/reg-sub
  :row-likelihoods-normed
  (fn [db _]
    (get-in db [:highlight-component :row-likelihoods])))

(rf/reg-sub
  ;; Returns values and scores of missing-cells.
  :missing-cells
  (fn [db _]
    (get-in db [:highlight-component :missing-cells])))

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
   {:post [(s/valid? ::missing-cells-values %)]}
   (for [row missing-cells-flagged]
     (->> row
          (medley/filter-vals :meets-threshold)
          (medley/map-vals :value)))))
