(ns inferenceql.viz.views
  (:require [re-frame.core :as rf]
            [re-com.core :refer [v-box]]
            [inferenceql.viz.panels.control.views :as control]
            [inferenceql.viz.panels.viz.views :as viz]
            [inferenceql.viz.panels.table.views :as table]
            [inferenceql.viz.panels.modal.views :as modal]))

;;;; Views are expressed in Hiccup-like syntax. See the Reagent docs for more info.

(defn app
  []
  (let [real-hot-props @(rf/subscribe [:table/real-hot-props])
        vega-lite-spec @(rf/subscribe [:viz/vega-lite-spec])
        generators      @(rf/subscribe [:viz/generators])
        pts-store @(rf/subscribe [:viz/pts-store])
        virtual @(rf/subscribe [:query/virtual])
        highlight-class @(rf/subscribe [:table/highlight-class])
        modal-content @(rf/subscribe [:modal/content])
        show-table-controls @(rf/subscribe [:table/show-table-controls])]
    [v-box
     :children [[control/panel]
                [table/controls show-table-controls]
                [table/handsontable-base :re-frame
                 {:class [highlight-class (when virtual "virtual")]}
                 real-hot-props]
                [viz/vega-lite vega-lite-spec {:actions false} generators pts-store]
                [modal/modal modal-content]]]))
