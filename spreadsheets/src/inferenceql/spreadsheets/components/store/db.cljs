(ns inferenceql.spreadsheets.components.store.db
  "Contains the initial state of the db corresponding to the store component
  The store component is esentially a part of the app-db where datasets and
  models are stored."
  (:require [clojure.spec.alpha :as s]
            [inferenceql.spreadsheets.config :as config]
            [inferenceql.spreadsheets.csv :as csv-utils]
            [inferenceql.inference.gpm :as gpm]
            [medley.core :as medley]))

;;; Compiled-in elements to store

(def compiled-in-model (get config/config :model))

(def compiled-in-schema (get-in config/config [:model :vars]))

(def compiled-in-dataset
  (let [dataset (get config/config :data)]
    (csv-utils/csv-data->clean-maps compiled-in-schema dataset {:keywordize-cols true})))

;;; Setting up store component db

(def default-db-basic
  "Portions of the store-db related to the compiled-in model and dataset."
  {:store-component {:datasets {:data {:rows compiled-in-dataset
                                       :schema compiled-in-schema
                                       :default-model :model}}
                     :models {:model (gpm/Multimixture compiled-in-model)}}})

(def default-db-geodata
  "Portions of the store-db related to any included geodata and geodata settings."
  (when (:geodata config/config)
    {:store-component {:datasets {:data {:geodata-name :default
                                         :geo-id-col (get-in config/config [:geodata :geo-id-col])}}
                       :geodata {:default (get-in config/config [:geodata])}}}))

(def default-db (medley/deep-merge default-db-basic
                                   (or default-db-geodata {})))

;;  Specs for the store-db

(s/def ::store-component (s/keys :req-un [::datasets
                                          ::models]
                                 :opt-un [::geodata]))

;; Specs for :datasets.

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
(s/def ::default-model ::model-name)
(s/def ::geo-id-col string?)
(s/def ::column-name keyword?)

;; Specs for :models.

(s/def ::models (s/map-of ::model-name ::model))
(s/def ::model gpm/gpm?)

;; Specs for :geodata.

(s/def ::geodata (s/map-of ::geodata-name ::geodatum))
(s/def ::geodata-name keyword?)
(s/def ::geodatum (s/keys :req-un [::data
                                   ::filetype
                                   ::id-prop]
                          :opt-un [::feature
                                   ::fips-code-length
                                   ::projection-type]))

(s/def ::data object?) ; ::data is stored as a JS object.
(s/def ::filetype #{:topojson :geojson})
(s/def ::id-prop string?)
(s/def ::feature string?)
(s/def ::fips-code-length integer?)
(s/def ::projection-type #{"albersUsa" "mercator"})
