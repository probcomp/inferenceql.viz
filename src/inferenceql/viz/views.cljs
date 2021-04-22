(ns inferenceql.viz.views
  (:require [re-frame.core :as rf]
            [re-com.core :refer [v-box h-box box gap line]]
            [inferenceql.viz.panels.control.views :as control]
            [inferenceql.viz.panels.viz.views :as viz]
            [inferenceql.viz.panels.table.views :as table]
            [inferenceql.viz.panels.modal.views :as modal]
            [inferenceql.viz.panels.sd2.views :as sd2]
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
        constraints @(rf/subscribe [:sim/constraints])]
    [h-box :children [[v-box
                       :size "1"
                       :style {:margin "20px"}
                       :children [[sim/view]
                                  [gap :size "30px"]
                                  [sd2/view (:model models) constraints]]]
                      [line
                       :size "1px"
                       :color "whitesmoke"]
                      [box
                       :size "2"
                       :child [viz/vega-lite vega-lite-spec {} nil nil]]]]))

