(ns inferenceql.viz.imputation
  (:require [inferenceql.viz.util :as util]
            [medley.core :as medley]
            [clojure.set :as set]
            [clojure.string :as string]
            [goog.string :refer [format]]))

(defn- min-max-normalizer-fn [nums]
  "Returns a function that rescales a number from `nums` to the range [0,1]."
  (let [min-n (apply min nums)
        max-n (apply max nums)
        scale-factor (util/abs (- max-n min-n))]
    (if (= scale-factor 0)
      identity ;; If we don't have a range of values, do nothing when normalizing.
      #(/ (- % min-n)
          scale-factor))))

(defn min-max-normalize [nums]
  "Re-scales each item in `nums` to the range [0,1]."
  (let [normalizer (min-max-normalizer-fn nums)]
    (map normalizer nums)))

;;; These functions are for imputing data in empty cells and
;;; also returning a score for the imputed values.

(defn normalize-scores
  "Normalizes scores within each map in `values-and-scores-by-row`"
  [values-and-scores-by-row]
  (let [all-scores (->> values-and-scores-by-row
                        (map vals)       ;; Produces sequence of {:score _ :value _ } maps for each row.
                        (apply concat)   ;; Flattened sequence of {:score _ :value _ } maps.
                        (map :score))    ;; Sequence of score values.
        normalizer (min-max-normalizer-fn all-scores)
        norm-scores (fn [row-vals-scores]
                      (medley/map-vals (fn [val-score-map] (update val-score-map :score normalizer))
                                       row-vals-scores))]
    (map norm-scores values-and-scores-by-row)))

(defn impute-query [targets constrainsts num-samples schema]
  "Returns a query string for imputing values for `targets`."
  (let [cond-str (->> constrainsts
                      ;; TODO: Sort conditions in a consistent order.
                      (map (fn [[k v]]
                             (case (get schema k)
                               :numerical [(name k) (str v)]
                               :nominal [(name k) (str "\"" v "\"")]
                               ;; Do not include the constraint if it is not in the schema.
                               ;; This is so un-modeled columns are ignored.
                               nil)))
                      (remove nil?)
                      (map (fn [[k v]] (format "%s=%s" k v)))
                      (string/join " AND "))
        targets-str (string/join ", " (map name targets))]
    (format "SELECT %s FROM (GENERATE %s UNDER model CONDITIONED BY %s) LIMIT %s"
            targets-str
            targets-str
            cond-str
            num-samples)))

(defn impute-fn [query-runner num-samples schema]
  (fn [target constraints]
    (let [q (impute-query [target] constraints num-samples schema)
          samples (map target (query-runner q))
          freq-list (sort-by val > (frequencies samples))
          [top-sample top-sample-count] (first freq-list)
          total-sample-count (count samples)
          top-sample-prob (/ top-sample-count total-sample-count)]
      {:value top-sample :score top-sample-prob})))

(defn impute-missing-in-row
  "Returns a map of imputed values and scores for all missing values in `row`"
  [imputer impute-cols row]
  (let [missing-keys (set/difference (set impute-cols) (set (keys row)))
        values-scores (map #(imputer % row) missing-keys)]
    (zipmap missing-keys values-scores)))

(defn impute-missing-cells
  "Returns imputed values and normalized scores for all missing values in `rows`"
  [query-runner rows schema impute-cols num-samples]
  (let [imputer (impute-fn query-runner num-samples schema)
        values-and-scores (for [r rows]
                            (impute-missing-in-row imputer impute-cols r))]
    (normalize-scores values-and-scores)))

(defn imputation-queries
  "Returns a collection of query strings for imputing missing values in `rows`"
  [rows schema impute-cols num-samples]
  (let [queries (for [r rows]
                  (let [missing (set/difference (set impute-cols) (set (keys r)))]
                    (when (seq missing)
                      (impute-query impute-cols r num-samples schema))))
        queries (map-indexed vector queries)]
    (filter (comp some? second) queries)))
