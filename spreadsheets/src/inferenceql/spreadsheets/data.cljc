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
  "Dataset with row id's and other special attributes."
  (vec (map-indexed (fn [idx row] (assoc row :iql.viz.row/id__ (generate-row-id)
                                             :iql.viz.row/row-number__ (inc idx)
                                             :iql.viz.row/user-added-row__ false))
                    nyt-data)))

(def app-dataset-order
  "Ordering of dataset by row id's"
  (mapv :iql.viz.row/id__ app-dataset))

(def app-dataset-indexed
  "Dataset indexed by row-id."
  (zipmap app-dataset-order app-dataset))
