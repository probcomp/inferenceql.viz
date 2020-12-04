(ns inferenceql.spreadsheets.data
  (:require [inferenceql.spreadsheets.csv :as csv-utils]
            [inferenceql.spreadsheets.config :as config]))

(def nyt-data
  (let [column-types (get-in config/config [:model :vars])
        csv-data (get config/config :data)]
    (csv-utils/csv-data->clean-maps column-types csv-data {:keywordize-cols true})))
