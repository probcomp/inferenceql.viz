(ns inferdb.analytical-solutions-mixture-test
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
  (if (nil? x)
    1.
    (Math/exp (dist/score-gaussian x [(:mu parameter) (:sigma parameter)]))))

(defn p-categorical [x parameter]
  "Compute categorical likelihood score."
  (if (nil? x)
    1.
    (nth (:p parameter) x)))

(defn choose-p-scorer [parameter]
 (if (contains? parameter :mu)
   p-gaussian
   p-categorical))

(defn p-mixture-model [observed parameters]
  "Compute an unconditional likelihood score for a Gaussian mixture model."
  (reduce +
          (map (fn [parameter]
                   (* (first parameter)
                      (reduce *
                              (map (fn [x-and-dim-param] (let
                                                           ;; XXX: line break :(
                                                           [x (first x-and-dim-param)
                                                            param (second x-and-dim-param)
                                                            p-scorer (choose-p-scorer param)]
                                                           (p-scorer x param)))
                                   (zipmap observed (rest parameter))))))
                 parameters)))


;; Compute P(X | Y) = P(X | C) * P(C | Y); where C is the cluster assignemnt.
;; We thus need  to compute P(C | Y) and then use that quantity to update the
;; cluster to get P(X | Y).
;; P(C | Y) \approx P(Y | C) * P(C).
;; To turn the \approx into `=` we simply need to normalize to 1.
(defn re-weight-cluster-assignment
  [cluster constraints]
  "Re-weight an individual component in a mixture model."
  ;; XXX: this currently only works with univariate constraints.
  (let [params (nth (rest cluster) (first (keys constraints)))
        y (first (vals constraints))
        p_k (first cluster)
        p-scorer (choose-p-scorer params)
        p_y_given_k (p-scorer y params)
        ]
  (* p_k p_y_given_k)))

(defn re-weight-cluster-assignments
  [gmm  constraints]
  "Take a gmm and return a new, updated gmm to compute p-mixture-model"
  (let [
        get-weights (fn [cluster]
                      (re-weight-cluster-assignment cluster constraints))
        unnormalized_p_given_y (map get-weights gmm)
        ]
  (utils/normalize unnormalized_p_given_y)))

(defn update-gmm-weights [gmm weights]
  (let [replace-weights (fn [i] i (assoc (nth gmm i) 0 (nth weights i)))]
    (map replace-weights (range (count weights)))))


;; XXX: (again) this currently only works with univariate constraints.
(defn p-mixture-model-conditional [observed parameters constraints]
  "Compute an conditional likelihood score for a Gaussian mixture model."
  (let [new-cluster-weights (re-weight-cluster-assignments parameters constraints)
        new-gmm-representation (update-gmm-weights parameters new-cluster-weights)]
    (p-mixture-model observed new-gmm-representation)))

;; Define some global test parameters.
(def threshold 0.0001)


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
          computed-probability (p-mixture-model [0 1] isotropic-biv-normal-params)
          true-probability  0.09653235263005393] ;; Computed with scipy stats.
      (is (utils/almost-equal computed-probability
                              true-probability
                              metrics/relerr threshold)))))

;; Define a GMM for testing.
(def gmm-parameters
  [[0.1 {:mu -10 :sigma 1} {:mu -10 :sigma 1}]
   [0.8 {:mu   0 :sigma 1} {:mu   0 :sigma 1}]
   [0.1 {:mu  10 :sigma 1} {:mu  10 :sigma 1}]])

(deftest re-weighted-clusters
  (testing "whether we correctly re-weight cluster probabilities."
    (let [expected [1. 0. 0.]
          computed (re-weight-cluster-assignments gmm-parameters {1 -10})]
    (is (utils/almost-equal-vectors
          expected
          computed
          metrics/relerr threshold)))))

(deftest replace-weights
  (testing "whether we correctly replace cluster weights/mixing coefficients in a GMM."
    (let [expected-reweighted-gmm [
                                   [0.2 {:mu -10 :sigma 1} {:mu -10 :sigma 1}]
                                   [0.3 {:mu   0 :sigma 1} {:mu   0 :sigma 1}]
                                   [0.5 {:mu  10 :sigma 1} {:mu  10 :sigma 1}]]
          computed-reweighted-gmm (update-gmm-weights gmm-parameters [0.2 0.3 0.5])]
    (is (= expected-reweighted-gmm computed-reweighted-gmm)))))

(deftest conditional-gmm
  (testing "whether if we conditioning on one column val"
     ;; by putting a lot of probability pressure on cluster one, we now the
     ;; problem reduces to scoring a single Gaussian.
    (let [observed [-10 nil]
          expected-probability (p-gaussian (first observed) {:mu -10 :sigma 1})
          computed-probability (p-mixture-model-conditional observed
                                                  gmm-parameters
                                                  {1 -10})]
      (is (utils/almost-equal computed-probability
                              expected-probability
                              metrics/relerr threshold)))))
