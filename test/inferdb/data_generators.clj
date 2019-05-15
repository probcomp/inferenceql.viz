(ns inferdb.data_generators
  (:refer-clojure :exclude [apply replicate])
  (:require [clojure.pprint :refer [pprint]]
            [clojure.repl :refer [doc source]]
            [clojure.test :refer :all]
            [clojure.string :refer [index-of]]
            [metaprob.distributions :refer :all]
            [inferdb.utils :refer :all]))

(deftest fixed-rand-seed
  (testing "whether we can fix the rand seed."
    (let [x (uniform 0 1)
          y (binding [metaprob.prelude/*rng* (java.util.Random. 42)]
              (uniform 0 1))]
    (is (= x y)))))

(def threshold 0.1)

(defn p-gaussian [x mu sigma]
  (Math/exp (score-gaussian x [mu sigma])))

(deftest check-gaussian-scorer
  (testing "whether the Gaussian scorer works"
    (let [true-probability 0.3989422804014327] ;; Computed with scipy stats.
      (is (almost-equal (p-gaussian 0 0 1) true-probability threshold)))))



(defn p-gaussian [x parameter]
  (Math/exp (score-gaussian x [(:mu parameter) (:sigma parameter)])))


;; XXX: this only works with a scalar x right now (that get's interpreted as [x x])
;; need to do some enumerate shit here...
(defn p-gmm [x parameters]
  (reduce + (map
              (fn [parameter] (* (first parameter)
                                 (reduce * (map (fn [param] (p-gaussian x param))
                                                 (rest parameter)))))
              parameters)))

(def gmm-parameters
  [[0.1 {:mu -10 :sigma 1}]
   [0.8 {:mu   0 :sigma 1}]
   [0.1 {:mu  10 :sigma 1}]])

;; Test the above.
;; (p-gmm 10 gmm-parameters)
