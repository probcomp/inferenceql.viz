(ns inferenceql.viz.score
  (:require [inferenceql.viz.util :as util]
            [inferenceql.inference.gpm.multimixture.search :as search]
            [inferenceql.inference.gpm.multimixture.utils :as mm.utils]
            [metaprob.prelude :as mp]
            [inferenceql.inference.gpm :as gpm]
            [medley.core :as medley]
            [clojure.set :as set]
            [clojure.spec.alpha :as s]))

;;; General specs for scoring functions.

(s/def ::score number?)

;;; Specs for row-wise scoring functions.

(s/def ::row-likelihoods (s/coll-of ::score))

;;; Specs for cell-wise scoring functions.

(s/def ::value any?)
(s/def ::value-score-map (s/keys :req-un [::value ::score]))

(s/def ::column-name string?)
(s/def ::map-for-row (s/map-of ::column-name ::value-score-map))

(s/def ::missing-vals-and-scores (s/coll-of :ms/map-for-row))

(def ^:private imputation-sample-size
  "The number of imputed values used to determine the value a missing cell."
  10)

(defn- min-max-normalizer-fn [nums]
  "Returns a function that rescales a number from `nums` to the range [0,1]."
  (let [min-n (apply min nums)
        max-n (apply max nums)
        scale-factor (util/abs (- max-n min-n))]
    #(/ (- % min-n)
        scale-factor)))

(defn min-max-normalize [nums]
  "Re-scales each item in `nums` to the range [0,1]."
  (let [normalizer (min-max-normalizer-fn nums)]
    (map normalizer nums)))

;;; These functions are for scoring the likelihood of entire rows.

(defn score-row-uncond
  "Scores the likelihood of `row`."
  [multi-mixture row]
  (let [target (util/filter-nil-kvs row)
        constraints {}]
    (Math/exp (gpm/logpdf (gpm/Multimixture multi-mixture) target constraints))))

(s/fdef row-likelihoods :ret ::row-likelihoods)
(defn row-likelihoods
  "Returns a sequence of normalized likelihoods for `rows`"
  [spec rows]
  (let [likelihoods (map #(score-row-uncond spec %) rows)
        distinct-vals-present (> (count (distinct likelihoods)) 1)]
    (if distinct-vals-present
      (min-max-normalize likelihoods)
      ;; Just return the raw likelihoods when we can't normalize
      likelihoods)))

;;; These functions are for scoring the likelihood of existing cells
;;; conditional on the rest of the cells in that row.

(defn score-cells-in-row
  "Scores the likelihood of each cell in `row`.
   It does this conditional on the other cells `row`."
  [multi-mixture row]
  (let [clean-row (util/filter-nil-kvs row)
        cell-pairs (seq clean-row)
        likelihood-pairs (for [[k v] cell-pairs]
                           (let [target (assoc {} k v)
                                 constraints (dissoc clean-row k)]
                             [k (Math/exp (gpm/logpdf (gpm/Multimixture multi-mixture) target constraints))]))]
    (into {} likelihood-pairs)))

(defn cell-likelihoods
  "Returns the likelihood of all the cells in `rows`"
  [spec rows]
  ;; TODO normalize scores before returning.
  (map #(score-cells-in-row spec %) rows))

;;; These functions are for imputing data in empty cells and
;;; also returning a score for the imputed values.

(s/fdef impute-and-score-cell :ret ::value-score-map)
(defn impute-and-score-cell
  "Returns an imputed value of `key-to-impute` along with its score"
  [row-gen row key-to-impute]
  (let [constraints (mm.utils/with-row-values {} row)
        gen-fn #(-> (mp/infer-and-score :procedure row-gen
                                        :observation-trace constraints)
                    (first)
                    (get key-to-impute))
        samples (repeatedly imputation-sample-size gen-fn)
        freq-list (sort-by val > (frequencies samples))
        [top-sample top-sample-count] (first freq-list)

        total-sample-count (count samples)
        top-sample-prob (/ top-sample-count total-sample-count)]
    {:value top-sample :score top-sample-prob}))

(s/fdef impute-missing-cells-in-row :ret ::map-for-row)
(defn impute-missing-cells-in-row
  "Returns a map of imputed values and scores for all missing values in `row`"
  [row-generator headers row]
  (let [clean-row (util/filter-nil-kvs row)
        available-keys (keys clean-row)
        missing-keys (set/difference (set headers) (set available-keys))
        values-scores (map #(impute-and-score-cell row-generator clean-row %) missing-keys)]
    (zipmap missing-keys values-scores)))

(s/fdef normalize-missing-cells-scores :ret ::missing-vals-and-scores)
(defn normalize-missing-cells-scores
  "Normalizes scores within each map in `values-and-scores-by-row`"
  [values-and-scores-by-row all-scores]
  (let [normalizer (min-max-normalizer-fn all-scores)
        norm-scores (fn [row-vals-scores]
                      (medley/map-vals (fn [val-score-map] (update val-score-map :score normalizer))
                                       row-vals-scores))]
    (map norm-scores values-and-scores-by-row)))

(s/fdef impute-missing-cells :ret ::missing-vals-and-scores)
(defn impute-missing-cells
  "Returns imputed values and normalized scores for all missing values in `rows`"
  [spec headers rows]
  (let [row-gen (search/optimized-row-generator spec)
        values-and-scores-by-row (map #(impute-missing-cells-in-row row-gen headers %) rows)

        all-scores (->> values-and-scores-by-row
                        (map vals) ;; Produces sequence of {:score _ :value _ } maps for each row.
                        (apply concat)  ;; Flattened sequence of {:score _ :value _ } maps.
                        (map :score))] ;; Sequence of score values.

    ;; Only normalize if distinct scores present.
    (if (> (count (distinct all-scores)) 1)
      (normalize-missing-cells-scores values-and-scores-by-row all-scores)
      ;; Just return the raw likelihoods otherwise.
      values-and-scores-by-row)))
