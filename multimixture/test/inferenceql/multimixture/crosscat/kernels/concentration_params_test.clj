(ns inferenceql.multimixture.crosscat.kernels.concentration-params-test
  (:require [clojure.test :as test :refer [deftest is]]
            [inferenceql.multimixture.crosscat.kernels.concentration-params :as cp]
            [inferenceql.multimixture.primitives :as prim]
            [inferenceql.multimixture.utils :as mmix-utils]
            [clojure.spec.alpha :as s]
            [inferenceql.multimixture.crosscat.specification :as xcats]))

(deftest alpha-scores
  "Tests `alpha-scores` by verifying the output is the same length as the
  specified grid. Checks with two different sizes of grids. The grid must
  be non-empty."
  (let [counts         [1 1 1 1 1 1]
        n              (reduce + counts)
        n-points-single 1
        grid-single     (mmix-utils/loglinspace (/ 1 n) n n-points-single)
        scores-single   (cp/alpha-scores grid-single counts)
        n-points-many   100
        grid-many       (mmix-utils/loglinspace (/ 1 n) n n-points-many)
        scores-many     (cp/alpha-scores grid-many counts)]

    (is (= n-points-single (count scores-single)))
    (is (coll? scores-single))
    (is (every? #(< 0 (Math/exp %) 1) scores-single))

    (is (= n-points-many (count scores-many)))
    (is (coll? scores-many))
    (is (every? #(< 0 (Math/exp %) 1) scores-many))))

(deftest alpha-sample
  "Tests `alpha-sample` by running many times, creating an empirical
  distribution and verifying it against the sampled distribution (the scores),
  which are intentionally unnormalized."
  (let [grid        [0 1 2]
        probs-tilde [0.9 0.05 0.05]
        Z           (reduce + probs-tilde)
        probs       (map #(/ % Z) probs-tilde)
        scores      (map #(Math/log %) probs-tilde)
        iters       1000
        freqs       (frequencies (repeatedly iters #(cp/alpha-sample grid scores)))
        error       0.05]

    (mapv (fn [[grid-val freq]]
            (is (< (Math/abs (- (/ freq iters)
                                (nth probs grid-val)))
                   error)))
          freqs)))

(deftest kernel-group
  "Tests `kernel-group` with three different cases of groupings and checking the average
  value of the newly sampled alpha."
  (let [counts-single [100]
        counts-sparse [1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1]
        counts-mixed  [4 1 2 5 1 2 4]
        iters         1000
        alpha'-single (/ (reduce + (repeatedly iters #(cp/kernel-group counts-single)))
                         iters)
        alpha'-sparse (/ (reduce + (repeatedly iters #(cp/kernel-group counts-sparse)))
                         iters)
        alpha'-mixed  (/ (reduce + (repeatedly iters #(cp/kernel-group counts-mixed)))
                         iters)]

    ;; A single group should result in a small value of alpha.
    (is (< alpha'-single 0.1))

    ;; A very sparse grouping should results in a large value of alpha.
    (is (> alpha'-sparse 10))

    ;; A mixed/roughly uniform grouping should result in a medium value of alpha.
    (is (< 2 alpha'-mixed 10))))

(deftest kernel
  "Tests `kernel` with three different cases of groupings and checking the average
  value of the newly sampled alpha, and verifying the data structure is properly updated."
  (let [counts-single [100]
        counts-sparse [1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1]
        counts-mixed  [4 1 2 5 1 2 4]
        latents       {:global {:alpha  1  ; These values of alpha are meaningless in this context.
                                :counts counts-single
                                :z      (zipmap (map str (range 100))
                                                (repeat 100 0))}
                       :local [{:alpha  5
                                :counts counts-sparse
                                :y      (range (count counts-sparse))}
                               {:alpha  10
                                :counts counts-mixed
                                :y      [0 3 3 1 0 5 2 0 2 3 5 4 6 0 6 3 6 3 6]}]}
        iters         1000
        latents'      (repeatedly iters #(cp/kernel latents))

        global-avg   (/ (reduce + (map #(get-in % [:global :alpha]) latents'))
                        iters)

        local1-avg   (/ (reduce + (map #(get-in % [:local 0 :alpha]) latents'))
                        iters)

        local2-avg   (/ (reduce + (map #(get-in % [:local 1 :alpha]) latents'))
                        iters)]

    ;; Checking test arguments.
    (is (xcats/valid-latents? latents))

    ;; A single group should result in a small value of alpha.
    (is (< global-avg 0.1))

    ;; A very sparse grouping should results in a large value of alpha.
    (is (> local1-avg 10))

    ;; A mixed/roughly uniform grouping should result in a medium value of alpha.
    (is (< 2 local2-avg 10))))
