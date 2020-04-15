(ns inferenceql.spreadsheets.views
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.panels.override.views :as modal]
            [inferenceql.spreadsheets.panels.control.views :as control]
            [inferenceql.spreadsheets.panels.viz.views :as viz]
            [inferenceql.spreadsheets.panels.table.views :as table]))

;;;; Views are expressed in Hiccup-like syntax. See the Reagent docs for more info.

(defn app
  []
  (let [vega-lite-spec @(rf/subscribe [:viz/vega-lite-spec])
        circle-spec @(rf/subscribe [:viz/circle-spec])
        generators      @(rf/subscribe [:viz/generators])]
    [:div
     [control/panel]
     [:div#viz-container
      (when circle-spec
        [viz/vega-lite circle-spec {:actions false :mode "vega"}])
      (when vega-lite-spec
        [viz/vega-lite vega-lite-spec {:actions false} generators])]]))
