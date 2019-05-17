(ns inferdb.data-generators
  "The purpose of this file is to provide ground truth solutions; for both
  synthetic data and the corresponding quantitities like logPDF, MI, KL, etc.

  We also want to test certain properties of the function that are supposed to
  compute ground truth values. "
  (:refer-clojure :exclude [apply replicate])
  (:require [clojure.test :refer [deftest testing is use-fixtures]]
            [clojure.string :refer [index-of]]
            [metaprob.distributions :as dist]
            [metaprob.prelude]
            [inferdb.metrics :as metrics]
            [inferdb.utils :as utils]))


;; XXX: this code should probably not live here.
(def random-seed 42)
(defn fix-random-seed
  [run-test]
  (binding [metaprob.prelude/*rng* (java.util.Random. random-seed)]
    (run-test)))

(use-fixtures :once fix-random-seed)

(defn p-gaussian [x parameter]
  "Compute Gaussian likelihood score."
  (Math/exp (dist/score-gaussian x [(:mu parameter) (:sigma parameter)])))

(defn p-gmm [observed parameters]
  "Compute an unconditional likelihood score for a Gaussian mixture model."
  (reduce +
          (map (fn [parameter]
                   (* (first parameter)
                      (reduce *
                              (map (fn [x-and-dim-param] (let
                                                           ;; XXX: line break :(
                                                           [x (first x-and-dim-param)
                                                            param (second x-and-dim-param)]
                                                           (p-gaussian x param)))
                                   (zipmap observed (rest parameter))))))
                 parameters)))


;; Define some global test parameters.
(def threshold 0.0001)

(def gmm-parameters
  [[0.1 {:mu -10 :sigma 1} {:mu -10 :sigma 1}]
   [0.8 {:mu   0 :sigma 1} {:mu   0 :sigma 1}]
   [0.1 {:mu  10 :sigma 1} {:mu  10 :sigma 1}]])

(deftest fixed-rand-seed
  (testing "whether we can fix the rand seed."
    (let [x (binding [metaprob.prelude/*rng* (java.util.Random. 42)]
              (dist/uniform 0 1))
          y (dist/uniform 0 1)]
      (is (= x y)))))

(deftest check-gaussian-scorer
  (testing "whether the Gaussian scorer works"
    (let [true-probability 0.3989422804014327] ;; Computed with scipy stats.
      (is (utils/almost-equal (p-gaussian 0 {:mu 0 :sigma 1})
                              true-probability
                              metrics/relerr threshold)))))

(deftest check-gmm-scorer
  (testing "whether the the joint GMM scorer works"
    (let [isotropic-biv-normal-params [[0.5 {:mu 0 :sigma 1} {:mu 0 :sigma 1}]
                                       [0.5 {:mu 0 :sigma 1} {:mu 0 :sigma 1}]]
          computed-probability (p-gmm [0 1] isotropic-biv-normal-params)
          true-probability  0.09653235263005393] ;; Computed with scipy stats.
      (is (utils/almost-equal computed-probability
                              true-probability
                              metrics/relerr threshold)))))

;; TODO: add tests for test harness, test against itself, and test against
;; fixed, analytical value.
