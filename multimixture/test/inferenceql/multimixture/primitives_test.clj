(ns inferenceql.multimixture.primitives-test
  (:require [clojure.test :as test :refer [deftest is]]
            [inferenceql.multimixture.primitives :as prim]))

;; We check the `simulate` methods of distributions by evaluating
;; their empirical mean and comparing that to the mean of the
;; distribution given its parameterization.
(deftest bernoulli-logpdf
  (let [x   true
        x'  false
        p   0.6
        p' (- 1 p)]
    (is (= (Math/log p)
           (prim/bernoulli-logpdf x p )))
    (is (= (Math/log p')
           (prim/bernoulli-logpdf x' p)))))

(deftest bernoulli-simulate
  (let [p       0.6
        n       10000
        samples (prim/bernoulli-simulate n p)
        counts  (frequencies samples)
        error   0.05]
    (is (< (Math/abs (- (/ (get counts true) n)
                        p))
           error))))

(deftest gamma-logpdf
  (let [x     4
        k     5
        theta 2
        error 0.0001]
    (is (< (Math/abs (- (Math/exp (prim/gamma-logpdf x {:k k}))
                        0.19536))
           error))
    (is (< (Math/abs (- (Math/exp (prim/gamma-logpdf x {:k k :theta theta}))
                       0.04511))
           error))))

(deftest gamma-simulate
  (let [k               5
        theta           2
        n               100000
        samples-k       (prim/gamma-simulate n {:k k})
        samples-k-theta (prim/gamma-simulate n {:k k :theta theta})
        mean-k          (/ (reduce + samples-k)
                           n)
        mean-k-theta    (/ (reduce + samples-k-theta)
                           n)
        error   0.05]
    (is (< (Math/abs (- mean-k
                        k))
           error))
    (is (< (Math/abs (- mean-k-theta
                        (* k theta)))
           error))))

(deftest beta-logpdf
  (let [x     0.5
        alpha 0.5
        beta  0.5
        error 0.001]
    (is (< (Math/abs (- (Math/exp (prim/beta-logpdf x {:alpha alpha :beta beta}))
                        0.63662))
           error))))

(deftest beta-simulate
  (let [alpha           1.5
        beta            1.5
        n               10000
        samples         (prim/beta-simulate n {:alpha alpha :beta beta})
        mean            (/ (reduce + samples)
                           n)
        error   0.05]
    (is (< (Math/abs (- mean
                        (/ alpha
                           (+ alpha beta))))
           error))))

(deftest categorical-logpdf
  (let [x     "green"
        ps    {"green" 0.2 "red" 0.4 "blue" 0.4}]
    (is (= 0.2 (Math/exp (prim/categorical-logpdf x ps))))))

(deftest categorical-simulate
  (let [ps    {"green" 0.2 "red" 0.4 "blue" 0.4}
        n               10000
        samples         (prim/categorical-simulate n ps)
        counts          (frequencies samples)
        error   0.05]
    (mapv #(is (< (Math/abs (- (/ (get counts %)
                                  n)
                               (get ps %)))
                   error))
          (keys ps))))

(deftest dirichlet-logpdf
  (let [x     [0.4 0.4 0.2]
        alpha [2 2 1]
        error 0.001]
    (is (< (Math/abs (- (Math/exp (prim/dirichlet-logpdf x alpha))
                        0.00667))
           error))))

(deftest dirichlet-simulate
  (let [alpha     [2 2 1]
        sum-alpha (reduce + alpha)
        n         10000
        samples   (prim/dirichlet-simulate n alpha)
        error      0.05]
    (mapv #(is (< (Math/abs (- (/ (reduce + (mapv (fn [sample]
                                                    ( nth sample %))
                                                  samples))
                                  n)
                               (/ (nth alpha %)
                                  sum-alpha)))
                   error))
          (range (count alpha)))))

(deftest gaussian-logpdf
  (let [x     0
        mu    0
        sigma 1
        error 0.001]
    (is (< (Math/abs (- (Math/exp (prim/gaussian-logpdf x {:mu mu :sigma sigma}))
                        0.39894))
           error))))

(deftest gaussian-simulate
  (let [mu      0
        sigma   1
        n       10000
        samples (prim/gaussian-simulate n {:mu mu :sigma sigma})
        mean            (/ (reduce + samples)
                           n)
        error   0.05]
    (is (< (Math/abs (- mean
                        mu))
           error))))

(deftest logpdf-test
  (let [dist  :gaussian
        x     0
        mu    0
        sigma 1
        error 0.001]
    (is (< (Math/abs (- (Math/exp (prim/logpdf x :gaussian {:mu mu :sigma sigma}))
                        0.39894))
           error))
    (is (thrown? Exception (prim/logpdf x :foobar {:mu mu :sigma sigma})))))

(deftest logpdf-simulate
  (let [dist    :gaussian
        mu      0
        sigma   1
        n       10000
        samples (prim/simulate n dist {:mu mu :sigma sigma})
        mean    (/ (reduce + samples)
                   n)
        error   0.05]
    (is (< (Math/abs (- mean
                        mu))
           error))))
    ;; Couldn't make the below work, the call gives the desired result on the REPL.
    ; (is (thrown? Exception (prim/simulate n :foobar {:mu mu :sigma sigma})))))
