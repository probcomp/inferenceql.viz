(ns inferenceql.viz.views
  (:require [re-frame.core :as rf]
            [re-com.core :refer [v-box h-box box gap line]]
            [inferenceql.viz.panels.viz.views :as viz]
            [inferenceql.viz.panels.sd2.model.views :as sd2-model]
            [inferenceql.viz.panels.sd2.start.views :as sd2-start]
            [inferenceql.viz.panels.sd2.sim.views :as sd2-sim]))

;;;; Views are expressed in Hiccup-like syntax. See the Reagent docs for more info.

(defn app
  []
  (let [vega-lite-spec @(rf/subscribe [:viz/vega-lite-spec])
        models @(rf/subscribe [:store/models])
        columns-used @(rf/subscribe [:sim/columns-used])
        constraints @(rf/subscribe [:sim/constraints])
        target-gene @(rf/subscribe [:sim/target-gene])
        essential-genes @(rf/subscribe [:sim/essential-genes])
        all-essential-genes @(rf/subscribe [:sim/all-essential-genes])
        page @(rf/subscribe [:page])]
    (case page
      :start
      [sd2-start/view]

      :knockout-sim
      [h-box :children [[v-box
                         :size "6"
                         :style {:padding "20px"
                                 :background "#fafafa"}
                         :children [[:button.toolbar-button.pure-button
                                     {:on-click (fn [e]
                                                  (rf/dispatch [:set-page :start])
                                                  ;;(rf/dispatch [:sd2-start/set-gene-clicked (name target-gene)])
                                                  (.blur (.-target e)))
                                      :style {:align-self "start" :margin-left "0px"}}
                                     "back"]
                                    [sd2-sim/view target-gene essential-genes all-essential-genes]
                                    [gap :size "30px"]
                                    [sd2-model/view (:model models) columns-used constraints]]]
                        [line
                         :size "1px"
                         :color "whitesmoke"]
                        [box
                         :size "8"
                         :margin "40px 0px"
                         :child [viz/vega-lite vega-lite-spec {} nil nil :knockout-sim-page]]]])))

