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

(defn write-specs [model-num iter-num]
  (let [model (get partition-data model-num)
        iter (get model iter-num)

        view-col-assignments (->> (get iter "view-partitions")
                                  (medley/map-keys #(Integer/parseInt %))
                                  (medley/map-keys #(get column-mapping %))
                                  (group-by second)
                                  (medley/map-vals #(map first %)))

        views (->> (get iter "view-row-partitions")
                   (medley/map-keys #(Integer/parseInt %)))

        view-ids (keys views)
        cluster-ids (medley/map-vals #(vec (distinct %)) views)

        assign-partitions-to-rows
        (fn [rows views]
          (let [add-view-info (fn [rows view-id cluster-assignments]
                                (let [view-name (str "view-" view-id)]
                                  (map (fn [row c-assignment] (assoc row view-name c-assignment)) rows cluster-assignments)))]
            (reduce-kv add-view-info rows views)))

        clustered-so-data (assign-partitions-to-rows so-data views)

        ;; TEMP hack for testing
        clustered-so-data (take 50 clustered-so-data)

        filename-prefix (str "model-" model-num "-iter-" iter-num)]
    (plot/write-specs filename-prefix view-ids cluster-ids view-col-assignments clustered-so-data)))

;(doseq [model-num (range 5)]
;  (write-specs (str model-num) "9"))
