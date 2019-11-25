(ns inferenceql.spreadsheets.clojure-conj.models.cond-count-simple
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
   [inferenceql.spreadsheets.clojure-conj.trace-plotting.core :as tracep]
   [inferenceql.spreadsheets.clojure-conj.table-plotting :as tablep]
   [inferenceql.multimixture.basic-queries :as bq]
   [inferenceql.multimixture :as mmix]
   [inferenceql.spreadsheets.clojure-conj.models.cond-count :refer [make-cond-count-model]]))

(def clj-row-gen
  (gen []
    (let [col-probs {"AWS" 0.40
                     "Clojure" 1.0
                     "Java" 0.25}
          vals (for [[col prob] col-probs]
                 (at `(:columns ~col) flip prob))]
      (zipmap (keys col-probs) vals))))

(def no-clj-row-gen
  (gen []
    (let [col-probs {"AWS" 0.80
                     "Clojure" 0
                     "Java" 0.90}
          vals (for [[col prob] col-probs]
                 (at `(:columns ~col) flip prob))]
      (zipmap (keys col-probs) vals))))

(def cond-count-model-row-gen
  (gen []
    (let-traced [clojure-prob 0.20
                 clojure-dev (flip clojure-prob)]
      (if clojure-dev
        (at '() clj-row-gen)
        (at '() no-clj-row-gen)))))

(def cond-count-model (make-cond-count-model cond-count-model-row-gen))

;----------------------------------

;; Testing simulate()

(defn get-frequency-of
  "Utility function for testing"
  [rows col-name]
  (let [results (frequencies (map #(get % col-name) rows))
        total (reduce + (vals results))]
    (double (/ (get results true) total))))

(def cc-model-test (bq/simulate cond-count-model {"Clojure" false} 5000))
(get-frequency-of cc-model-test "AWS")
;; 0.8

(def cc-model-test (bq/simulate cond-count-model {"AWS" true} 5000))
(get-frequency-of cc-model-test "Clojure")

(def cc-model-test (bq/simulate cond-count-model {"AWS" true} 5000))
(get-frequency-of cc-model-test "Java")

;----------------------------------

;; Testing logpdf()

(exp (bq/logpdf cond-count-model {"Clojure" true} {}))

(exp (bq/logpdf cond-count-model {"AWS" true} {}))
(+ (* 0.4 0.2) (* 0.8 0.8))

(exp (bq/logpdf cond-count-model {"Java" true} {}))
(+ (* 0.25 0.2) (* 0.9 0.8))

(exp (bq/logpdf cond-count-model {"AWS" true} {"Clojure" true}))
;; 0.4

(exp (bq/logpdf cond-count-model {"AWS" true} {"Clojure" false}))
;; 0.8

(exp (bq/logpdf cond-count-model {"AWS" true} {"Java" true}))
;; p(Java)
(+ (* 0.25 0.2) (* 0.9 0.8))
;; p(AWS, Java)
(+ (* 0.25 0.4 0.2) (* 0.9 0.8 0.8))
;; p(AWS, Java) / P(Java)
(/ (+ (* 0.25 0.4 0.2) (* 0.9 0.8 0.8))
   (+ (* 0.25 0.2) (* 0.9 0.8)))

(exp (bq/logpdf cond-count-model {"Clojure" true} {"AWS" true}))
;; p(Clojure, AWS) / p(AWS)
(/ (* 0.4 0.2)
   (+ (* 0.4 0.2) (* 0.8 0.8)))
