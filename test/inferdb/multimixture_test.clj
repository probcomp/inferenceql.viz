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


(def test-points [{:tx 10 :ty 5 :test-point "A"}])


(defn scatter-plot-json
  [columns values domain title]
    (cheshire/generate-string
     {:$schema "https://vega.github.io/schema/vega-lite/v3.json"
      :background "white"
      :data {:values (concat values test-points)}
      :title title
      :layer [{:width 700
               :height 700
               :mark {:type "point" :filled true}
               :encoding {
                  :x {:field (first columns)
                      :title (name (first columns))
                      :type "quantitative"
                      :scale {:domain domain}},
                  :y {:field (second columns)
                      :title (name (second columns))
                      :type "quantitative"
                      :scale {:domain domain}}
                  :color {:field "a"
                          :type "nominal"}
                  :shape {:field "b"
                          :type "nominal"}}}
              {:width 700
               :height 700
               :mark {:type "text" :dx 15}
               :encoding {
                  :text {:field "test-point"
                      :type "nominal"}
                  :x {:field "tx"
                      :type "quantitative"}
                  :y {:field "ty"
                      :type "quantitative"}}}
              {:width 700
               :height 700
               :mark {:type "square"
                      :filled false
                      :color "#030303"
                      :size 100}
               :encoding {
                  :x {:field "tx"
                      :type "quantitative"}
                  :y {:field "ty"
                      :type "quantitative"}}}]}))

(def num-rows-from-generator 1000)
(defn get-counts [item] {
                         "category" (first (vals (first item)))
                         ;; XXX: the 1000 below should be supplied as param.
                         "probability" (float (/ (second item) num-rows-from-generator))})

(defn bar-plot
  [samples title]
    (cheshire/generate-string
     {:$schema "https://vega.github.io/schema/vega-lite/v3.json"
      :background "white"
      :data {:values (map get-counts (frequencies samples))}
      :width 200
      :height 300
      :mark "bar"
      :title title
      :encoding {
         :y {
           :field "category"
           :type "ordinal"}
         :x {
           :field "probability"
           :type "quantitative"}}}))

(defn hist-plot
  [samples columns title]
    (let
      [xlabel (str (first columns) " (binned) ")]
      (cheshire/generate-string
       {:$schema "https://vega.github.io/schema/vega-lite/v3.json"
        :background "white"
        :data {:values  samples}
        :width 200
        :height 300
        :mark "bar"
        :title title
        :transform [
           {
             :bin {:binned true :step 1},
             :field (first columns)
             :as xlabel
           }
         ]
        :encoding {
           :x {:field xlabel
               :title (name (first columns))
               :bin {:binned true :step 1}
               :type "quantitative"}
           :x2 {:field (str xlabel "_end")}
           :y {:aggregate "count"
               :type "quantitative"
               }
           :color {:field (second columns)
                   :type "nominal"}}})))

(defn column-subset [data columns]
  (let [row-subset (fn [row] (select-keys row columns))]
    (map row-subset data)))

(deftest crosscatsimulate-simulate-joint
  (testing "(smoke) simulate n complete rows"
    (let [num-samples num-rows-from-generator
          samples (cgpm-simulate
                    crosscat-cgpm
                    [:x :y :z :a :b :c]
                    {}
                    {}
                    num-samples)]
      (save-json "out/json-results/simulations-x-y.json"
                 (scatter-plot-json ["x" "y"]
                                    samples
                                    [-2 19]
                                    "View 1: X, Y, A, B"))
      (save-json "out/json-results/simulations-z.json"
                 (hist-plot (column-subset samples [:z :c]) [:z :c]"Dim Z"))
      (save-json "out/json-results/simulations-a.json"
                 (bar-plot (column-subset samples [:a]) "Dim A"))
      (save-json "out/json-results/simulations-b.json"
                 (bar-plot (column-subset samples [:b]) "Dim B"))
      (save-json "out/json-results/simulations-c.json"
                 (bar-plot (column-subset samples [:c]) "Dim C"))
      (is (= (count samples)
             num-rows-from-generator)))))
