(ns inferenceql.multimixture.search.gibbs-search-test
  (:require [clojure.test :as test :refer [deftest testing is]]
            [inferenceql.multimixture.utils :as mmix-utils]
            [inferenceql.multimixture.search.gibbs :as gibbs]))

;; Gibbs sampling tests.
(deftest sample-cluster-assignments
  "Checks that sampled values of clusters are within reason."
  (let [probs      [[0.1 0.3 0.6]
                    [0.4 0.2 0.4]]
        n-clusters (count (first probs))
        total-el   (* n-clusters (count probs))
        sampled    (->> (fn [] (gibbs/sample-cluster-assignments probs))
                        (repeatedly 1000)
                        (mmix-utils/transpose)
                        (mapv #(->> (frequencies %)
                                    (into (sorted-map))
                                    (mapv second)))
                        (mapv #(let [sum (apply + %)]
                                 (mapv (fn [el] (/ el sum)) %))))]
    ;; Sums the absolute residuals between empirical distribution
    ;; and true distribution, and checks that it is below the
    ;; threshhold.
    (is (< (/ (->> (map (fn [prob sample]
                          (->> (map - prob sample)
                               (map #(is (< (Math/abs %) 0.01))))
                          probs sampled))
                   (flatten)
                   (map #(max % (- %)))
                   (apply +))
              total-el)
           0.01))))

(deftest cluster-assignments->thetas
  "Checks that given cluster assignments for all rows, the posterior
  probabilities are correctly calculated."
  (let [diff-assign  [0 1]
        same-assign  [0 0]
        beta-params  {:alpha 0.5 :beta 0.5}
        n-clusters   2
        labels       [true false]
        diff-thetas  (gibbs/cluster-assignments->thetas
                      diff-assign
                      beta-params
                      n-clusters
                      labels)
        same-thetas  (gibbs/cluster-assignments->thetas
                      same-assign
                      beta-params
                      n-clusters
                      labels)]
    ;; Assignments contains both clusters.
    (is (= [0.75 0.25] diff-thetas))
    ;; Assignments contain one cluster.
    (is (= [0.50 0.50] same-thetas))))

(deftest thetas->pred-probs
  "Verifies the mapping from cluster configuration to cluster posterior."
  (let [unknown-clusters [0 1 0]
        thetas           [0.3 0.7]]
    (is (= [0.3 0.7 0.3] (gibbs/thetas->pred-probs
                          unknown-clusters
                          thetas)))))

(deftest gibbs-search
  "Smoke test for Gibbs row sampling search."
  ;; Gibbs search works as the following.
  ;;  0. Generate probability table given the spec and rows.
  ;;  1. For iter = 1 .. iters:
  ;;    a. Resample cluster assignments for all rows.
  ;;        c_i ~ Categorical(prob-table[row_i])
  ;;    b. Calculate predictive probabilities based on updates.
  ;;        alpha_k' = alpha_k + # True obs. in cluster k
  ;;        beta_k'  = beta_k  + # False obs. in cluster k
  ;;        pred_prob = alpha_k' / (alpha_k' + beta_k')
  ;;    c. Record pred. prob.
  ;;  2. Return sum(pred. probs) / iters.
  (let [spec          {:vars {"x" :gaussian}
                       :views [[{:probability 0.5
                                 :parameters {"x" {:mu 1 :sigma 1}}}
                                {:probability 0.5
                                 :parameters {"x" {:mu 4 :sigma 1}}}]]}
        unknown-rows [{"x" 0}
                      {"x" 5}]
        known-rows   [{"x" 0 "y" true}
                      {"x" 5 "y" false}]
        beta-params   {:alpha 0.5 :beta 0.5}
        expected     [0.75 0.25]
        actual       (gibbs/search
                      spec
                      "y"
                      known-rows
                      unknown-rows
                      beta-params)]
    ;; Checks that sum of residuals is less than the threshhold.
    (is (< (apply + (map (comp #(max % (- %)) -) expected actual)) 0.01))))
