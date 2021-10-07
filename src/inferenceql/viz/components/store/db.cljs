(ns inferenceql.viz.components.store.db
  "Contains the initial state of the db corresponding to the store component
  The store component is esentially a part of the app-db where datasets and
  models and geodata are stored."
  (:require [clojure.spec.alpha :as s]
            [inferenceql.viz.config :as config]
            [inferenceql.viz.csv :refer [clean-csv-maps]]
            [inferenceql.viz.util :refer [keywordize-kv]]))

;;; Compiled-in elements to store

(def compiled-in-schema
  ;; Coerce schema to contain columns names and datatyptes as keywords.
  (keywordize-kv (get config/config :schema)))

(def compiled-in-dataset
  (clean-csv-maps compiled-in-schema
                  (get config/config :data)))

;;; Setting up store component db

(def default-db
  "Portions of the initial store-db related to the compiled-in model and dataset.

  The compiled-in dataset is called: data.
  The compiled-in model is called: model."
  {:store-component {:datasets {:data {:rows compiled-in-dataset
                                       :schema compiled-in-schema}}}})

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

