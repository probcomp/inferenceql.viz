(ns inferenceql.viz.views
  (:require [re-com.core :refer [v-box h-box gap box]]
            [re-frame.core :as rf]
            [inferenceql.viz.config :refer [config transitions mutual-info]]
            [inferenceql.viz.panels.control.views :as control]
            [inferenceql.viz.panels.jsmodel.views :refer [js-model]]
            [inferenceql.viz.panels.table.views :refer [data-table]]
            [inferenceql.viz.panels.viz.views :refer [mi-plot select-vs-simulate-plot]]))

(defn app
  []
  (let [iteration @(rf/subscribe [:control/iteration])
        plot-type @(rf/subscribe [:control/plot-type])
        cluster-selected @(rf/subscribe [:control/cluster-selected])]
    [v-box
     :children [[control/panel]
                [v-box
                 :margin "20px"
                 :children [[data-table iteration cluster-selected]
                            [gap :size "30px"]
                            [h-box
                             :children [[js-model iteration cluster-selected]
                                        [gap :size "20px"]
                                        (case plot-type
                                          :mutual-information
                                          [v-box
                                           ;; Create a mi-plot for mi-info from each CrossCat sample.
                                           :children (for [mi mutual-info]
                                                        [mi-plot mi iteration])]

                                          :select-vs-simulate
                                          [select-vs-simulate-plot
                                           cluster-selected iteration])]]]]]]))
