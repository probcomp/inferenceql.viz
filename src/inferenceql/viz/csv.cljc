(ns inferenceql.viz.csv
  "Functions for processing csv data files."
  (:require [clojure.string :as str]
            [inferenceql.query.data :refer [row-coercer]]))

(defn clean-vecs [csv-vecs]
  "Changes all blank strings vals to nil."
  (for [row csv-vecs]
    (for [v row]
      (if (str/blank? v) nil v))))

(defn csv-maps
  [csv-data]
  (let [headers (map keyword (first csv-data))
        rows (rest csv-data)]
    (mapv #(zipmap headers %)
          rows)))

(defn clean-csv-maps
  "Takes `csv-data`, a vector of row vectors, and returns a seq of cleaned, casted data in maps.
  Casts data according to types in `column-types`."
  [column-types csv-data]
  (let [row-coercer (row-coercer column-types)
        csv-maps (-> csv-data
                     (clean-vecs)
                     (csv-maps))]
    (mapv row-coercer csv-maps)))
