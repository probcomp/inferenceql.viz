(ns inferenceql.viz.js.components.smart.views
  (:require [clojure.walk :refer [postwalk]]
            [cljs-bean.core :refer [->clj]]
            [re-com.core :refer [v-box h-box box gap]]
            [inferenceql.viz.js.components.plot.views :refer [vega-lite]]
            [inferenceql.viz.js.components.smart.vega :refer [generate-spec]]
            [inferenceql.viz.js.util :refer [clj-schema]]
            [inferenceql.inference.utils :as utils]
            [kixi.stats.core :refer [standard-deviation-p
                                     standard-deviation]]))

(defn anomaly-plot
  "Reagent component for displaying an anomoly plot"
  [data schema cur-col]
  (if (some? data)
    (let [;; Custom spec generating function for SMART app.
          spec (generate-spec schema data [[cur-col :p]])]
      [vega-lite spec {:actions false} nil nil])))

(defn mean [coll]
  (let [sum (apply + coll)
        count (count coll)]
    (if (pos? count)
      (/ sum count)
      0)))

(defn median [coll]
  (let [sorted (sort coll)
        cnt (count sorted)
        halfway (quot cnt 2)]
    (if (odd? cnt)
      (nth sorted halfway) ; (1)
      (let [bottom (dec halfway)
            bottom-val (nth sorted bottom)
            top-val (nth sorted halfway)]
        (mean [bottom-val top-val]))))) ; (2)

(defn generate-sim-spec
  [sims row row-anom]
  (let [cols (keys sims)
        row (select-keys row cols)
        actual (for [[k v] row]
                 (when v
                   {:timepoint k :value v :anomaly (get row-anom k)}))
        actual (remove nil? actual)
        anomalous-timepoints (keep (fn [[k v]] (when v k))
                                   row-anom)

        calc-error (fn [[time a]] (let [stddev (utils/std a)
                                        m (median a)]
                                    {:ci1 (+ m stddev)
                                     :ci0 (- m stddev)
                                     :center m
                                     :timepoint time}))
        simulations-error (map calc-error sims)]
    {:$schema "https://vega.github.io/schema/vega-lite/v5.json",
     :datasets {:simulations-error simulations-error
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
                                        :value "black"}
                           :labelLimit 500}}
                :y {:axis {:grid false
                           :title "value"}
                    :scale {:zero false}}}
     :layer [;; Layers for simulated data.
             {:data {:name "simulations-error"}
              :mark {:type "errorband",
                     :opacity 0.8
                     :borders true
                     :color "#FFE8C7"}
              :encoding {:y {:field "ci1"
                             :type "quantitative"}
                         :y2 {:field "ci0"
                              :type "quantitative"}}}
             {:data {:name "simulations-error"}
              :mark {:type "line"
                     :color "#FF8D00"}
              :encoding {:y {:field "center" :type "quantitative"}}}
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
  [anomalous data]
  (when anomalous
    (if-not (some? data)
      [v-box
       :min-height "200px"
       :min-width "400px"
       :align :center
       :justify :center
       :children [[:span "Running simulations..."]]]
      [v-box
       :align :start
       :children [(when data
                    (let [{:keys [sims row row-anom]} data
                          spec (generate-sim-spec sims row row-anom)]
                      [gap :size "10px"]
                      [vega-lite spec {:actions false} nil nil]))]])))
