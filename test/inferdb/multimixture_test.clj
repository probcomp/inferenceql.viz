(ns inferdb.multimixture-test
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [metaprob.distributions :refer :all]
            [inferdb.cgpm.main :refer :all]
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

(defn save-json [file-name file-str]
  (io/make-parents file-name)
  (spit file-name file-str))


(def test-points [{:tx 10 :ty 5 :test-point "P 1"}
                  {:tx 10 :ty 6 :test-point "P 2"}])



(defn column-subset [data columns]
  (let [row-subset (fn [row] (select-keys row columns))]
    (map row-subset data)))

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
                                    [-2 19]
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
(clojure.test/run-tests)
