(ns inferenceql.spreadsheets.components.store.db
  "Contains the initial state of the db corresponding to the store component
  The store component is esentially a part of the app-db where datasets and
  models are stored."
  (:require [clojure.spec.alpha :as s]
            [inferenceql.spreadsheets.config :as config]
            [inferenceql.spreadsheets.csv :as csv-utils]
            [inferenceql.inference.gpm :as gpm]))

(def compiled-in-model (gpm/Multimixture (get config/config :model)))

(def compiled-in-schema
  (get-in config/config [:model :vars]))

(def compiled-in-dataset
  (let [csv-data (get config/config :data)]
    (csv-utils/csv-data->clean-maps compiled-in-schema csv-data {:keywordize-cols true})))

(def default-db
  {:store-component {:datasets {:data {:rows compiled-in-dataset
                                       :schema compiled-in-schema
                                       :default-model :model}}
                     :models {:model compiled-in-model}}})

(s/def ::store-component (s/keys :req-un [::datasets
                                          ::models]))


;; TODO: add more specs