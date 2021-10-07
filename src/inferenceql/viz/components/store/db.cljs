(ns inferenceql.viz.components.store.db
  (:require [inferenceql.viz.config :refer [config]]
            [inferenceql.viz.csv :refer [clean-csv-maps]]
            [inferenceql.viz.util :refer [keywordize-kv]]
            [inferenceql.inference.gpm.crosscat :as crosscat]
            [inferenceql.viz.model.xcat :as xcat]
            [inferenceql.viz.model.xcat-util :refer [columns-in-model sample-xcat]]))

;;; Compiled-in elements from config.

(def schema
  ;; Coerce schema to contain columns names and datatyptes as keywords.
  (keywordize-kv (:schema config)))

(def rows (clean-csv-maps schema (:data config)))
(def mapping-table (:mapping-table config))

;; Data obtained from the global js namespace, placed there by scripts tags in index.html.

(def mutual-info (js->clj js/mutual_info :keywordize-keys true))
(def cgpm-models (js->clj js/transitions))

;; Model iterations

;; TODO: Off load the conversion into xcat into DVC stage.
(def xcat-models (map (fn [cgpm]
                        (let [num-rows (count (get cgpm "X"))]
                          (xcat/import cgpm (take num-rows rows) mapping-table schema)))
                      cgpm-models))
(def mmix-models (doall (map crosscat/xcat->mmix xcat-models)))

;; Secondary defs built off of xcat model iterations.

(def col-ordering
  (reduce (fn [ordering xcat]
            (let [new-columns (clojure.set/difference (set (columns-in-model xcat))
                                                      (set ordering))]
              (concat ordering new-columns)))
          []
          xcat-models))

(def num-points-at-iter (map (fn [xcat]
                               (let [[view-1-name view-1] (first (get xcat :views))]
                                 ;; Count the number of row to cluster assignments.
                                 (count (get-in view-1 [:latents :y]))))
                             xcat-models))

(def num-points-required (map - num-points-at-iter (conj num-points-at-iter 0)))

;; Settings up samples.

(defn add-null-columns [row]
  (let [columns (keys schema)
        null-kvs (zipmap columns (repeat nil))]
    (merge null-kvs row)))

(def iteration-tags
  (mapcat (fn [iter count]
            (repeat count {:iter iter}))
          (range)
          num-points-required))

(def observed-samples
  (->> rows
       (map #(assoc % :collection "observed"))
       (map add-null-columns)
       (map merge iteration-tags)))

(def virtual-samples
  (->> (mapcat sample-xcat xcat-models num-points-required)
       (map #(assoc % :collection "virtual"))
       (map add-null-columns)
       (map merge iteration-tags)))

(def all-samples (concat observed-samples virtual-samples))
