(ns inferenceql.spreadsheets.views
  (:require [re-frame.core :as rf]
            [reagent-forms.core :as forms]
            [inferenceql.spreadsheets.events :as events]
            [inferenceql.spreadsheets.modal :as modal]
            [inferenceql.spreadsheets.panels.control.views :as control]
            [inferenceql.spreadsheets.panels.viz.views :as viz]
            [inferenceql.spreadsheets.panels.table.views :as table]))

(defn app
  []
  (let [real-hot-props      @(rf/subscribe [:real-hot-props])
        virtual-hot-props @(rf/subscribe [:virtual-hot-props])
        vega-lite-spec @(rf/subscribe [:vega-lite-spec])
        vega-lite-log-level @(rf/subscribe [:vega-lite-log-level])
        generator      @(rf/subscribe [:generator])]
    [:div
     [control/panel]
     [:div.table-title [:span "Real Data"]]
     [table/handsontable {:style {:overflow "hidden"}}  real-hot-props]
     [:div.table-title [:span "Virtual Data"]]
     [table/handsontable {:style {:overflow "hidden"} :class "virtual-hot"} virtual-hot-props]
     [:div#viz-container
      (when vega-lite-spec
        [viz/vega-lite vega-lite-spec {:actions false :logLevel vega-lite-log-level} generator])]
     [modal/modal]]))
