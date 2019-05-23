(ns inferdb.multimixture-test
  (:refer-clojure :exclude [map apply replicate])
  (:require [clojure.pprint :refer [pprint]]
            [clojure.repl :refer [doc source]]
            [clojure.test :refer :all]
            [clojure.string :refer [index-of]]
            [metaprob.generative-functions :refer :all]
            [metaprob.code-handlers :refer :all]
            [metaprob.expander :refer :all]
            [metaprob.trace :refer :all]
            [metaprob.autotrace :refer :all]
            [metaprob.prelude :refer :all]
            [metaprob.inference :refer :all]
            [metaprob.distributions :refer :all]
            [inferdb.cgpm.main :refer :all]
            [inferdb.charts.select-simulate :refer :all]
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
       0.2 {"x" [0 1]
            "y" [0 1]
            "a" [[0.5 0.5 0 0 0 0 0 0 0 0]]
            "b" [[0.5 0.5 0 0 0 0 0 0 0 0]]}
       0.2 {"x" [0 1]
            "y" [0 1]
            "a" [[0.5 0.5 0 0 0 0 0 0 0 0]]
            "b" [[0.5 0.5 0 0 0 0 0 0 0 0]]}
       0.2 {"x" [0 1]
            "y" [0 1]
            "a" [[0.5 0.5 0 0 0 0 0 0 0 0]]
            "b" [[0.5 0.5 0 0 0 0 0 0 0 0]]}
       0.2 {"x" [0 1]
            "y" [0 1]
            "a" [[0.5 0.5 0 0 0 0 0 0 0 0]]
            "b" [[0.5 0.5 0 0 0 0 0 0 0 0]]}
       0.2 {"x" [0 1]
            "y" [0 1]
            "a" [[0.5 0.5 0 0 0 0 0 0 0 0]]
            "b" [[0.5 0.5 0 0 0 0 0 0 0 0]]}))
    (view
      {"z" gaussian
       "c" categorical}
      (clusters
       0.25 {"z" [0 1]
             "c" [[1 0 0 0 0]]}
       0.25 {"z" [0 1]
             "c" [[1 0 0 0 0]]}
       0.25 {"z" [0 1]
             "c" [[1 0 0 0 0]]}
       0.25 {"z" [0 1]
             "c" [[1 0 0 0 0]]}))))

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

(deftest crosscat-conditional-simulate-smoke
  (testing "(smoke) simulate one col conditioned on another"
    (let [num-samples 10
          samples
          (cgpm-simulate
           crosscat-cgpm
           [:x]
           {:z 1}
           {}
           num-samples)]
      (is (= (count samples)
             10)))))

(clojure.test/run-tests)
