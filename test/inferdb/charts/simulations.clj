(ns inferdb.charts.simulations
  (:require [clojure.walk :as walk]
            [cheshire.core :as cheshire]
            [inferdb.cgpm.main :as cgpm]
            [clojure.repl :refer [doc source]]
            [clojure.test :refer :all]
            [clojure.string :refer [index-of]]
            [inferdb.multimixture-test :refer :all]))

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
