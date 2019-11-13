(ns inferenceql.spreadsheets.clojure-conj.data
  (:require
    [inferenceql.spreadsheets.data :refer [fix-row csv-data->maps]]
    [clojure.data.csv :as csv]
    [clojure.java.io :as io]
    [clojure.string :as str]
    [medley.core :as medley]
    [clojure.data.json :as json]))

(def data-filename "stack-overflow.csv")

(def csv-lines (-> data-filename (io/resource) (slurp) (csv/read-csv) (vec)))
(def so-data (->> csv-lines
                  (mapv fix-row)
                  (csv-data->maps)))

(defn has-item? [column-name item-name data-map]
  (let [items-str (get data-map column-name)
        items (str/split items-str #";")]
    (cond (= items-str "NA") :NA
          (some #{item-name} items) :true
          :else :false)))

(def so-data-clj (filter
                  #(= (has-item? "LanguageWorkedWith" "Clojure" %) :true)
                  so-data))

(def so-data-not-clj (filter
                      #(= (has-item? "LanguageWorkedWith" "Clojure" %) :false)
                      so-data))

(def items-to-find
  {"LanguageWorkedWith" ["Clojure" "C++" "Rust" "Java" "JavaScript"]
   "PlatformWorkedWith" ["Kubernetes" "Docker" "AWS"]
   "WebFrameWorkedWith" ["React.js"]})

;--------------------------------


(defn items-map [rows]
  (let [build-item-group (fn [accum group-name items-in-group]
                            (let [p (for [item items-in-group]
                                      (map (partial has-item? group-name item) rows))
                                  p-map (zipmap items-in-group p)]
                              (merge accum p-map)))]
   (reduce-kv build-item-group {} items-to-find)))

(defn items-subset
  "Return subset of columns in each row.
  Useful for sending to table plotting functions."
  [rows]
  (let [make-pair (fn [group-name item row]
                    (case (has-item? group-name item row)
                      :true [item true]
                      :false [item false]
                      :NA [item "NA"]))
        group-items (apply concat
                            (for [[group-name items] items-to-find]
                              (for [item items]
                                [group-name item])))]
    ;; Make one map for each row with our desired fields.
    (for [r rows]
      (into {}
        (for [[group-name item] group-items]
          (make-pair group-name item r))))))

(defn items-freqs-map [items-map]
  (medley/map-vals frequencies items-map))

(defn items-tf-map [items-freqs-map]
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

(defn salary-map [data]
  (let [salary-data (map #(get % "CompTotal") data)]
    (assoc {} "CompTotal" salary-data)))

;--------------------------------

;;; Data for the unconditional model

(def uncond-freqs (-> so-data
                      (items-map)
                      (items-freqs-map)))

(def uncond-tf (items-tf-map uncond-freqs))

;;; Data for the conditional model

(def clojure-dev-prob
  (let [clojure-freqs (get uncond-freqs "Clojure")
        total (+ (:true clojure-freqs) (:false clojure-freqs))
        ratio (/ (:true clojure-freqs) total)]
    ratio))

(def clj-tf (-> so-data-clj
                (items-map)
                (items-freqs-map)
                (items-tf-map)))

(def not-clj-tf (-> so-data-not-clj
                    (items-map)
                    (items-freqs-map)
                    (items-tf-map)))

(def clj-freqs (-> so-data-clj
                (items-map)
                (items-freqs-map)))

(def not-clj-freqs (-> so-data-not-clj
                    (items-map)
                    (items-freqs-map)))
;;; For printing and copying probabilities

(defn print-probs [tf-map]
  (for [[col probs] tf-map]
    (println (str "\"" col "\"") (:true probs))))

;;; Subsetted data maps for passing to table plotting functions.

(def data-subset-clj (items-subset so-data-clj))
(def data-subset-not-clj (items-subset so-data-not-clj))
(def data-subset (items-subset so-data))

(comment
  (print-probs clj-tf)
  (print-probs not-clj-tf))

(defn freqs-to-vega-data [freqs]
  (let [reduce-col (fn [accum col-name col-map]
                     (let [new-lines (for [[val-type val-count] col-map]
                                       {:column col-name :value val-type :count val-count})]
                       (concat accum new-lines)))]
    (reduce-kv reduce-col [] freqs)))

(comment
  (print (json/write-str (freqs-to-vega-data uncond-freqs)))
  (print (json/write-str (freqs-to-vega-data clj-freqs)))
  (print (json/write-str (freqs-to-vega-data not-clj-freqs))))
