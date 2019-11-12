(ns inferenceql.spreadsheets.clojure-conj.models.count
  (:refer-clojure :exclude [map replicate apply])
  #?(:cljs (:require-macros [metaprob.generative-functions :refer [gen let-traced]]))
  (:require
   [metaprob.prelude :as mp :refer [infer-and-score map replicate apply exp]]
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
   [inferenceql.multimixture :as mmix]))

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
                 (at `(:columns ~col) flip prob))]
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

(defn demo-simple-table-plot [n]
  (let [rows (repeatedly n count-model)
        col-order (->> (keys (first rows))
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

;(demo-simple-table-plot 20)
;(demo-partioned-table-plot)
;(demo-partioned-table-plot-2)

;(tracep/view-trace (second (infer-and-score :procedure count-model)))
