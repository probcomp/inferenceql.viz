(ns inferenceql.viz.components.store.db
  "Contains the initial state of the db corresponding to the store component
  The store component is esentially a part of the app-db where datasets and
  models and geodata are stored."
  (:require [clojure.spec.alpha :as s]
            [inferenceql.viz.config :refer [config transitions]]
            [inferenceql.viz.csv :refer [clean-csv-maps]]
            [inferenceql.viz.util :refer [keywordize-kv]]
            [inferenceql.inference.gpm.crosscat :as crosscat]
            [inferenceql.viz.model.xcat :as xcat]
            [inferenceql.viz.model.xcat-util :refer [columns-in-model sample-xcat]]))

;;; Compiled-in elements to store

(def schema
  ;; Coerce schema to contain columns names and datatyptes as keywords.
  (keywordize-kv (get config :schema)))

(def rows
  (clean-csv-maps schema
                  (get config :data)))

;; Model iterations

(def cgpm-models transitions)
;; TODO: Off load the conversion into xcat into DVC stage.
(def xcat-models (map (fn [cgpm]
                        (let [num-rows (count (get cgpm "X"))]
                          (xcat/import cgpm (take num-rows rows) (:mapping-table config) schema)))
                      cgpm-models))
(def mmix-models (doall (map crosscat/xcat->mmix xcat-models)))

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

(defn add-null-columns [row]
  (let [columns (keys schema)
        null-kvs (zipmap columns (repeat nil))]
    (merge null-kvs row)))

(def iteration-tags (mapcat (fn [iter count]
                              (repeat count {:iter iter}))
                            (range)
                            num-points-required))

(def observed-samples (->> (map #(assoc % :collection "observed") rows)
                        (map add-null-columns)
                        (map merge iteration-tags)))

(def virtual-samples (->> (mapcat sample-xcat xcat-models num-points-required)
                       (map #(assoc % :collection "virtual"))
                       (map add-null-columns)
                       (map merge iteration-tags)))
(def all-samples (concat observed-samples virtual-samples))


;;; Setting up store component db

(def default-db
  "Portions of the initial store-db related to the compiled-in model and dataset.

  The compiled-in dataset is called: data.
  The compiled-in model is called: model."
  {:store-component {:datasets {:data {:rows rows
                                       :schema schema}}}})

;;;  Specs for the store-db

(s/def ::store-component (s/keys :req-un [::datasets]))

;;; Specs for :datasets.

(s/def ::datasets (s/map-of ::dataset-name ::dataset))
(s/def ::dataset-name keyword?)
(s/def ::dataset (s/keys :req-un [::rows
                                  ::schema]))


(s/def ::rows (s/coll-of ::row))
(s/def ::row (s/map-of ::column-name any?))
(s/def ::schema (s/map-of ::column-name ::stat-type))
(s/def ::stat-type #{:numerical :nominal})
(s/def ::column-name keyword?)

;;; Accessor functions to portions of the table-panel db.

(defn datasets
  [db]
  (get-in db [:store-component :datasets]))

