(ns inferenceql.spreadsheets.clojure-conj.data
  (:require
    [inferenceql.spreadsheets.data :refer [fix-row csv-data->maps]]
    [clojure.data.csv :as csv]
    [clojure.java.io :as io]
    [clojure.string :as str]
    [medley.core :as medley]))

(def data-filename "stack-overflow.csv")

(def csv-lines (-> data-filename (io/resource) (slurp) (csv/read-csv) (vec)))
(def so-data (->> csv-lines
                  (mapv fix-row)
                  (csv-data->maps)))

(def items-to-find
  {"LanguageWorkedWith" ["Clojure" "C++" "Rust" "Java" "JavaScript"]
   "PlatformWorkedWith" ["Kubernetes" "Docker" "AWS"]
   "WebFrameWorkedWith" ["React.js"]})

(defn has-item? [column-name item-name data-map]
  (let [items-str (get data-map column-name)
        items (str/split items-str #";")]
    (cond (= items-str "NA") :NA
          (some #{item-name} items) :true
          :else :false)))

(def items-map
  (let [build-item-group (fn [accum group-name items-in-group]
                            (let [p (for [item items-in-group]
                                      (map (partial has-item? group-name item) so-data))
                                  p-map (zipmap items-in-group p)]
                              (merge accum p-map)))]
   (reduce-kv build-item-group {} items-to-find)))

(def items-freqs-map
  (medley/map-vals frequencies items-map))

(def items-tf-map
  (let [make-tf-map (fn [freqs]
                      (let [t-count (get freqs :true 0)
                            f-count (get freqs :false 0)
                            total (+ t-count f-count)]
                        (assert (not= total 0))
                        {:true (double (/ t-count total))
                         :false (double (/ f-count total))}))]
                        ;{:true (format "%.2f" (double (/ t-count total)))
                        ; :false (format "%.2f" (double (/ f-count total)))}))]
    (medley/map-vals make-tf-map items-freqs-map)))

(def items-flip-statements
  (let [make-flip-statement (fn [item-name tf-map]
                              (let [new-val (list 'at item-name 'flip (get tf-map :true))]
                               [item-name new-val]))]
    (medley/map-kv make-flip-statement items-tf-map)))

(def salary-map
  (let [salary-data (map #(get % "CompTotal") so-data)]
    (assoc {} "CompTotal" salary-data)))
