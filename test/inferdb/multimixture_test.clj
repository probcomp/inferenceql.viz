(ns inferdb.multimixture-test
  (:refer-clojure :exclude [map apply replicate])
  (:require [clojure.pprint :refer [pprint]]
            [clojure.repl :refer [doc source]]
            [clojure.test :refer :all]
            [clojure.string :refer [index-of]]
            [cheshire.core :as cheshire]
            [clojure.java.io :as io]
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

(defn save-json [file-name file-str]
  (make-parents file-name)
  (spit file-name file-str))

(defn scatter-plot-json
  [columns  values domain]
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
           :scale {:domain domain}}}}))

(defn get-counts [item] {
                         "category" (first (vals (first item)))
                         ;; XXX: the 1000 below should be supplied as param.
                         "probability" (float (/ (second item) 1000))})
(defn bar-plot
  [samples]
    (cheshire/generate-string
     {:$schema "https://vega.github.io/schema/vega-lite/v3.json"
      :background "white"
      :data {:values (map get-counts (frequencies samples))}
      :width 1000
      :height 1000
      :mark "bar"
      :encoding {
         :y {
           :field "category"
           :type "ordinal"}
         :x {
           :field "probability"
           :type "quantitative"}}}))


;;(map get-counts (frequencies (cgpm-simulate crosscat-cgpm [:a] {} {} 100)))


(deftest crosscatsimulate-categorical-smoke
  (testing "(smoke) simulate a single categorical column"
    (let [num-samples 1000
          samples
          (cgpm-simulate
           crosscat-cgpm
           [:a]
           {}
           {}
           num-samples)]
      (save-json "out/json-results/simulations-a.json"
                 (bar-plot samples))
      (is (= (count samples)
             1000)))))




(deftest crosscat-simulate-numerical-columns-smoke
  (testing "(smoke) simulate two numerical columns"
    (let [num-samples 1000
          samples
          (cgpm-simulate
           crosscat-cgpm
           [:x :y]
           {}
           {}
           num-samples)]
      (save-json "out/json-results/simulations-x-y.json"
                 (scatter-plot-json ["x" "y"] samples [-2 19]))
      (is (= (count samples)
             1000)))))

(clojure.test/run-tests)
