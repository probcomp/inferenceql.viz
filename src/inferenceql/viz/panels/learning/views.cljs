(ns inferenceql.viz.panels.learning.views
  (:require [re-frame.core :as rf]
            [re-com.core :refer [h-box box slider label gap]]
            [inferenceql.viz.config :refer [config]]))

(def models (:transitions config))

(defn panel
  []
  (let [iteration @(rf/subscribe [:learning/iteration])]
    [h-box
     :children [[label :label "Iteration:"]
                [gap :size "10px"]
                [box
                 :style {:padding-top "3px"}
                 :child [slider
                         :min 0
                         :max (dec (count models))
                         :model iteration
                         :on-change #(rf/dispatch [:learning/set-iteration %])]]
                [gap :size "10px"]
                [label :label iteration]]]))

