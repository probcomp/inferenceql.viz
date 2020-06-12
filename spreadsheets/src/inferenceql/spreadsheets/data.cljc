(ns inferenceql.spreadsheets.data
  (:require [inferenceql.spreadsheets.csv :as csv-utils]
            [inferenceql.spreadsheets.config :as config]))

(defn generate-row-id
  "Generates a row id to attach to data rows."
  []
  (name (gensym "r")))

(def nyt-data
  (let [column-types (get-in config/config [:model :vars])
        csv-data (get config/config :data)]
    (csv-utils/csv-data->clean-maps column-types csv-data {:keywordize-cols true})))

(def app-dataset
  ;; Adding hidden attributes to each row.
  (mapv #(assoc % :inferenceql.viz.row/id__ (generate-row-id))
        nyt-data))

(def app-dataset-indexed
  (zipmap (map :inferenceql.viz.row/id__ app-dataset)
          app-dataset))

(def app-dataset-order
  (mapv :inferenceql.viz.row/id__ app-dataset))
