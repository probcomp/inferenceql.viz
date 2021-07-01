(ns inferenceql.viz.observable.imputation
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
    #(/ (- % min-n)
        scale-factor)))

;;; These functions are for imputing data in empty cells and
;;; also returning a score for the imputed values.

(defn impute-and-score-cell
  "Returns an imputed value of `key-to-impute` along with its score"
  [query-fn row schema key-to-impute num-samples]
  (let [cond-string (->> row
                         (map (fn [[k v]]
                                (case (get schema k)
                                  :numerical [(name k) (str v)]
                                  :nominal [(name k) (str "\"" v "\"")])))
                         (map (fn [[k v]] (format "%s=%s" k v))))
        cond-string (string/join " AND " cond-string)

        query-str (format
                   "SELECT %s FROM (GENERATE %s UNDER model CONDITIONED BY %s) LIMIT %s"
                   (name key-to-impute)
                   (name key-to-impute)
                   cond-string
                   num-samples)

        samples (map key-to-impute (query-fn query-str))
        freq-list (sort-by val > (frequencies samples))
        [top-sample top-sample-count] (first freq-list)

        total-sample-count (count samples)
        top-sample-prob (/ top-sample-count total-sample-count)]
    {:value top-sample :score top-sample-prob}))

(defn impute-missing-cells-in-row
  "Returns a map of imputed values and scores for all missing values in `row`"
  [query-fn row schema impute-cols num-samples]
  (let [available-keys (keys row)
        missing-keys (set/difference (set impute-cols) (set available-keys))
        values-scores (map #(impute-and-score-cell query-fn row schema % num-samples) missing-keys)]
    (zipmap missing-keys values-scores)))

(defn normalize-missing-cells-scores
  "Normalizes scores within each map in `values-and-scores-by-row`"
  [values-and-scores-by-row all-scores]
  (let [normalizer (min-max-normalizer-fn all-scores)
        norm-scores (fn [row-vals-scores]
                      (medley/map-vals (fn [val-score-map] (update val-score-map :score normalizer))
                                       row-vals-scores))]
    (map norm-scores values-and-scores-by-row)))

(defn impute-missing-cells
  "Returns imputed values and normalized scores for all missing values in `rows`"
  [query-fn rows schema impute-cols num-samples]
  (let [values-and-scores-by-row (map #(impute-missing-cells-in-row query-fn % schema impute-cols num-samples) rows)

        all-scores (->> values-and-scores-by-row
                        (map vals) ;; Produces sequence of {:score _ :value _ } maps for each row.
                        (apply concat)  ;; Flattened sequence of {:score _ :value _ } maps.
                        (map :score))] ;; Sequence of score values.


    ;; Only normalize if distinct scores present.
    values-and-scores-by-row
    #_(if (> (count (distinct all-scores)) 1)
        (normalize-missing-cells-scores values-and-scores-by-row all-scores)
        ;; Just return the raw likelihoods otherwise.
        values-and-scores-by-row)))

;------------------

(defn imputation-query
  [row schema impute-cols num-samples]
  (let [available-keys (keys row)
        missing-keys (set/difference (set impute-cols) (set available-keys))]
    (when (seq missing-keys)
      (let [cond-string (->> row
                          (map (fn [[k v]]
                                 (case (get schema k)
                                   :numerical [(name k) (str v)]
                                   :nominal [(name k) (str "\"" v "\"")])))
                          (map (fn [[k v]] (format "%s=%s" k v))))
            cond-string (string/join " AND " cond-string)

            impute-cols-str (string/join ", " (map name impute-cols))]

        (format
         "SELECT %s FROM \n(GENERATE %s \nUNDER model \nCONDITIONED BY %s) \nLIMIT %s"
         impute-cols-str
         impute-cols-str
         cond-string
         num-samples)))))

(defn imputation-queries
  [rows schema impute-cols num-samples]
  (let [queries (for [[idx r] (map-indexed vector rows)]
                  [idx (imputation-query r schema impute-cols num-samples)])]
    (filter (comp some? second) queries)))
