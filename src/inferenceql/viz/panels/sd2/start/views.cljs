(ns inferenceql.viz.panels.sd2.start.views
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [inferenceql.viz.panels.sd2.start.db :refer [plot-data]]
            [inferenceql.viz.panels.viz.views :as viz]))

(.log js/console :aoeu plot-data)

(defn time-series [data]
  {:$schema "https://vega.github.io/schema/vega-lite/v5.json",
   :width 1000
   :config {:view {:stroke nil}}
   :height 1000
   :data {:values data}
   ;;:transform [{:calculate "datum.status == 'rec' ? 1 : 0" :as "zorder"}]
   :mark {:type "line" :tooltip {:content "data"}}
   :encoding {:color {:field "status", :type "nominal"
                      :scale {:domain ["rec", "not-rec"] :range ["#4e79a7" "#f28e2b"]}}}
   :resolve {:axis {:x "independent" :y "independent"}}
   :layer [{:mark "line"
            :encoding {:x {:field "time", :type "quantitative"
                           :axis {:grid true
                                  :values (range 0 50 8)}}
                       :y {:field "expr-level", :type "quantitative"
                           :axis {:grid true
                                  :values (range 0 1.70 0.4)}}
                       :strokeDash {:field "gene" :type "nominal" :legend nil}}}
           {:encoding {:x {:aggregate "max"
                           :field "time"
                           :type "quantitative"
                           :axis nil}
                       :y {:aggregate {:argmax "time"}
                           :field "expr-level"
                           :type "quantitative"
                           :axis nil}}
            :layer [{:mark {:type "circle"}
                     :encoding {:detail {:field "gene" :type "nominal"}}}
                    {:mark {:type "text" :align "left" :dx 4}
                     :encoding {:text {:field "gene" :type "nominal"}}}]}]})






(defn view []
  [viz/vega-lite (time-series plot-data) {} nil nil])
