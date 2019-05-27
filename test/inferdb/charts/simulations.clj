(ns inferdb.charts.simulations
  (:require [clojure.walk :as walk]
            [cheshire.core :as cheshire]
            [inferdb.cgpm.main :as cgpm]
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
            [inferdb.multimixture.dsl :refer :all]
            [inferdb.cgpm.main :refer :all]))

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
                    "a" [[0.5 0.5 0 0 0 0 0 0 0 0]]
                    "b" [[0.5 0.5 0 0 0 0 0 0 0 0]]}
       0.166666666 {"x" [3 0.1]
                    "y" [4 1]
                    "a" [[0.5 0.5 0 0 0 0 0 0 0 0]]
                    "b" [[0.5 0.5 0 0 0 0 0 0 0 0]]}
       0.166666667 {"x" [8 0.5]
                    "y" [10 1]
                    "a" [[0.5 0.5 0 0 0 0 0 0 0 0]]
                    "b" [[0.5 0.5 0 0 0 0 0 0 0 0]]}
       0.166666666 {"x" [14 0.5]
                    "y" [7 0.5]
                    "a" [[0 0 0.5 0.5 0 0 0 0 0 0]]
                    "b" [[0 0 0.5 0.5 0 0 0 0 0 0]]}
       0.166666666 {"x" [16 0.5]
                    "y" [9 0.5]
                    "a" [[0 0 0 0 0.5 0.5 0 0 0 0]]
                    "b" [[0 0 0 0 0.5 0.5 0 0 0 0]]}
       0.166666666 {"x" [9  2.5]
                    "y" [16 0.1]
                    "a" [[0 0 0 0 0 0 0 0 0.5 0.5]]
                    "b" [[0 0 0 0 0 0 0 0 0.5 0.5]]}))
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
           [:x :y]
           {}
           {}
           num-samples)]
      (is (= (count samples)
             10)))))



(defn scatter-plot
  [columns  values domain]
    (println
     (cheshire/generate-string
      {:$schema "https://vega.github.io/schema/vega-lite/v3.json"
       :background "white"
       :data {:values values}
       :width 1000
       :height 1000
       :mark "circle"
       :encoding {
          :x {
            :field (first columns)
            :type "quantitative"
            :scale {:domain domain}},
          :y {
            :field (second columns)
            :type "quantitative"
            :scale {:domain domain}}}})))

(defn -main
  []
  (let [columns ["x" "y"]
        values (->> (cgpm/cgpm-simulate crosscat-cgpm
                                                 [(keyword (first columns))
                                                  (keyword (second columns))]
                                                 {}
                                                 {}
                                                 10000))]

        (scatter-plot columns values [-2 19])))
