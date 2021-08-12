(ns inferenceql.viz.js.smart.views
  (:require [clojure.walk :refer [postwalk]]
            [cljs-bean.core :refer [->clj]]
            [re-com.core :refer [v-box h-box box gap]]
            [inferenceql.viz.js.components.plot.views :refer [vega-lite]]
            [inferenceql.viz.js.smart.vega :refer [generate-spec]]
            [inferenceql.viz.js.util :refer [clj-schema]]))

(defn anomaly-plot
  "Reagent component for displaying an anomoly plot"
  [data schema cur-col]
  (if (some? data)
    (let [;; Custom spec generating function for SMART app.
          spec (generate-spec schema data [[cur-col :p]])]
      [vega-lite spec {:actions false} nil nil])))

(defn generate-sim-spec
  [sims row row-anom]
  (let [cols (keys sims)
        row (select-keys row cols)
        actual (for [[k v] row]
                 {:timepoint k :value v :anomaly (get row-anom k)})
        anomalous-timepoints (keep (fn [[k v]] (when v k))
                                   row-anom)

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
                    :scale {:padding 0.01}
                    :axis {:tickColor {:condition {:test {:field "value" :oneOf anomalous-timepoints}
                                                   :value "red"}
                                       :value "black"}
                           :labelColor {:condition {:test {:field "value" :oneOf anomalous-timepoints}
                                                    :value "red"}
                                        :value "black"}}}
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
  [data anim-step]
  (if-not (some? data)
    [v-box
     :min-height "200px"
     :min-width "400px"
     :align :center
     :justify :center
     :children [[:span "Running simulations..."]]]
    [v-box
     :align :start
     :children [[:button.toolbar-button.pure-button
                 {:style {:margin-left "48px"}
                  :on-click (fn [e]
                              (anim-step)
                              (.blur (.-target e)))}
                 "Continue"]
                (when data
                  (let [{:keys [sims row row-anom]} data
                        spec (generate-sim-spec sims row row-anom)]
                    [gap :size "10px"]
                    [vega-lite spec {:actions false} nil nil]))]]))
