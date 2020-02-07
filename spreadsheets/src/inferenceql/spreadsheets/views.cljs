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
        virtual-hot-props @(rf/subscribe [:table/virtual-hot-props])
        real-table-in-viz @(rf/subscribe [:viz/real-table-in-viz])
        virtual-table-in-viz @(rf/subscribe [:viz/virtual-table-in-viz])
        vega-lite-spec @(rf/subscribe [:viz/vega-lite-spec])
        vega-lite-log-level @(rf/subscribe [:viz/vega-lite-log-level])
        generator      @(rf/subscribe [:viz/generator])]
    [:div
     [control/panel]
     [table/handsontable {} real-hot-props]
     [:div#viz-container
      (when vega-lite-spec
        [viz/vega-lite vega-lite-spec {:actions false :logLevel vega-lite-log-level} generator])]
     [modal/modal]]))
