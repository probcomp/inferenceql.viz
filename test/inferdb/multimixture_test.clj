(ns inferdb.multimixture-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [metaprob.distributions :refer :all]
            [inferdb.cgpm.main :refer :all]
            [inferdb.utils :refer :all]
            [inferdb.plotting.generate-vljson :refer :all]
            [inferdb.multimixture.dsl :refer :all]))

(defn make-identity-output-addr-map
  [output-addrs-types]
  (let [output-addrs (keys output-addrs-types)
        trace-addrs  (map clojure.core/name output-addrs)]
    (clojure.core/zipmap output-addrs trace-addrs)))

(def generate-crosscat-row
  (multi-mixture
    (view
      {"x" gaussian
       "y" gaussian
       "a" categorical
       "b" categorical}
      (clusters
       0.166666666 {"x" [3 1]
                    "y" [4 0.1]
                    "a" [[1 0 0 0 0 0]]
                    "b" [[0.95 0.01 0.01 0.01 0.01 0.01]]}
       0.166666666 {"x" [3 0.1]
                    "y" [4 1]
                    "a" [[0 1 0 0 0 0]]
                    "b" [[0.01 0.95 0.01 0.01 0.01 0.01]]}
       0.166666667 {"x" [8 0.5]
                    "y" [10 1]
                    "a" [[0 0 1 0 0 0]]
                    "b" [[0.01 0.01 0.95 0.01 0.01 0.01]]}
       0.166666666 {"x" [14 0.5]
                    "y" [7 0.5]
                    "a" [[0 0 0 1 0 0]]
                    "b" [[0.01 0.01 0.01 0.95 0.01 0.01]]}
       0.166666666 {"x" [16 0.5]
                    "y" [9 0.5]
                    "a" [[0 0 0 0 1 0]]
                    "b" [[0.01 0.01 0.01 0.01 0.95 0.01]]}
       0.166666666 {"x" [9  2.5]
                    "y" [16 0.1]
                    "a" [[0 0 0 0 0 1]]
                    "b" [[0.01 0.01 0.01 0.01 0.01 0.95]]}))
    (view
      {"z" gaussian
       "c" categorical}
      (clusters
       0.25 {"z" [0 1]
             "c" [[1 0 0 0]]}
       0.25 {"z" [10 1]
             "c" [[0 1 0 0]]}
       0.25 {"z" [20 1]
             "c" [[0 0 1 0]]}
       0.25 {"z" [30 1]
             "c" [[0 0 0 1]]}))))

(def crosscat-cgpm
  (let [outputs-addrs-types {;; Variables in the table.
                             :x real-type
                             :y real-type
                             :z real-type
                             :a integer-type
                             :b integer-type
                             :c integer-type
                             ;; Exposed latent variables.
                             :cluster-for-x integer-type
                             :cluster-for-y integer-type
                             :cluster-for-z integer-type
                             :cluster-for-a integer-type
                             :cluster-for-b integer-type
                             :cluster-for-c integer-type}
        output-addr-map (make-identity-output-addr-map outputs-addrs-types)
        inputs-addrs-types {}
        input-addr-map {}]
    (make-cgpm generate-crosscat-row
               outputs-addrs-types
               inputs-addrs-types
               output-addr-map
               input-addr-map)))



(def test-points [{:tx 3  :ty 4  :test-point "P 1"}
                  {:tx 8  :ty 10 :test-point "P 2"}
                  {:tx 14 :ty 7  :test-point "P 3"}
                  {:tx 15 :ty 8  :test-point "P 4"}
                  {:tx 16 :ty 9  :test-point "P 5"}
                  {:tx 9  :ty 16 :test-point "P 6"}])

(defn test-point-coordinates [name]
  (let [predicate (fn [point] (= (:test-point point) name))]
    (dissoc (first (filter predicate test-points)) :test-point)))

(def n 1000)

(deftest crosscatsimulate-simulate-joint
  (testing "(smoke) simulate n complete rows"
    (let [num-samples n
          samples (cgpm-simulate
                    crosscat-cgpm
                    [:x :y :z :a :b :c]
                    {}
                    {}
                    num-samples)]
      (save-json "out/json-results/simulations-x-y.json"
                 (scatter-plot-json ["x" "y"]
                                    samples
                                    test-points
                                    [0 18]
                                    "View 1: X, Y, A, B"))
      (save-json "out/json-results/simulations-z.json"
                 (hist-plot (column-subset samples [:z :c]) [:z :c]"Dim Z"))
      (save-json "out/json-results/simulations-a.json"
                 (bar-plot (column-subset samples [:a]) "Dim A" n))
      (save-json "out/json-results/simulations-b.json"
                 (bar-plot (column-subset samples [:b]) "Dim B" n))
      (save-json "out/json-results/simulations-c.json"
                 (bar-plot (column-subset samples [:c]) "Dim C" n))
      (is (= (count samples)
             n)))))


(def numper-simulations-for-test 100)
(def threshold 0.1)
(defn is-almost-equal [a b] (almost-equal a b relerr threshold))
(defn is-almost-equal-vectors [a b] (almost-equal-vectors a b relerr threshold))
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;; Testin P 2 ;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def p2 (test-point-coordinates "P 2"))
;; Testing invatri conditioning on the cluster ID = 2 which corresponds to the component
;; that of which p2 is a cluster center.
(deftest crosscat-simulate-simulate-mean-conditioned-on-cluster-p2
  (testing "Mean of simulations conditioned on cluster-ID = 2"
    (let [samples (cgpm-simulate
                    crosscat-cgpm
                    [:x :y]
                    {:cluster-for-x 2}
                    {}
                    numper-simulations-for-test
                    )
          x-samples (get-col :x  samples)
          y-samples (get-col :y  samples)]
      (is (and (is-almost-equal (average x-samples) (:tx p2))
               (is-almost-equal (average y-samples) (:ty p2)))))))

(deftest crosscat-simulate-simulate-mean-conditioned-on-cluster-p2
  (testing "Standard deviaton of simulations conditioned on cluster-ID = 2"
    (let [samples (cgpm-simulate
                    crosscat-cgpm
                    [:x :y]
                    {:cluster-for-x 2}
                    {}
                    numper-simulations-for-test
                    )
          x-samples (get-col :x  samples)
          y-samples (get-col :y  samples)
          factor 2]
      (is (and (within-factor (std x-samples) 0.5 factor)
               (within-factor (std y-samples) 1 factor))))))

(deftest crosscat-simulate-categoricals-conditioned-on-cluster-p2
  (testing "Categorical simulations conditioned on cluster-ID = 2"
    (let [samples (cgpm-simulate
                    crosscat-cgpm
                    [:a :b]
                    {:cluster-for-x 2}
                    {}
                    numper-simulations-for-test
                    )
          a-samples (column-subset samples [:a])
          b-samples (column-subset samples [:b])
          true-p-a [0 0 1 0 0 0]
          true-p-b [0.01 0.01 0.95 0.01 0.01 0.01]
          possible-values (range 6)
          a-p-fraction (probability-vector a-samples possible-values)
          b-p-fraction (probability-vector b-samples possible-values)]
      (is (and (is-almost-equal-vectors a-p-fraction true-p-a)
               (is-almost-equal-vectors b-p-fraction true-p-b))))))
