(ns inferenceql.spreadsheets.views
  (:require [re-frame.core :as rf]
            [re-com.core :refer [v-box]]
            [inferenceql.spreadsheets.panels.control.views :as control]
            [inferenceql.spreadsheets.panels.viz.views :as viz]
            [inferenceql.spreadsheets.panels.table.views :as table]
            [inferenceql.spreadsheets.panels.modal.views :as modal]))

;;;; Views are expressed in Hiccup-like syntax. See the Reagent docs for more info.

(defn app
  []
  (let [real-hot-props @(rf/subscribe [:table/real-hot-props])
        vega-lite-spec @(rf/subscribe [:viz/vega-lite-spec])
        generators      @(rf/subscribe [:viz/generators])
        pts-store @(rf/subscribe [:viz/pts-store])
        virtual @(rf/subscribe [:table/virtual])
        highlight-class @(rf/subscribe [:table/highlight-class])
        modal-content @(rf/subscribe [:modal/content])]
    [v-box
     :children [[control/panel]
                [table/handsontable {:class [highlight-class (when virtual "virtual")]} real-hot-props]
                (when vega-lite-spec
                  [viz/vega-lite vega-lite-spec {:actions false} generators pts-store])
                [modal/modal modal-content]]]))
