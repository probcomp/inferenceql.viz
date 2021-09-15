(ns inferenceql.viz.panels.learning.views
  (:require [re-frame.core :as rf]
            [re-com.core :refer [v-box h-box box slider label gap selection-list]]
            [inferenceql.viz.config :refer [config transitions]]))

(defn panel
  [column-list]
  (let [iteration @(rf/subscribe [:learning/iteration])
        col-selection @(rf/subscribe [:learning/col-selection])]
    [v-box
     :children [[h-box
                 :children [[label :label "Iteration:"]
                            [gap :size "10px"]
                            [box
                             :style {:padding-top "3px"}
                             :child [slider
                                     :min 0
                                     :max (dec (count transitions))
                                     :model iteration
                                     :on-change #(rf/dispatch [:learning/set-iteration %])]]
                            [gap :size "10px"]
                            [label :label iteration]]]
                [gap :size "10px"]
                [h-box
                 :children [[label :label "Columns:"]
                            [gap :size "10px"]
                            [box
                             :style {:padding-top "3px"}
                             :child [selection-list
                                     :choices (vec (for [c column-list]
                                                     {:id c :label (name c)}))
                                     :required? true
                                     :model col-selection
                                     :on-change #(rf/dispatch [:learning/select-cols %])]]]]]]))


