(ns inferenceql.viz.views
  (:require [re-frame.core :as rf]
            [re-com.core :refer [v-box]]
            [inferenceql.viz.panels.control.views :as control]
            [inferenceql.viz.panels.viz.views :as viz]
            [inferenceql.viz.panels.table.views :as table]
            [inferenceql.viz.panels.modal.views :as modal]
            [inferenceql.viz.config :refer [config]]
            [inferenceql.viz.panels.learning.views :as learning]))

;;;; Views are expressed in Hiccup-like syntax. See the Reagent docs for more info.

(defn app
  []
  (let [iteration @(rf/subscribe [:learning/iteration])
        cgpm-models (:transitions config)
        cgpm-model (nth cgpm-models iteration)]
    (.log js/console :model cgpm-model)
    [v-box
     :children [[learning/panel]
                #_[viz/vega-lite vega-lite-spec {:actions false} generators pts-store]]]))
