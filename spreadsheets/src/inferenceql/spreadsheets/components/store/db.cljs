(ns inferenceql.spreadsheets.components.store.db
  "Contains the initial state of the db corresponding to the store component
  The store component is esentially a part of the app-db where datasets and
  models are stored."
  (:require [clojure.spec.alpha :as s]
            [inferenceql.spreadsheets.config :as config]
            [inferenceql.spreadsheets.csv :as csv-utils]
            [inferenceql.inference.gpm :as gpm]))

;;; Compiled-in elements to store

(def compiled-in-model (get config/config :model))

(def compiled-in-schema (get-in config/config [:model :vars]))

(def compiled-in-dataset
  (let [dataset (get config/config :data)]
    (csv-utils/csv-data->clean-maps compiled-in-schema dataset {:keywordize-cols true})))

;;; Setting up store component db

(def default-db
  {:store-component {:datasets {:data {:rows compiled-in-dataset
                                       :schema compiled-in-schema
                                       :default-model :model}}
                     :models {:model (gpm/Multimixture compiled-in-model)}}})

(s/def ::store-component (s/keys :req-un [::datasets
                                          ::models]))

;; TODO: add more specs