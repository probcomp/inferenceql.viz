(ns inferenceql.spreadsheets.views
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.panels.override.views :as modal]
            [inferenceql.spreadsheets.panels.control.views :as control]
            [inferenceql.spreadsheets.panels.viz.views :as viz]
            [inferenceql.spreadsheets.panels.table.views :as table]))

;;;; Views are expressed in Hiccup-like syntax. See the Reagent docs for more info.

(defn app
  []
  (let [real-hot-props @(rf/subscribe [:table/real-hot-props])
        vega-lite-spec @(rf/subscribe [:viz/vega-lite-spec])
        generators      @(rf/subscribe [:viz/generators])
        virtual @(rf/subscribe [:table/virtual])
        highlight-class @(rf/subscribe [:table/highlight-class])
        show-table-controls @(rf/subscribe [:table/show-table-controls])]
    [:div
     [control/panel]
     [table/controls show-table-controls]
     [:div#table-container {:class [highlight-class (when virtual "virtual")]}
       [table/handsontable {} real-hot-props]]
     [:div#viz-container
      [:div.flex-box-space-filler-20]
      (when vega-lite-spec
        [viz/vega-lite vega-lite-spec {:actions false} generators])
      [:div.flex-box-space-filler-20]]
     [modal/modal]]))
