(ns inferenceql.viz.js-score
  (:require [inferenceql.viz.util :as util]
            [inferenceql.inference.gpm.multimixture.search :as search]
            [inferenceql.inference.gpm.multimixture.utils :as mm.utils]
            [metaprob.prelude :as mp]
            [inferenceql.inference.gpm :as gpm]
            [medley.core :as medley]
            [cljs-bean.core :refer [->clj]]
            [clojure.set :as set]
            [clojure.spec.alpha :as s]
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
  [query-fn schema row key-to-impute num-samples]
  (let [cond-string (->> row
                         (map (fn [[k v]]
                                (case (get schema k)
                                  :gaussian [(name k) (str v)]
                                  :categorical [(name k) (str "\"" v "\"")])))
                         (map (fn [[k v]] (format "%s=%s" k v))))
        cond-string (string/join " AND " cond-string)

        query-str (format
                   "SELECT %s FROM (GENERATE %s UNDER model CONDITIONED BY %s) LIMIT %s"
                   (name key-to-impute)
                   (name key-to-impute)
                   cond-string
                   num-samples)

        results (->clj (query-fn query-str))
        samples (map key-to-impute results)
        freq-list (sort-by val > (frequencies samples))
        _ (.log js/console (str key-to-impute " " freq-list))

        [top-sample top-sample-count] (first freq-list)

        total-sample-count (count samples)
        top-sample-prob (/ top-sample-count total-sample-count)]
    {:value top-sample :score top-sample-prob}))

(defn impute-missing-cells-in-row
  "Returns a map of imputed values and scores for all missing values in `row`"
  [query-fn schema row num-samples]
  (let [available-keys (keys row)
        missing-keys (set/difference (set (keys schema)) (set available-keys))
        missing-keys (filter #(= (get schema %) :categorical) missing-keys)
        values-scores (map #(impute-and-score-cell query-fn schema row % num-samples) missing-keys)]
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
  [query-fn schema rows num-samples]
  (let [values-and-scores-by-row (map #(impute-missing-cells-in-row query-fn schema % num-samples) rows)

        all-scores (->> values-and-scores-by-row
                        (map vals) ;; Produces sequence of {:score _ :value _ } maps for each row.
                        (apply concat)  ;; Flattened sequence of {:score _ :value _ } maps.
                        (map :score))] ;; Sequence of score values.

    ;; Only normalize if distinct scores present.
    (if (> (count (distinct all-scores)) 1)
      (normalize-missing-cells-scores values-and-scores-by-row all-scores)
      ;; Just return the raw likelihoods otherwise.
      values-and-scores-by-row)))
