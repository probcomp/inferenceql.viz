(ns inferenceql.viz.panels.learning.views
  (:require [re-frame.core :as rf]
            [re-com.core :refer [v-box h-box box slider label gap selection-list radio-button]]
            [inferenceql.viz.config :refer [config transitions]]))

(defn panel
  [column-list]
  (let [iteration @(rf/subscribe [:learning/iteration])
        col-selection @(rf/subscribe [:learning/col-selection])
        plot-type @(rf/subscribe [:learning/plot-type])
        marginal-types @(rf/subscribe [:learning/marginal-types])]
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
                 :children [[label :label "Plot type:"]
                            [gap :size "10px"]
                            [v-box
                             :children
                             (doall (for [p ["mutual-information" "select-vs-simulate"]]
                                      ^{:key p}
                                      [radio-button
                                       :label p
                                       :value (keyword p)
                                       :model plot-type
                                       :label-style (if (= p plot-type) {:font-weight "bold"})
                                       :on-change #(rf/dispatch [:learning/set-plot-type %])]))]]]
                [gap :size "30px"]
                [h-box
                 :children [[label :label "Marginals:"]
                            [gap :size "10px"]
                            [box
                             :style {:padding-top "3px"}
                             :child [selection-list
                                     :choices (vec (for [c [:1D :2D]]
                                                     {:id c :label (name c)}))
                                     :disabled? (= plot-type :mutual-information)
                                     :model marginal-types
                                     :on-change #(rf/dispatch [:learning/set-marginal-types %])]]]]
                [h-box
                 :children [[label :label "Columns:"]
                            [gap :size "10px"]
                            [box
                             :style {:padding-top "3px"}
                             :child [selection-list
                                     :choices (vec (for [c column-list]
                                                     {:id c :label (name c)}))
                                     :disabled? (= plot-type :mutual-information)
                                     :model col-selection
                                     :on-change #(rf/dispatch [:learning/select-cols %])]]]]]]))



