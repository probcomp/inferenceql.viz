(ns inferenceql.viz.data
  (:require [inferenceql.viz.csv :as csv-utils]
            [inferenceql.viz.config :as config]))

(def nyt-data
  (let [column-types (get-in config/config [:model :vars])
        csv-data (get config/config :data)]
    (csv-utils/csv-data->clean-maps column-types csv-data {:keywordize-cols true})))
