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
  [sims row row-anom]
  (let [cols (keys sims)
        row (select-keys row cols)
        actual (for [[k v] row]
                 {:timepoint k :value v :anomaly (get row-anom k)})

        tuples (fn [[k vs]]
                 (for [v vs]
                   {:timepoint k :value v}))
        simulations (mapcat tuples sims)]
    {:$schema "https://vega.github.io/schema/vega-lite/v5.json",
     :datasets {:simulations simulations
                :actual actual}
     :height 200
     :width 400
     :encoding {:x {:field "timepoint", :type "ordinal"
                    :scale {:padding 0.01}}
                :y {:axis {:grid false}}}
     :layer [;; Layers for simulated data.
             {:data {:name "simulations"}
              :mark {:type "errorband",
                     :extent "stdev"
                     :opacity 0.8
                     :borders true
                     :color "#FFE8C7"}
              :encoding {:y {:field "value",
                             :type "quantitative",}}}
             {:data {:name "simulations"}
              :mark {:type "line"
                     :color "#FF8D00"}
              :encoding {:y {:aggregate "mean", :field "value"}}}
             ;; Layers for actual data.
             {:data {:name "actual"}
              :mark {:type "line"
                     :color "black"
                     :strokeDash [4, 3]}
              :encoding {:y {:field "value" :type "quantitative"}}}
             {:data {:name "actual"}
              :mark {:type "circle"
                     :size 100
                     :opacity 1}
              :encoding {:color {:condition {:test "datum.anomaly == true"
                                             :value "red"}
                                 :value "black"}
                         :y {:field "value" :type "quantitative"}}}]}))



(defn sim-plot
  [data]
  (when (some? data)
    (let [{:keys [sims row row-anom]} data
          spec (generate-sim-spec sims row row-anom)]
      [:div
       #_[:div (str "sims: " sims)]
       #_[:div (str "row: " row)]
       [vega-lite spec {:actions true} nil nil]])))
