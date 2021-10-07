(ns inferenceql.viz.panels.control.views
  (:require [re-frame.core :as rf]
            [re-com.core :refer [v-box h-box box slider label gap
                                 selection-list radio-button hyperlink]]
            [inferenceql.viz.store :refer [schema col-ordering
                                           xcat-models mutual-info]]))

(def mi-bounds
  (if (seq mutual-info)
    (let [mi-vals (flatten
                   (for [mi-crosscat-sample mutual-info]
                     (for [mi-model-iter mi-crosscat-sample]
                       (for [[_col-1 inner-map] (:mi mi-model-iter)]
                         (for [[_col-2 mi-val] inner-map]
                           mi-val)))))]
      {:min (apply min mi-vals)
       :max (apply max mi-vals)})
    {:min 0
     :max 1}))

(def column-list (keep (set (keys schema)) col-ordering))

(defn panel []
  (let [iteration @(rf/subscribe [:control/iteration])
        col-selection @(rf/subscribe [:control/col-selection])
        plot-type @(rf/subscribe [:control/plot-type])
        marginal-types @(rf/subscribe [:control/marginal-types])
        show-plot-options @(rf/subscribe [:control/show-plot-options])
        mi-threshold @(rf/subscribe [:control/mi-threshold])]
    [v-box
     :padding "20px 20px 10px 20px"
     :margin "0px 0px 20px 0px"
     :style {:background-color "#f5f5f5"
             :border-bottom "1px solid #ececec"}
     :children [[h-box
                 :children [[label :label "Iteration:"]
                            [gap :size "10px"]
                            [box
                             :style {:padding-top "3px"}
                             :child [slider
                                     :min 0
                                     :max (dec (count xcat-models))
                                     :model iteration
                                     :on-change (fn [iter]
                                                  (rf/dispatch [:control/clear-cluster-selection])
                                                  (rf/dispatch [:control/set-iteration iter]))]]
                            [gap :size "10px"]
                            [label :label iteration]]]
                [gap :size "20px"]
                [hyperlink :label (if show-plot-options "hide" "Plot options")
                           :on-click #(rf/dispatch [:control/toggle-plot-options])]
                [gap :size "10px"]
                [v-box
                 :style {:display (if show-plot-options "flex" "none")}
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
                                                   :on-change #(rf/dispatch [:control/set-plot-type %])]))]]]
                            [gap :size "20px"]
                            (when (= plot-type :mutual-information)
                              [h-box
                               :children [[label :label "Edge threshold:"]
                                          [gap :size "10px"]
                                          [box
                                           :style {:padding-top "3px"}
                                           :child [slider
                                                   :min (:min mi-bounds)
                                                   :max (:max mi-bounds)
                                                   :step (/ (- (:max mi-bounds) (:min mi-bounds))
                                                            100)
                                                   :disabled? (not= plot-type :mutual-information)
                                                   :model mi-threshold
                                                   :on-change (fn [val]
                                                                (rf/dispatch [:control/set-mi-threshold val]))]]
                                          [gap :size "10px"]
                                          [label :label mi-threshold]]])
                            [gap :size "10px"]
                            (when (= plot-type :select-vs-simulate)
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
                                                   :on-change #(rf/dispatch [:control/set-marginal-types %])]]]])
                            [gap :size "10px"]
                            (when (= plot-type :select-vs-simulate)
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
                                                   :on-change #(rf/dispatch [:control/select-cols %])]]]])]]]]))



