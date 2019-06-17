(ns inferdb.multimixture-test
  (:require [clojure.test :refer :all]
            [inferdb.cgpm.main :refer :all]
            [inferdb.utils :as utils]
            [inferdb.plotting.generate-vljson :refer :all]
            [inferdb.multimixture.dsl :refer :all]
            [metaprob.distributions :refer :all]))

;; XXX: why is this still here?
(defn make-identity-output-addr-map
  [output-addrs-types]
  (let [output-addrs (keys output-addrs-types)
        trace-addrs  (map name output-addrs)]
    (zipmap output-addrs trace-addrs)))

;; The following data generator has some interesting properties:
;; - clusters 0 and 1 in view 0 share the samme mu parameter.
;; - a is a determinstic indicator of the cluster.
;; - b is a noisy copy of a.
;; - in both views, clusters are equally weighted.
;; - in view 1, the third Gaussian components (cluster 0) "spans" the domain of
;; all the other components and share a center with cluster 1.
;;
;; I'd encourage everyone who works with the file to run the tests in this file
;; and then run make charts to see how the components relate.
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
       0.25 {"z" [15 1]
             "c" [[0 1 0 0]]}
       0.25 {"z" [30 1]
             "c" [[0 0 1 0]]}
       0.25 {"z" [15 8]
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

; XXX this is not all that elegant: for plotting, I need all the test points in
; that format.
(def test-points [{:tx 3  :ty 4  :test-point "P 1"}
                  {:tx 8  :ty 10 :test-point "P 2"}
                  {:tx 14 :ty 7  :test-point "P 3"}
                  {:tx 15 :ty 8  :test-point "P 4"}
                  {:tx 16 :ty 9  :test-point "P 5"}
                  {:tx 9  :ty 16 :test-point "P 6"}])

(defn test-point-coordinates [name]
  "A function to extract a relevant point from the array above."
  (dissoc (first (filter #(= (:test-point %) name) test-points))
          :test-point))

; How many points do we want to create for our plot?
(def n 1000)
(deftest crosscatsimulate-simulate-joint
  "This tests saves plots for all simulated data in out/json results/"
  ;; Charts can be generated with make charts.
  (testing "(smoke) simulate n complete rows and save them as vl-json"
    (let [num-samples n
          samples (cgpm-simulate
                    crosscat-cgpm
                    [:x :y :z :a :b :c]
                    {}
                    {}
                    num-samples)]
      (utils/save-json "simulations-x-y"
                 (scatter-plot-json ["x" "y"]
                                    samples
                                    test-points
                                    [0 18]
                                    "View 1: X, Y, A, B"))
      (utils/save-json "simulations-z"
                 (hist-plot
                   (utils/column-subset samples [:z :c])
                   [:z :c]
                   "Dim Z and C"))
      (utils/save-json "simulations-a"
                 (bar-plot (utils/column-subset samples [:a]) "Dim A" n))
      (utils/save-json "simulations-b"
                 (bar-plot (utils/column-subset samples [:b]) "Dim B" n))
      (utils/save-json "simulations-c"
                 (bar-plot (utils/column-subset samples [:c]) "Dim C" n))
      (is (= (count samples) n)))))

; Let's define a few helper constants and functions that we'll use below.
(def numper-simulations-for-test 100)
(def threshold 0.1)
(defn is-almost-equal? [a b] (utils/almost-equal? a b utils/relerr threshold))
(defn is-almost-equal-vectors? [a b] (utils/almost-equal-vectors? a b utils/relerr threshold))
(defn is-almost-equal-p? [a b] (utils/almost-equal? a b utils/relerr 0.01))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;; Testing P 1 ;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; TODO. This case is marginally more complicated because P 1 happens to be the
;; center of two clusters.

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;; Testing P 2 ;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def p2 (test-point-coordinates "P 2"))
;; Testing invariants conditioning on the cluster ID = 2 which corresponds to the component
;; that of which p2 is a cluster center.
(deftest crosscat-simulate-simulate-mean-conditioned-on-cluster-p2
  (testing "mean of simulations conditioned on cluster-ID = 2"
    (let [samples (cgpm-simulate
                    crosscat-cgpm
                    [:x :y]
                    {:cluster-for-x 2}
                    {}
                    numper-simulations-for-test)
          x-samples (utils/col :x  samples)
          y-samples (utils/col :y  samples)]
      (is (and (is-almost-equal? (utils/average x-samples) (:tx p2))
               (is-almost-equal? (utils/average y-samples) (:ty p2)))))))

(deftest crosscat-simulate-simulate-mean-conditioned-on-cluster-p2
  (testing "standard deviaton of simulations conditioned on cluster-ID = 2"
    (let [samples (cgpm-simulate
                    crosscat-cgpm
                    [:x :y]
                    {:cluster-for-x 2}
                    {}
                    numper-simulations-for-test)
          x-samples (utils/col :x  samples)
          y-samples (utils/col :y  samples)
          factor 2]
      (is (and (utils/within-factor? (utils/std x-samples) 0.5 factor)
               (utils/within-factor? (utils/std y-samples) 1 factor))))))

(deftest crosscat-simulate-categoricals-conditioned-on-cluster-p2
  (testing "categorical simulations conditioned on cluster-ID = 2"
    (let [samples (cgpm-simulate
                    crosscat-cgpm
                    [:a :b]
                    {:cluster-for-x 2}
                    {}
                    numper-simulations-for-test)
          a-samples (utils/column-subset samples [:a])
          b-samples (utils/column-subset samples [:b])
          true-p-a [0 0 1 0 0 0]
          true-p-b [0.01 0.01 0.95 0.01 0.01 0.01]
          possible-values (range 6)
          a-p-fraction (utils/probability-vector a-samples possible-values)
          b-p-fraction (utils/probability-vector b-samples possible-values)]
      (is (and (is-almost-equal-vectors? a-p-fraction true-p-a)
               (is-almost-equal-vectors? b-p-fraction true-p-b))))))

(deftest crosscat-logpdf-point-conditioned-on-cluster-p2
  (testing "categorical logPDF of P2 conditioned on cluster-ID = 2"
    (let [logpdf (cgpm-logpdf
                    crosscat-cgpm
                    {:x (:tx p2) :y (:ty p2)}
                    {:cluster-for-x 2}
                    {})
          analytical-logpdf (+
                             (score-gaussian (:tx p2) [8  0.5])
                             (score-gaussian (:ty p2) [10 1]))]
      (is (is-almost-equal-p?  logpdf analytical-logpdf)))))

;; TODO: Add a smoke test. Categories for :a are deteriministic. If we condition
;; on :a taking any different value than 2 this will crash.
(deftest crosscat-logpdf-categoricals-conditioned-on-cluster-p2
  (testing "logPDF of categoricals implying cluster ID 2 conditioned on cluster-ID = 2"
    ;; XXX, not sure how to deal wth line breaks for this....
    (let [logpdf (cgpm-logpdf
                    crosscat-cgpm
                    {:a 2  :b 2}
                    {:cluster-for-x 2}
                    {})
          analytical-logpdf (Math/log 0.95)]
      (is (is-almost-equal-p?  logpdf analytical-logpdf)))))

(deftest crosscat-simulate-cluster-id-conditoned-on-p2
  (testing "simulations of cluster-IDs conditioned on P2"
    (let [samples (cgpm-simulate
                    crosscat-cgpm
                    [:cluster-for-x, :cluster-for-y]
                    {:x (:tx p2) :y (:ty p2)}
                    {}
                    numper-simulations-for-test)
          id-samples-x (utils/column-subset samples [:cluster-for-x])
          id-samples-y (utils/column-subset samples [:cluster-for-y])
          cluster-p-fraction (utils/probability-vector id-samples-x (range 6))
          true-p-cluster [0 0 1 0 0 0]]
      (is (utils/equal-sample-values id-samples-x id-samples-y))
      (is (is-almost-equal-vectors? cluster-p-fraction true-p-cluster)))))

(deftest crosscat-logpdf-cluster-id-conditoned-on-p2
  (testing "logPDF of the correct cluster-IDs for P2 conditioned on P2"
    (let [logpdf (cgpm-logpdf
                    crosscat-cgpm
                    {:cluster-for-x 2}
                    {:x (:tx p2) :y (:ty p2)}
                    {})]
      (is (is-almost-equal-p?  logpdf 0)))))

(deftest crosscat-simulate-categoricals-conditioned-on-p2
  (testing "categorical simulations conditioned on P2"
    (let [samples (cgpm-simulate
                    crosscat-cgpm
                    [:a :b]
                    {:x (:tx p2) :y (:ty p2)}
                    {}
                    numper-simulations-for-test)
          a-samples (utils/column-subset samples [:a])
          b-samples (utils/column-subset samples [:b])
          true-p-a [0 0 1 0 0 0]
          true-p-b [0.01 0.01 0.95 0.01 0.01 0.01]
          possible-values (range 6)
          a-p-fraction (utils/probability-vector a-samples possible-values)
          b-p-fraction (utils/probability-vector b-samples possible-values)]
      (is (and (is-almost-equal-vectors? a-p-fraction true-p-a)
               (is-almost-equal-vectors? b-p-fraction true-p-b))))))

(deftest crosscat-logpdf-categoricals-conditioned-on-p2
  (testing "logPDF of categoricals conditioned on P2"
    (let [logpdf (cgpm-logpdf
                    crosscat-cgpm
                    {:a 2  :b 2}
                    {:x (:tx p2) :y (:ty p2)}
                    {})
          analytical-logpdf (Math/log 0.95)]
      (is (is-almost-equal-p?  logpdf analytical-logpdf)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;; Testing P 3 ;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
(def p3 (test-point-coordinates "P 3"))
;; Testing invariants conditioning on the cluster ID = 3 which corresponds to the component
;; that of which p3 is a cluster center.
(deftest crosscat-simulate-simulate-mean-conditioned-on-cluster-p3
  (testing "mean of simulations conditioned on cluster-ID = 3"
    (let [samples (cgpm-simulate
                    crosscat-cgpm
                    [:x :y]
                    {:cluster-for-x 3}
                    {}
                    numper-simulations-for-test)
          x-samples (utils/col :x  samples)
          y-samples (utils/col :y  samples)]
      (is (and (is-almost-equal? (utils/average x-samples) (:tx p3))
               (is-almost-equal? (utils/average y-samples) (:ty p3)))))))

(deftest crosscat-simulate-simulate-mean-conditioned-on-cluster-p3
  (testing "standard deviaton of simulations conditioned on cluster-ID = 3"
    (let [samples (cgpm-simulate
                    crosscat-cgpm
                    [:x :y]
                    {:cluster-for-x 3}
                    {}
                    numper-simulations-for-test)
          x-samples (utils/col :x  samples)
          y-samples (utils/col :y  samples)
          factor 2]
      (is (and (utils/within-factor? (utils/std x-samples) 0.5 factor)
               (utils/within-factor? (utils/std y-samples) 0.5 factor))))))

(deftest crosscat-simulate-categoricals-conditioned-on-cluster-p3
  (testing "Categorical simulations conditioned on cluster-ID = 3"
    (let [samples (cgpm-simulate
                    crosscat-cgpm
                    [:a :b]
                    {:cluster-for-x 3}
                    {}
                    numper-simulations-for-test)
          a-samples (utils/column-subset samples [:a])
          b-samples (utils/column-subset samples [:b])
          true-p-a [0 0 0 1 0 0]
          true-p-b [0.01 0.01 0.01 0.95 0.01 0.01]
          possible-values (range 6)
          a-p-fraction (utils/probability-vector a-samples possible-values)
          b-p-fraction (utils/probability-vector b-samples possible-values)]
      (is (and (is-almost-equal-vectors? a-p-fraction true-p-a)
               (is-almost-equal-vectors? b-p-fraction true-p-b))))))

(deftest crosscat-logpdf-point-conditioned-on-cluster-p3
  (testing "categorical logPDF of P3 conditioned on cluster-ID = 3"
    (let [logpdf (cgpm-logpdf
                    crosscat-cgpm
                    {:x (:tx p3) :y (:ty p3)}
                    {:cluster-for-x 3}
                    {})
          analytical-logpdf (+
                             (score-gaussian (:tx p3) [14 0.5])
                             (score-gaussian (:ty p3) [ 7 0.5]))]
      (is (is-almost-equal-p?  logpdf analytical-logpdf)))))

;; TODO: Same as above. Add a smoke test. Categories for :a are deteriministic.
;; If we condition on :a taking any different value than 3 this will crash.
(deftest crosscat-logpdf-categoricals-conditioned-on-cluster-p3
  (testing "logPDF of categoricals implying cluster ID 3 conditioned on cluster-ID = 3"
    ;; XXX, not sure how to deal wth line breaks for this....
    (let [logpdf (cgpm-logpdf
                    crosscat-cgpm
                    {:a 3  :b 3}
                    {:cluster-for-x 3}
                    {})
          analytical-logpdf (Math/log 0.95)]
      (is (is-almost-equal-p?  logpdf analytical-logpdf)))))

(deftest crosscat-simulate-cluster-id-conditoned-on-p3
  (testing "simulations of cluster-IDs conditioned on P3"
    (let [samples (cgpm-simulate
                    crosscat-cgpm
                    [:cluster-for-x, :cluster-for-y]
                    {:x (:tx p3) :y (:ty p3)}
                    {}
                    numper-simulations-for-test)
          id-samples-x (utils/column-subset samples [:cluster-for-x])
          id-samples-y (utils/column-subset samples [:cluster-for-y])
          cluster-p-fraction (utils/probability-vector id-samples-x (range 6))
          true-p-cluster [0 0 0 1 0 0]]
      (is (utils/equal-sample-values id-samples-x id-samples-y))
      (is (is-almost-equal-vectors? cluster-p-fraction true-p-cluster)))))

(deftest crosscat-logpdf-cluster-id-conditoned-on-p3
  (testing "logPDF of the correct cluster-IDs for P3 conditioned on P3"
    (let [logpdf (cgpm-logpdf
                    crosscat-cgpm
                    {:cluster-for-x 3}
                    {:x (:tx p3) :y (:ty p3)}
                    {})]
      (is (is-almost-equal-p?  logpdf 0)))))

(deftest crosscat-simulate-categoricals-conditioned-on-p3
  (testing "categorical simulations conditioned on P3"
    (let [samples (cgpm-simulate
                    crosscat-cgpm
                    [:a :b]
                    {:x (:tx p3) :y (:ty p3)}
                    {}
                    numper-simulations-for-test)
          a-samples (utils/column-subset samples [:a])
          b-samples (utils/column-subset samples [:b])
          true-p-a [0 0 0 1 0 0]
          true-p-b [0.01 0.01 0.01 0.95 0.01 0.01]
          possible-values (range 6)
          a-p-fraction (utils/probability-vector a-samples possible-values)
          b-p-fraction (utils/probability-vector b-samples possible-values)]
      (is (and (is-almost-equal-vectors? a-p-fraction true-p-a)
               (is-almost-equal-vectors? b-p-fraction true-p-b))))))

(deftest crosscat-logpdf-categoricals-conditioned-on-p3
  (testing "logPDF of categoricals conditioned on P3"
    (let [logpdf (cgpm-logpdf
                    crosscat-cgpm
                    {:a 3  :b 3}
                    {:x (:tx p3) :y (:ty p3)}
                    {})
          analytical-logpdf (Math/log 0.95)]
      (is (is-almost-equal-p?  logpdf analytical-logpdf)))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;; Testing P 4 ;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; TODO

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;; Testing P 5 ;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; TODO

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;; Testing P 6 ;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; TODO
