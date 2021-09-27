(ns inferenceql.viz.panels.learning.views
  (:require [re-frame.core :as rf]
            [re-com.core :refer [v-box h-box box slider label gap
                                 selection-list radio-button hyperlink]]
            [inferenceql.viz.config :refer [config transitions]]))

(defn panel
  [column-list mi-min mi-max]
  (let [iteration @(rf/subscribe [:learning/iteration])
        col-selection @(rf/subscribe [:learning/col-selection])
        plot-type @(rf/subscribe [:learning/plot-type])
        marginal-types @(rf/subscribe [:learning/marginal-types])
        show-plot-options @(rf/subscribe [:learning/show-plot-options])
        mi-threshold @(rf/subscribe [:learning/mi-threshold])]
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
                                     :on-change (fn [iter]
                                                  (rf/dispatch [:learning/select-cluster nil])
                                                  (rf/dispatch [:learning/set-iteration iter]))]]
                            [gap :size "10px"]
                            [label :label iteration]]]
                [gap :size "20px"]
                [hyperlink :label (if show-plot-options "plot options" "hide")
                           :on-click #(rf/dispatch [:learning/toggle-plot-options])]
                [v-box
                 :style {:display (if show-plot-options "block" "none")}
                 :children [[h-box
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
                            [gap :size "10px"]
                            [h-box
                             :children [[label :label "edge threshold:"]
                                        [gap :size "10px"]
                                        [box
                                         :style {:padding-top "3px"}
                                         :child [slider
                                                 :min mi-min
                                                 :max mi-max
                                                 :step (/ (- mi-max mi-min)
                                                          100)
                                                 :disabled? (not= plot-type :mutual-information)
                                                 :model mi-threshold
                                                 :on-change (fn [val]
                                                              (rf/dispatch [:learning/set-mi-threshold val]))]]
                                        [gap :size "10px"]
                                        [label :label mi-threshold]]]
                            [gap :size "10px"]
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
                            [gap :size "10px"]
                            [h-box
                             :children [[label :label "Columns:"]
                                        [gap :size "16px"]
                                        [box
                                         :style {:padding-top "3px"}
                                         :child [selection-list
                                                 :choices (vec (for [c column-list]
                                                                 {:id c :label (name c)}))
                                                 :disabled? (= plot-type :mutual-information)
                                                 :model col-selection
                                                 :on-change #(rf/dispatch [:learning/select-cols %])]]]]]]]]))



