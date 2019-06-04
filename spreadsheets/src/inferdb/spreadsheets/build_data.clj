(ns inferdb.spreadsheets.build-data
  (:require [clojure.pprint]))

(defn csv-data->maps [csv-data]
  (mapv zipmap
        (->> (map str (first csv-data))
             repeat)
        (rest csv-data)))

(defn write-data [data fn]
  (with-open [w (clojure.java.io/writer fn)]
    (clojure.pprint/pprint '(ns inferdb.spreadsheets.data) w)
    (.write w "\n")
    (clojure.pprint/pprint `(def ~'nyt-data ~(csv-data->maps data)) w)))
