(ns inferenceql.viz.panels.sd2.start.views
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]))

#_(defn view [model columns-used constraints]
    [:div
     [v-box :children [[:h1 "model"]
                       [gap :size "5px"]
                       (for [[view-id view] (:views model)]
                         ^{:key view-id} [xcat-view view-id view columns-used constraints])
                       [gap :size "40px"]
                       [model-output]]]])
