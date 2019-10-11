(ns inferdb.spreadsheets.score
  (:require [inferdb.spreadsheets.util :as util]
            [inferdb.multimixture.search :as search]
            [inferdb.multimixture :as mmix]
            [metaprob.prelude :as mp]
            [medley.core :as medley]
            [clojure.set :as set]))

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
  [row-generator row]
  (let [target (util/filter-nil-kvs row)
        constraints {}]
    (Math/exp (search/logpdf row-generator target constraints))))

(defn row-likelihoods
  "Returns a sequence of normalized likelihoods for `rows`"
  [spec rows]
  (let [row-gen (search/optimized-row-generator spec)
        likelihoods (map #(score-row-uncond row-gen %) rows)
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
  [row-generator row]
  (let [clean-row (util/filter-nil-kvs row)
        cell-pairs (seq clean-row)
        likelihood-pairs (for [[k v] cell-pairs]
                           (let [target (assoc {} k v)
                                 constraints (dissoc clean-row k)]
                             [k (Math/exp (search/logpdf row-generator target constraints))]))]
    (into {} likelihood-pairs)))

(defn cell-likelihoods
  "Returns the likelihood of all the cells in `rows`"
  [spec rows]
  (let [row-gen (search/optimized-row-generator spec)]
    ;; TODO normalize scores before returning.
    (map #(score-cells-in-row row-gen %) rows)))

;;; These functions are for imputing data in empty cells and
;;; also returning a likelihoods for the imputed values.

(defn impute-and-score-cell
  "Returns an imputed value of `key-to-impute` along with its likelihood"
  [row-gen row key-to-impute]
  (let [constraints (mmix/with-row-values {} row)
        gen-fn #(-> (mp/infer-and-score :procedure row-gen
                                        :observation-trace constraints)
                    (first)
                    (get key-to-impute))
        samples (repeatedly 2 gen-fn)
        freq-list (sort-by val > (frequencies samples))
        [top-sample top-sample-count] (first freq-list)

        total-sample-count (count samples)
        top-sample-prob (/ top-sample-count total-sample-count)]
    [top-sample top-sample-prob]))

(defn impute-missing-cells-in-row
  "Returns a map of imputed values and likelihoods for all missing values in `row`"
  [row-generator headers row]
  (let [clean-row (util/filter-nil-kvs row)
        available-keys (keys clean-row)
        missing-keys (set/difference (set headers) (set available-keys))
        values-scores (map #(impute-and-score-cell row-generator clean-row %) missing-keys)

        values (map first values-scores)
        values-map (zipmap missing-keys values)
        scores (map second values-scores)
        scores-map (zipmap missing-keys scores)

        ret-val {:values values-map :scores scores-map}]
    ret-val))

(defn normalize-missing-cells-likelihoods
  "Normalizes likelihoods across a whole seq of maps (`likelihood-maps`)
  each containing likelihoods for missing cells in a row."
  [likelihood-maps]
  (let [all-likelihoods (mapcat vals likelihood-maps)
        normalizer (min-max-normalizer-fn all-likelihoods)

        ;; Normalizes every value in a map of likelihoods for
        ;; missing cells in a row.
        normalize-row-fn #(medley/map-vals normalizer %)]
    (map normalize-row-fn likelihood-maps)))

(defn impute-missing-cells
  "Returns imputed values and normalized likelihoods for
  all missing values in `rows`"
  [spec headers rows]
  (let [row-gen (search/optimized-row-generator spec)
        values-and-scores (map #(impute-missing-cells-in-row row-gen headers %) rows)
        values (map :values values-and-scores)
        scores (map :scores values-and-scores)

        all-scores (mapcat vals scores)
        distinct-scores-present (> (count (distinct all-scores)) 1)

        norm-scores (if distinct-scores-present
                      (normalize-missing-cells-likelihoods scores)

                      ;; Just return the raw likelihoods when we can't normalize.
                      scores)]

    ;; Returns imputed values with normalized likelihoods for each row
    (map #(assoc {} :values %1 :scores %2) values norm-scores)))
