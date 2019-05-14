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


(defn p-gmm-1d [x parameters]
  (reduce + (map
              (fn [parameter] (* (:p parameter)
                                 (p-gaussian x
                                             (:mu parameter)
                                             (:sigma parameter))))
              parameters)))

(def gmm-1d-parameters
  [{:p 0.1 :mu -10 :sigma 1}
   {:p 0.8 :mu   0 :sigma 1}
   {:p 0.1 :mu  10 :sigma 1}])

(p-gmm-1d 10 gmm-1d-parameters)
