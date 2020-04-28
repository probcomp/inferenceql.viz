(ns inferenceql.multimixture.crosscat.specification-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :as test :refer [deftest testing is]]
            [expound.alpha :as expound]
            [clojure.test.check :as tc]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.generators :as gen]
            [inferenceql.multimixture.crosscat.specification :as spec]))


;; Smoke test for positive number generator.
(defspec pos-number 1000
  (prop/for-all [number (spec/pos-number)]
    (and (> number 0)
         (not (Double/isNaN      number))
         (not (Double/isInfinite number)))))

;; Smoke test for nonnegative number generator.
(defspec nonneg-number 1000
  (prop/for-all [number (spec/nonneg-number)]
    (and (>= number 0)
         (not (Double/isNaN      number))
         (not (Double/isInfinite number)))))

;; Smoke test for number generator.
(defspec number 1000
  (prop/for-all [number (spec/number)]
    (and (number? number)
         (not (Double/isNaN      number))
         (not (Double/isInfinite number)))))

;; Smoke test for probability generator.
(defspec prob 1000
  (prop/for-all [number (spec/prob)]
    (and (number? number)
         (< 0 number 1)
         (not (Double/isNaN      number))
         (not (Double/isInfinite number)))))

;; Smoke test for probability spec.
(defspec probability-spec 1000
  (prop/for-all [prob (s/gen ::spec/probability)]
    (and (number? prob)
         (< 0 prob 1))))

;; Smoke test for probability distribution spec.
(defspec probability-distribution-spec 100
  (prop/for-all [dist (s/gen ::spec/probability-distribution)]
    (and (every? #(< 0 % 1) dist)
           (< (Math/abs (- 1 (reduce + dist)))
                  1e-7))))

;; Smoke test for bernoulli distribution spec.
(defspec bernoulli-spec 50
  (prop/for-all [p              (s/gen        :bernoulli/p)
                 parameters     (gen/hash-map :p (s/gen :bernoulli/p))
                 parameter-list (s/gen        :bernoulli/parameters-list)]
    (and (and (number? p) (< 0 p 1))
         (and (contains?  parameters :p)
              (= 1 (count parameters)))
         (and (some #{:p} parameter-list)
              (= 1 (count parameter-list))))))

;; Smoke test for beta distribution spec.
(defspec beta-spec 50
  (prop/for-all [alpha          (s/gen        :beta-dist/alpha)
                 beta           (s/gen        :beta-dist/beta)
                 parameters     (gen/hash-map :alpha (s/gen :beta-dist/alpha)
                                              :beta  (s/gen :beta-dist/beta))
                 parameter-list (s/gen        :beta-dist/parameters-list)]
    (and (and (number? alpha)
              (< 0 alpha)
              (not (Double/isNaN      alpha))
              (not (Double/isInfinite alpha)))
         (and (number? beta)
              (< 0 beta)
              (not (Double/isNaN      beta))
              (not (Double/isInfinite beta)))
         (and (contains?  parameters :alpha)
              (contains?  parameters :beta)
              (= 2 (count parameters)))
         (and (some #{:alpha} parameter-list)
              (some #{:beta}  parameter-list)
              (= 2 (count parameter-list))))))

;; Smoke test for categorical distribution spec.
(defspec categorical-spec 50
  (prop/for-all [option         (s/gen        :categorical/option)
                 p              (s/gen        :categorical/p)
                 parameters     (gen/hash-map :p (s/gen :categorical/p))
                 parameter-list (s/gen        :categorical/parameters-list)]
    (and (or (string? option)
             (and (integer? option)
                  (not (neg? option))))
         (every? (fn [[k v]] (and (or (string? k)
                                      (and (integer? k)
                                           (not (neg? k))))
                                  (< 0 v 1))) p)
         (and (contains?  parameters :p)
              (= 1 (count parameters)))
         (and (some #{:p} parameter-list)
              (= 1 (count parameter-list))))))

;; Smoke test for dirichlet distribution spec.
(defspec dirichlet-spec 50
  (prop/for-all [alpha          (s/gen        :dirichlet/alpha)
                 parameters     (gen/hash-map :alpha (s/gen :dirichlet/alpha))
                 parameter-list (s/gen        :dirichlet/parameters-list)]
    (and (every? #(and (pos? %)
                       (number? %)
                       (and (not (Double/isNaN %))
                            (not (Double/isInfinite %))))
                 alpha)
         (and (contains?  parameters :alpha)
              (= 1 (count parameters)))
         (and (some #{:alpha} parameter-list)
              (= 1 (count parameter-list))))))

;; Smoke test for gamma distribution spec.
(defspec gamma-spec 50
  (prop/for-all [k              (s/gen        :gamma/k)
                 theta          (s/gen        :gamma/theta)
                 parameters     (gen/hash-map :k     (s/gen :gamma/k)
                                              :theta (s/gen :gamma/theta))
                 parameter-list (s/gen        :gamma/parameters-list)]
    (and (and (number? k)
              (not (Double/isNaN      k))
              (not (Double/isInfinite k)))
         (and (number? theta)
              (not (Double/isNaN      theta))
              (not (Double/isInfinite theta)))
         (and (contains?  parameters :k)
              (contains?  parameters :theta)
              (= 2 (count parameters)))
         (and (some #{:k} parameter-list)
              (some #{:theta}  parameter-list)
              (= 2 (count parameter-list))))))

;; Smoke test for gaussian distribution spec.
(defspec gaussian-spec 50
  (prop/for-all [mu             (s/gen        :gaussian/mu)
                 sigma          (s/gen        :gaussian/sigma)
                 parameters     (gen/hash-map :mu    (s/gen :gaussian/mu)
                                              :sigma (s/gen :gaussian/sigma))
                 parameter-list (s/gen        :gaussian/parameters-list)]
    (and (and (number? mu)
              (not (Double/isNaN      mu))
              (not (Double/isInfinite mu)))
         (and (number? sigma)
              (not (Double/isNaN      sigma))
              (not (Double/isInfinite sigma)))
         (and (contains?  parameters :mu)
              (contains?  parameters :sigma)
              (= 2 (count parameters)))
         (and (some #{:mu} parameter-list)
              (some #{:sigma}  parameter-list)
              (= 2 (count parameter-list))))))
