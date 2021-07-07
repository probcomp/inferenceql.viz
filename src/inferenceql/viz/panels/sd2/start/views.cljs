(ns inferenceql.viz.panels.sd2.start.views
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [inferenceql.viz.panels.sd2.start.db :refer [plot-data]]
            [inferenceql.viz.panels.viz.views :as viz]))

(defn time-series [data]
  {:$schema "https://vega.github.io/schema/vega-lite/v5.json",
   :width 1000
   ;;:config {:view {:stroke nil}}
   :height 1000
   :data {:values data}
   :transform [{:calculate "datum.status == 'rec' ? 0 : 1" :as "z-order"}]
   :mark {:type "line" :tooltip {:content "data"}}
   :encoding {:color {:field "status", :type "nominal"}}
   :layer [{:mark "line"
            :encoding {:x {:field "time", :type "temporal"}
                       :y {:field "expr-level", :type "quantitative"}
                       :order {:field "z-order" :type "quantitative"}
                       :strokeDash {:field "gene" :type "nominal" :legend nil}}}
           {:encoding {:x {:aggregate "max"
                           :field "time"
                           :type "temporal"
                           :axis {:title "time"}}
                       :y {:aggregate {:argmax "time"}
                           :field "expr-level"
                           :type "quantitative"
                           :axis {:title "expr-level"}}}
            :layer [{:mark {:type "circle"}
                     :encoding {:detail {:field "gene" :type "nominal"}}}
                    {:mark {:type "text" :align "left" :dx 4}
                     :encoding {:text {:field "gene" :type "nominal"}}}]}]})






(defn view []
  [viz/vega-lite (time-series plot-data) {} nil nil])
