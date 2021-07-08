(ns inferenceql.viz.panels.sd2.start.views
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [inferenceql.viz.panels.sd2.start.db :refer [plot-data]]
            [inferenceql.viz.panels.viz.views :as viz]))

;;(.log js/console :aoeu plot-data)

(defn time-series [data]
  {:$schema "https://vega.github.io/schema/vega-lite/v5.json",
   :config {:view {:stroke nil}}
   :data {:values data}
   ;;:transform [{:calculate "datum.status == 'rec' ? 1 : 0" :as "zorder"}]
   :resolve {:axis {:x "independent" :y "independent"}}
   :width 1000
   :height 1000
   :encoding {:color {:field "status", :type "nominal"
                      :scale {:domain ["rec", "not-rec"] :range ["#4e79a7" "#f28e2b"]}}
              :order {:condition {:param "hover"
                                  :value 1}
                      :value 0}
              :opacity {:condition {:param "hover",
                                    :value 1}
                        :value 0.1}}

   :layer [{:mark {:type "line" #_:tooltip #_{:content "data"}
                   :strokeWidth 2}
            :params [{:name "hover"
                      :select {:type "point",
                               :fields ["gene"],
                               :on "mouseover"}}]
            :encoding {:x {:field "time", :type "quantitative"
                           :axis {:grid true
                                  :values (range 0 60 2)}}
                       :y {:field "expr-level", :type "quantitative"
                           :axis {:grid true}}
                                  ;:values (range 0 1.70 0.4)}}
                       :strokeDash {:field "gene" :type "nominal" :legend nil}
                       :label {:field "gene" :type "nominal"}}}

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


(defn time-series-2 [data]
  {:$schema "https://vega.github.io/schema/vega-lite/v5.json",
   :width 1000
   :config {:view {:stroke nil}}
   :height 1000
   :data {:values data}
   :encoding {:color {:field "status", :type "nominal"
                      :scale {:domain ["rec", "not-rec"] :range ["#4e79a7" "#f28e2b"]}}}
   :layer [{:mark {:type "line" :clip true :tooltip {:content "data"}}
            :encoding {:x {:field "time", :type "quantitative"}
                           ;;:scale {:domain [40, 59]}}
                       :y {:field "expr-level", :type "quantitative"}
                           ;:scale {:domain [0.9, 1.4]}}
                       :strokeDash {:field "gene" :type "nominal" :legend nil}}}]})

(defn view []
  [viz/vega-lite (time-series plot-data) {} nil nil])
