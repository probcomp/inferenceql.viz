(ns inferenceql.spreadsheets.views
  (:require [re-frame.core :as rf]
            [reagent.core :as reagent]
            [inferenceql.spreadsheets.panels.override.views :as modal]
            [inferenceql.spreadsheets.panels.control.views :as control]
            [inferenceql.spreadsheets.panels.viz.views :as viz]
            [inferenceql.spreadsheets.panels.table.views :as table]))

;;;; Views are expressed in Hiccup-like syntax. See the Reagent docs for more info.

(defn app []
  (let [foo 3]
    (reagent/create-class
      {:display-name "app"

       :component-did-mount
       (fn [this]
         (comment
           (js/Split (clj->js ["#foo" "#bar"])
                     (clj->js {:sizes [50 50]
                               :direction 'vertical'
                               :gutterSize 20}))))

       :reagent-render
       (fn []
         (let [real-hot-props      @(rf/subscribe [:table/real-hot-props])
               vega-lite-spec @(rf/subscribe [:viz/vega-lite-spec])
               vega-lite-log-level @(rf/subscribe [:viz/vega-lite-log-level])
               generator      @(rf/subscribe [:viz/generator])
               highlight-class @(rf/subscribe [:table/highlight-class])]
           [:div
            [control/panel]
            [:div#page-container
             [:div#table-container {:class [highlight-class "split"]}
               [table/handsontable {} real-hot-props]]
             [:div#viz-container {:class "split"}
              (when vega-lite-spec
                [viz/vega-lite vega-lite-spec {:actions false :logLevel vega-lite-log-level} generator])]]
            [modal/modal]]))})))
