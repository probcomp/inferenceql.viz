(ns inferenceql.viz.js_score
  (:require [inferenceql.viz.util :as util]
            [inferenceql.inference.gpm.multimixture.search :as search]
            [inferenceql.inference.gpm.multimixture.utils :as mm.utils]
            [metaprob.prelude :as mp]
            [inferenceql.inference.gpm :as gpm]
            [medley.core :as medley]
            [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [cljs-bean.core :refer [->clj]]))

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


;;; These functions are for imputing data in empty cells and
;;; also returning a score for the imputed values.

(defn impute-and-score-cell
  "Returns an imputed value of `key-to-impute` along with its score"
  [query-fn schema row key-to-impute]
  (let [cond-string (->> row
                         (map (fn [[k v]] (case (get schema k)
                                            :gaussian [(name k) (str v)]
                                            :categorical [(name k) (str "\"" v "\"")])))
                         (map (fn [[k v]] (format "%s=%s" k v)))
                         (string/join " AND "))

        query-str (format
                   "SELECT %s FROM (GENERATE %s UNDER model CONDITIONED BY %s) LIMIT %s"
                   (name key-to-impute)
                   (name key-to-impute)
                   cond-string
                   imputation-sample-size)

        samples (map #(get % key-to-impute) (query-fn query-str))
        freq-list (sort-by val > (frequencies samples))
        [top-sample top-sample-count] (first freq-list)

        total-sample-count (count samples)
        top-sample-prob (/ top-sample-count total-sample-count)]
    {:value top-sample :score top-sample-prob}))

(defn impute-missing-cells-in-row
  "Returns a map of imputed values and scores for all missing values in `row`"
  [query-fn schema row]
  (let [available-keys (keys row)
        missing-keys (set/difference (set (keys schema)) (set available-keys))
        values-scores (map #(impute-and-score-cell query-fn schema row %) missing-keys)]
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
  [query-fn schema rows]
  (let [rows (vec (->clj rows))
        schema (medley/map-kv (fn [k v] [(keyword k) (keyword v)])
                              (->clj schema))

        values-and-scores-by-row (map #(impute-missing-cells-in-row query-fn schema %) rows)

        all-scores (->> values-and-scores-by-row
                        (map vals) ;; Produces sequence of {:score _ :value _ } maps for each row.
                        (apply concat)  ;; Flattened sequence of {:score _ :value _ } maps.
                        (map :score)) ;; Sequence of score values.

        ;; Only normalize if distinct scores present.
        ret-map (if (> (count (distinct all-scores)) 1)
                  (normalize-missing-cells-scores values-and-scores-by-row all-scores)
                  ;; Just return the raw likelihoods otherwise.
                  values-and-scores-by-row)]
    (clj->js ret-map)))
