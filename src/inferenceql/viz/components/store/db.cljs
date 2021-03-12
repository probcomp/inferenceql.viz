(ns inferenceql.viz.components.store.db
  "Contains the initial state of the db corresponding to the store component
  The store component is esentially a part of the app-db where datasets and
  models and geodata are stored."
  (:require [clojure.spec.alpha :as s]
            [inferenceql.viz.config :as config]
            [inferenceql.viz.csv :as csv-utils]
            [inferenceql.inference.gpm :as gpm]))

;;; Compiled-in elements to store

(def compiled-in-schema (get config/config :schema))

(def compiled-in-dataset
  (csv-utils/csv-data->clean-maps (get config/config :schema)
                                  (get config/config :data)
                                  {:keywordize-cols true}))

(def compiled-in-model (get config/config :model))

;;; Setting up store component db

(def default-db-basic
  "Portions of the initial store-db related to the compiled-in model and dataset.

  The compiled-in dataset is called: data.
  The compiled-in model is called: model."
  {:store-component {:datasets {:data {:rows compiled-in-dataset
                                       :schema compiled-in-schema
                                       :default-model :model}}
                     :models {:model compiled-in-model}}})

(def default-db
  "The initial store db including any geodata and geodata settings included with the appp."
  (if (:geodata config/config)
    (-> default-db-basic
        (assoc-in [:store-component :datasets :data :geodata-name] :default)
        (assoc-in [:store-component :datasets :data :geo-id-col] (get-in config/config [:geodata :geo-id-col]))
        (assoc-in [:store-component :geodata :default] (get-in config/config [:geodata])))
    default-db-basic))

;;;  Specs for the store-db

(s/def ::store-component (s/keys :req-un [::datasets
                                          ::models]
                                 :opt-un [::geodata]))

;;; Specs for :datasets.

(s/def ::datasets (s/map-of ::dataset-name ::dataset))
(s/def ::dataset-name keyword?)
(s/def ::dataset (s/keys :req-un [::rows
                                  ::schema
                                  ::default-model]
                         :opt-un [::geodata-name
                                  ::geo-id-col]))


(s/def ::rows (s/coll-of ::row))
(s/def ::row (s/map-of ::column-name any?))
(s/def ::schema (s/map-of ::column-name ::stat-type))
(s/def ::stat-type #{:gaussian :categorical})
(s/def ::model-name keyword?)
;; This is the model used to generate samples for simulation plots when a
;; single cell is selected in the table.
(s/def ::default-model ::model-name)
;; This is the name of the column in the dataset that is joined against entity
;; ids in geodata files.
(s/def ::geo-id-col string?)
(s/def ::column-name keyword?)

;;; Specs for :models.

(s/def ::models (s/map-of ::model-name ::model))
(s/def ::model gpm/gpm?)

;;; Specs for :geodata.

(s/def ::geodata (s/map-of ::geodata-name ::geodatum))
(s/def ::geodata-name keyword?)
(s/def ::geodatum (s/keys :req-un [::data
                                   ::filetype
                                   ::id-prop]
                          :opt-un [::feature
                                   ::fips-code-length
                                   ::projection-type]))

;; This is the actual geodata which is stored as a JS object.
(s/def ::data object?)
(s/def ::filetype #{:topojson :geojson})
;; This is the key for the collection of objects in the topojson to use for
;; matching with rows. This is only present for :topojson files.
(s/def ::feature string?)
;; This is the property of each object in the geodata file that is matched
;; with the :geo-id-col column in a dataset.
(s/def ::id-prop string?)
;; This is the expected length of the ids pointed to by :id-prop.
;; If items in the data table's :geo-id-col column are not the
;; proper length they will be 0-padded so they match this length.
(s/def ::fips-code-length integer?)
;; This is the type of d3-geo projection to use when rendering the geodata
;; data. See here for more info:
;; https://vega.github.io/vega-lite/docs/projection.html#projection-types
(s/def ::projection-type #{"albers" "albersUsa" "mercator" "identity"})
