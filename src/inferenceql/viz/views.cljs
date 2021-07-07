(ns inferenceql.viz.views
  (:require [re-frame.core :as rf]
            [re-com.core :refer [v-box h-box box gap line]]
            [inferenceql.viz.panels.control.views :as control]
            [inferenceql.viz.panels.viz.views :as viz]
            [inferenceql.viz.panels.table.views :as table]
            [inferenceql.viz.panels.modal.views :as modal]
            [inferenceql.viz.panels.sd2.model.views :as sd2-model]
            [inferenceql.viz.panels.sim.views :as sim]))

;;;; Views are expressed in Hiccup-like syntax. See the Reagent docs for more info.

(defn app
  []
  (let [real-hot-props @(rf/subscribe [:table/real-hot-props])
        generators      @(rf/subscribe [:viz/generators])
        pts-store @(rf/subscribe [:viz/pts-store])
        virtual @(rf/subscribe [:query/virtual])
        highlight-class @(rf/subscribe [:table/highlight-class])
        modal-content @(rf/subscribe [:modal/content])
        show-table-controls @(rf/subscribe [:table/show-table-controls])
        vega-lite-spec @(rf/subscribe [:viz/vega-lite-spec])
        models @(rf/subscribe [:store/models])
        columns-used @(rf/subscribe [:sim/columns-used])
        constraints @(rf/subscribe [:sim/constraints])
        target-gene @(rf/subscribe [:sim/target-gene])
        essential-genes @(rf/subscribe [:sim/essential-genes])
        all-essential-genes @(rf/subscribe [:sim/all-essential-genes])]
    [h-box :children [[v-box
                       :size "6"
                       :style {:padding "20px"
                               :background "#fafafa"}
                       :children [[sim/view target-gene essential-genes all-essential-genes]
                                  [gap :size "30px"]
                                  [sd2-model/view (:model models) columns-used constraints]]]
                      [line
                       :size "1px"
                       :color "whitesmoke"]
                      [box
                       :size "8"
                       :margin "40px 0px"
                       :child [viz/vega-lite vega-lite-spec {} nil nil]]]]))

