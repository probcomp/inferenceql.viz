(ns inferenceql.spreadsheets.clojure-conj.models.count
  (:refer-clojure :exclude [replicate])
  #?(:cljs (:require-macros [metaprob.generative-functions :refer [gen let-traced]]))
  (:require
   [metaprob.prelude :as mp :refer [infer-and-score replicate exp]]
   #?(:clj [metaprob.generative-functions :refer [apply-at at make-generative-function gen let-traced]]
      :cljs [metaprob.generative-functions :refer [apply-at at make-generative-function]])
   [metaprob.distributions :as dist :refer [flip uniform gaussian categorical exactly]]
   [metaprob.inference :as inf]
   [metaprob.trace :as trace]
   [clojure.pprint :refer [pprint]]
   [inferenceql.spreadsheets.clojure-conj.data :as data]
   [inferenceql.spreadsheets.clojure-conj.trace-plotting :as tracep]
   [inferenceql.spreadsheets.clojure-conj.table-plotting :as tablep]
   [inferenceql.multimixture.basic-queries :as bq]
   [inferenceql.multimixture :as mmix]
   [inferenceql.spreadsheets.clojure-conj.models.util :refer [t-flip]]
   [clojure.string :as string]
   [clojure.data.csv :as csv]
   [clojure.java.io :as io]))

(def count-model-row-gen
  (gen []
    (let [col-probs {"AWS" 0.2639442971479545
                     "React.js"  0.3056965334809757
                     "Rust"  0.03190626819993377
                     "JavaScript" 0.6762552958238646
                     "C++" 0.2343751784307232
                     "Clojure" 0.01432013612123012
                     "Java" 0.4101565622537656
                     "Docker" 0.3123621676536908
                     "Kubernetes" 0.08468171568748915}
          vals (for [[col prob] col-probs]
                 (at `(:columns ~col) t-flip prob))]
      (zipmap (keys col-probs) vals))))

(defn make-count-model [row-generator]
  (make-generative-function
   row-generator
   (gen [partial-trace]
     (gen []
       (let [[v t s] (mp/infer-and-score :procedure row-generator :observation-trace partial-trace)]
         ;; NOTE: returning the new trace, not the partial trace as in search.clj
         ;; Maybe that was a bug in search.clj?
         [v t s])))))

(def count-model (make-count-model count-model-row-gen))

;;; ------------------------------------

;;; Demos of table and trace plotting functions.

(defn simple-table-plot [rows]
  (let [col-order (->> (keys (first rows))
                       (sort)
                       (map vector (range)))
        make-row (fn [row-id row]
                     (for [[col-idx col-name] col-order]
                       {:row row-id :col col-idx :val (get row col-name) :col-name col-name}))]
    (tablep/spec-with-data (mapcat make-row (range) rows))))

(defn demo-partioned-table-plot []
  (let [row-group-1 (repeatedly 20 count-model)
        row-group-2 (repeatedly 15 count-model)
        row-group-3 (repeatedly 5 count-model)
        all-groups [row-group-1 row-group-2 row-group-3]

        colors [["blue" "lightblue"] ["green" "lightgreen"] ["firebrick" "salmon"]]]
    (tablep/spec-with-mult-partitions all-groups colors)))

(defn demo-partioned-table-plot-2 []
  (let [all-groups [data/data-subset-clj data/data-subset-not-clj]
        colors [["blue" "lightblue"] ["green" "lightgreen"]]]
    (tablep/spec-with-mult-partitions all-groups colors)))

;(def rows (repeatedly 10 count-model)
;(simple-table-plot rows)

;(demo-partioned-table-plot)
;(demo-partioned-table-plot-2)

;(tracep/view-trace (second (infer-and-score :procedure count-model)))

(defn print-row [row]
  (let [str-reducer (fn [str-accum col-name col-val]
                      (let [part (str "\"" col-name "\" " col-val " ")]
                        (str str-accum part)))]
    (println (str "{" (string/trim (reduce-kv str-reducer "" row)) "}"))))

;(def demo-data (repeatedly 10 count-model))
;(simple-table-plot demo-data)
;(map print-row demo-data)

(defn write-data-to-csv [data-maps filename]
  (let [col-names (keys (first data-maps))
        data (map vals data-maps)
        all-rows (cons col-names data)]
    (with-open [writer (io/writer filename)]
      (csv/write-csv writer all-rows))))

;(write-data-to-csv data/data-subset "presentation/data.csv")
;(write-data-to-csv data/data-subset-clj "presentation/data-clj.csv")
;(write-data-to-csv data/data-subset-not-clj "presentation/data-not-clj.csv")
