(ns inferenceql.spreadsheets.views
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.panels.override.views :as modal]
            [inferenceql.spreadsheets.panels.control.views :as control]
            [inferenceql.spreadsheets.panels.viz.views :as viz]
            [inferenceql.spreadsheets.panels.table.views :as table]))

;;;; Views are expressed in Hiccup-like syntax. See the Reagent docs for more info.

(defn app
  []
  (let [real-hot-props      @(rf/subscribe [:table/real-hot-props])
        vega-lite-spec-blue @(rf/subscribe [:viz/vega-lite-spec-blue])
        vega-lite-spec-green @(rf/subscribe [:viz/vega-lite-spec-green])
        vega-lite-spec-red @(rf/subscribe [:viz/vega-lite-spec-red])
        generator-blue      @(rf/subscribe [:viz/generator-blue])
        generator-green      @(rf/subscribe [:viz/generator-green])
        generator-red      @(rf/subscribe [:viz/generator-red])
        vega-mode-blue      @(rf/subscribe [:viz/vega-mode-blue])
        vega-mode-green      @(rf/subscribe [:viz/vega-mode-green])
        vega-mode-red      @(rf/subscribe [:viz/vega-mode-red])
        highlight-class @(rf/subscribe [:table/highlight-class])]
    [:div
     [control/panel]
     [:div {:class highlight-class}
       [table/handsontable {} real-hot-props]]
     [:div#viz-container
      [:div.flex-box-space-filler]
      (when vega-lite-spec-blue
        [viz/vega-lite vega-lite-spec-blue {:actions false :mode vega-mode-blue} generator-blue])
      (when vega-lite-spec-green
        [viz/vega-lite vega-lite-spec-green {:actions false :mode vega-mode-green} generator-green])
      (when vega-lite-spec-red
        [viz/vega-lite vega-lite-spec-red {:actions false :mode vega-mode-red} generator-red])
      [:div.flex-box-space-filler]]
     [modal/modal]]))
