(ns inferenceql.spreadsheets.clojure-conj.data-ulli
  (:require
    [inferenceql.spreadsheets.data :refer [fix-row csv-data->maps]]
    [clojure.data.csv :as csv]
    [clojure.java.io :as io]
    [clojure.string :as str]
    [medley.core :as medley]
    [clojure.data.json :as json]
    [inferenceql.spreadsheets.clojure-conj.table-plotting-views :as plot]))

(def data-filename "stackoverflow-demo-subset-v4.csv")
(def partitions-filename "partitions.json")

;----------------------------

(defn manager-binary [val]
  "Turns to nominal manager column into a binary"
  (case val
    "Not sure" "False"
    "No" "False"
    "Yes" "True"
    "I am already a manager" "True"
    nil "NA"))

(def csv-lines (-> data-filename (io/resource) (slurp) (csv/read-csv) (vec)))
(def so-data (->> csv-lines
                  (mapv fix-row)
                  (csv-data->maps)
                  ;; add row ids
                  (map (fn [id row] (assoc row "id" id)) (range))
                  ;; make manager column binary
                  (map #(update % "aspire_to_be_manager" manager-binary))))

(def column-mapping (zipmap (range) (first csv-lines)))

;----------------------------

(def partition-data (-> partitions-filename (io/resource) (slurp) (json/read-str)))

;----------------------------

(let [model-num "0"]
  (plot/viz-model partition-data so-data column-mapping model-num))
