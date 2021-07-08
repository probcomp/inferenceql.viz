(ns inferenceql.viz.panels.sd2.start.views
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [inferenceql.viz.panels.sd2.start.db :refer [plot-data]]
            [inferenceql.viz.panels.viz.views :as viz]))

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
              :order {:condition {:param "click"
                                  :value 1}
                      :value 0}
              :opacity {:condition {:param "click",
                                    :value 1}
                        :value 0.1}}
   :layer [{:mark {:type "point" :tooltip {:content "data"}}
            :params [#_{:name "hover"
                        :select {:type "point",
                                 :fields ["gene"],
                                 :on "mouseover"}}
                     {:name "xypoint"
                      :select {:type "point",
                               :fields ["time", "expr-level"]
                               :on "mousedown"
                               :nearest true}}
                     {:name "click"
                      :select {:type "point",
                               :fields ["gene"],
                               :on "mousedown"
                               :nearest true}}]
            :encoding {:x {:field "time", :type "quantitative"
                           :axis nil}
                       :y {:field "expr-level", :type "quantitative"
                           :axis nil}
                       :opacity {:value 0}}}

           {:mark {:type "line"
                   :strokeWidth 2}
            :encoding {:x {:field "time", :type "quantitative"
                           :axis {:grid true
                                  :values (range 0 60 2)}}
                       :y {:field "expr-level", :type "quantitative"
                           :axis {:grid true}}
                       ;:values (range 0 1.70 0.4)}}
                       ;; TODO: make stroke solid on selection.
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
                     ;; TODO: make text bold on selection.
                     :encoding {:text {:field "gene" :type "nominal"}}}]}]})


(defn view []
  [viz/vega-lite (time-series plot-data) {} nil nil])
