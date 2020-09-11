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

(def default-db-base
  {:store-component {:datasets {:data {:rows compiled-in-dataset
                                       :schema compiled-in-schema
                                       :default-model :model}}
                     :models {:model (gpm/Multimixture compiled-in-model)}}})

(def geodata-db-entries
  (if (nil? (:geodata config/config))
    {}
    {:store-component {:datasets {:data {:geodata-name :default-geo
                                         :geo-id-col (get-in config/config [:geodata :geo-id-col])}}
                       :geodata {:default-geo {:data (get-in config/config [:geodata :data])
                                               :filetype (get-in config/config [:geodata :filetype])
                                               :feature (get-in config/config [:geodata :feature])
                                               :id-prop (get-in config/config [:geodata :id-prop])
                                               :id-prop-code-length (get-in config/config [:geodata :id-prop-code-length])
                                               :projection-type (get-in config/config [:geodata :projection-type])}}}}))

(def default-db (medley/deep-merge default-db-base geodata-db-entries))

;; TODO: add more specs
(s/def ::store-component (s/keys :req-un [::datasets
                                          ::models]))
