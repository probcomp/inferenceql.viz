(ns inferdb.aide-test
  (:refer-clojure :exclude [map apply replicate])
  (:require
   [metaprob.generative-functions :refer [gen let-traced]]
   [metaprob.prelude :refer [map]]
   [inferdb.aide.main :as aide]
   [metaprob.distributions :as dist]
   [metaprob.trace :as trace]
   [clojure.test :refer [is deftest]]))

(def coin-model
  (gen [n]
    (let-traced [p (dist/beta 1 1)]
      (map (fn [i] (at i dist/flip p)) (range n)))))

(defn make-approx-inference-algorithm
  [n observations n-particles]
  (gen []
    (at '() aide/importance-resampling-gf coin-model [n] observations n-particles)))

(defn exact-inference [n observations]
  (let [all-flips (filter boolean? (map (fn [addr] (trace/trace-value
                                                    observations addr))
                                        (trace/addresses-of observations)))
        heads (count (filter true? all-flips))
        tails (count (filter false? all-flips))]
    (gen []
      (let [p (at '("inferred-trace" "p") dist/beta (inc heads) (inc tails))]
        (doseq [i (range n)]
          (when (not (trace/trace-has-value? observations i))
            (at `("inferred-trace" ~i) dist/flip p)))))))

(defn aide-demo [n observations]
  (map (fn [i]
         (aide/compare-generative-functions
          (exact-inference n observations)
          (make-approx-inference-algorithm n observations i)
          '(("inferred-trace" "p"))
          100 1
          100 20))
       [1 2 3 5]))

;; Create an observation trace specifying we saw 7 heads and 3 tails
(def seven-heads
  (into {} (map-indexed
            (fn [i x] [i {:value x}])
            (concat (repeat 7 true) (repeat 3 false)))))

(deftest test-seven-heads
  (is (clojure.core/apply >= (aide-demo 10 seven-heads))))
