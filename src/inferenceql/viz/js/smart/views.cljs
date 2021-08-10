(ns inferenceql.viz.js.smart.views
  (:require [clojure.walk :refer [postwalk]]
            [cljs-bean.core :refer [->clj]]
            [inferenceql.viz.js.components.plot.views :refer [vega-lite]]
            [inferenceql.viz.js.smart.vega :refer [generate-spec]]
            [inferenceql.viz.js.util :refer [clj-schema]]))

(defn anomaly-plot
  "Reagent component for displaying an anomoly plot"
  [data schema selections]
  (if (some? data)
    (let [selections (postwalk #(if (string? %) (keyword  %) %)
                               selections)
          data (->clj data)
          ;; Custom spec generating function for SMART app.
          spec (generate-spec schema data selections)]
      [vega-lite spec {:actions false} nil nil])))

(defn generate-sim-spec
  [sims row]
  (let [tuples (fn [[k vs]]
                 (for [v vs]
                   {:timepoint k :value v}))
        sims (mapcat tuples sims)]
    {:$schema "https://vega.github.io/schema/vega-lite/v5.json",
     :datasets {:simulations sims
                :actual row}
     :height 400
     :width 400
     :layer [{:data {:name "simulations"}
              :mark {:type "errorband",
                     :extent "stdev"
                     :opacity 1.0
                     :color "#FFF4E4"}
              :encoding {:y {:field "value",
                             :type "quantitative",}
                         :x {:field "timepoint", :type "ordinal"}}}
             {:data {:name "simulations"}
              :mark {:type "line"
                     :color "#FF8D00"}
              :encoding {:y {:aggregate "mean", :field "value"}
                         :x {:field "timepoint", :type "ordinal"}}}]}))

(defn sim-plot
  [data]
  (when (some? data)
    (let [{:keys [sims row]} data
          spec (generate-sim-spec sims row)]
      [:div
       #_[:div (str "sims: " sims)]
       #_[:div (str "row: " row)]
       [vega-lite spec {:actions false} nil nil]])))
