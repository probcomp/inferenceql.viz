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

(def model-1 (get partition-data "0"))
(def model-1-iter (get model-1 "9"))

(def view-col-assignments (->> (get model-1-iter "view-partitions")
                               (medley/map-keys #(Integer/parseInt %))
                               (medley/map-keys #(get column-mapping %))
                               (group-by second)
                               (medley/map-vals #(map first %))))

(def views (->> (get model-1-iter "view-row-partitions")
                (medley/map-keys #(Integer/parseInt %))))

(def view-ids (keys views))
(def cluster-ids
  (medley/map-vals #(vec (distinct %)) views))

(defn assign-partitions-to-rows [rows views]
  (let [add-view-info (fn [rows view-id cluster-assignments]
                        (let [view-name (str "view-" view-id)]
                          (map (fn [row c-assignment] (assoc row view-name c-assignment)) rows cluster-assignments)))]
    (reduce-kv add-view-info rows views)))

(def clustered-so-data (assign-partitions-to-rows so-data views))

;------------------------------------
;; Visualizing partitions

(defn generate-colors [cluster-ids]
  (let [color-list [["blue" "lightblue"] ["green" "lightgreen"] ["firebrick" "salmon"]]
        grab-colors (fn [cids-in-view]
                      (assert (<= (count cids-in-view) (count color-list)))
                      (zipmap cids-in-view color-list))]
    (medley/map-vals grab-colors cluster-ids)))

(defn demo-multi-view-plot []
  (let [colors (generate-colors cluster-ids)]
    (plot/spec-mult-views view-ids cluster-ids view-col-assignments clustered-so-data colors)))

;(demo-multi-view-plot)
