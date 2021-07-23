(ns inferenceql.viz.panels.sd2.start.views
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [re-com.core :refer [v-box h-box box gap line hyperlink checkbox info-button]]
            [inferenceql.viz.panels.viz.views :as viz]
            [re-frame.core :as rf]))

(defn time-series [data]
  {:$schema "https://vega.github.io/schema/vega-lite/v5.json",
   :title "Growth curves"
   :config {:view {:stroke nil}}
   :data {:values data}
   ;;:transform [{:calculate "datum.status == 'rec' ? 1 : 0" :as "zorder"}]
   :resolve {:axis {:x "independent" :y "independent"}}
   :width 780
   :height 780
   :encoding {:color {:field "status", :type "nominal"
                      :scale {:domain ["recommended", "not-recommended"] :range ["#4e79a7" "#f28e2b"]}
                      :legend nil}
              :order {:condition {:param "pts"
                                  :value 1}
                      :value 0}
              :opacity {:condition {:param "pts",
                                    :value 1}
                        :value 0.1}}
   :layer [{:mark {:type "point" :tooltip {:content "data"}}
            :params [#_{:name "hover"
                        :select {:type "point",
                                 :fields ["gene"],
                                 :on "mouseover"}}
                     #_{:name "zoom-pan-control"
                        :bind "scales"
                        :select {:type "interval"
                                 :on "[mousedown[event.shiftKey], window:mouseup] > window:mousemove"
                                 :translate "[mousedown[event.shiftKey], window:mouseup] > window:mousemove"
                                 :clear "dblclick[event.shiftKey]"
                                 :zoom "wheel![event.shiftKey]"}}
                     {:name "xypoint"
                      :select {:type "point",
                               :fields ["time", "OD600"]
                               :on "mousedown"
                               :nearest true}}
                     {:name "pts"
                      :select {:type "point",
                               :fields ["gene"],
                               :on "mousedown"
                               :nearest true}}]
            :encoding {:x {:field "time", :type "quantitative"
                           :axis nil}
                       :y {:field "OD600", :type "quantitative"
                           :axis nil}
                       :opacity {:value 0}}}

           {:mark {:type "line"
                   :strokeWidth 2}
            :encoding {:x {:field "time", :type "quantitative"
                           :axis {:grid true
                                  :values (range 0 700 25)
                                  :labelOverlap true
                                  :labelFontSize 12
                                  ;;:labelSeparation 5
                                  :title "minutes"}}
                       :y {:field "OD600", :type "quantitative"
                           :axis {:grid true
                                  :title "optical density (OD600)"}}
                       ;:values (range 0 1.70 0.4)}}
                       ;; TODO: make stroke solid on selection.
                       :strokeDash {:field "gene" :type "nominal" :legend nil}}}


           {:encoding {:x {:aggregate "max"
                           :field "time"
                           :type "quantitative"
                           :axis nil}
                       :y {:aggregate {:argmax "time"}
                           :field "OD600"
                           :type "quantitative"
                           :axis nil}}
            :layer [{:mark {:type "circle"}
                     :encoding {:detail {:field "gene" :type "nominal"}}}
                    {:mark {:type "text" :align "left" :dx 4}
                     ;; TODO: make text bold on selection.
                     :encoding {:text {:field "gene" :type "nominal"}}}]}]})


(defn gene-selector [gene-selection-list gene-clicked]
  (let [next-gene (r/atom nil)
        prev-gene (r/atom nil)
        keydown-handler (fn [e]
                          (case (.-code e)
                            "ArrowDown"
                            (rf/dispatch [:sd2-start/set-gene-clicked @next-gene])

                            "ArrowUp"
                            (rf/dispatch [:sd2-start/set-gene-clicked @prev-gene])

                            "Escape"
                            (rf/dispatch [:sd2-start/set-gene-clicked nil])

                            nil))
        dbl-click-handler (fn [e] (rf/dispatch [:sd2-start/set-gene-clicked nil]))]
    (r/create-class
      {:display-name "gene-selector"

       :component-did-mount
       (fn [this]
         (.addEventListener js/window "keydown" keydown-handler)
         ;; NOTE: This is hackey because the "gene-selector-pane" DOM node doesn't belong
         ;; to this component. This should be moved.
         (when-let [bg-pane (.getElementById js/document "gene-selector-pane")]
           (.addEventListener bg-pane "dblclick" dbl-click-handler)))

       :component-will-unmount
       (fn [this]
         (.removeEventListener js/window "keydown" keydown-handler)
         (when-let [bg-pane (.getElementById js/document "gene-selector-pane")]
           (.removeEventListener bg-pane "dblclick" dbl-click-handler)))

       :reagent-render
       (fn [gene-selection-list gene-clicked]
         (let [gene-order (map vector
                               (range)
                               (map (comp name second) gene-selection-list))
               cur-gene-index (when gene-clicked
                                (some (fn [[idx gene]]
                                        (when (= gene gene-clicked)
                                          idx))
                                      gene-order))
               prev-gene-index (when (and (some? cur-gene-index) (not= cur-gene-index 0))
                                 (dec cur-gene-index))
               next-gene-index (when (and (some? cur-gene-index) (not= cur-gene-index (dec (count gene-order))))
                                 (inc cur-gene-index))]

           (reset! next-gene (some->> next-gene-index (nth gene-order) second))
           (reset! prev-gene (some->> prev-gene-index (nth gene-order) second))
           [:div {:style {:display "flex"
                          :align-items "stretch"
                          :max-height "759px"
                          :flex-flow "column wrap"
                          :flex "0 0 auto"
                          :justify-content "flex-start"}}
            (for [row gene-selection-list]
              (let [[value gene-key rec] row
                    gene-name (name gene-key)
                    clicked (= gene-name gene-clicked)]
                ^{:key gene-name}
                [h-box
                 :style {:width (if clicked "153px" "135px")
                         :margin-left (when clicked "-18px")}
                 :children [(when clicked
                              [:div {:style {:margin-right "5px"}} "➔️"])
                            [:div {:style {:background-color (if rec "#d7e4f4" "#ffdbb8")
                                           ;;:width "50%"
                                           :cursor "pointer"}
                                   :on-click (fn [e]
                                               (rf/dispatch [:sd2-start/set-gene-clicked gene-name]))}
                             gene-name]
                            (when clicked
                              [:<>
                               [gap :size "10px"]
                               [hyperlink
                                :style {:font-weight "500"
                                        :padding "0px 5px"
                                        :box-shadow "0px 0px 0px 1px #457ab2 inset"};
                                :on-click (fn [e]
                                            (rf/dispatch [:sim/set-target-gene (keyword gene-clicked)])
                                            (rf/dispatch [:set-page :knockout-sim])
                                            (.blur (.-target e)))
                                :label "continue"]])]]))]))})))


(defn view [show]
  (let [gene-clicked @(rf/subscribe [:sd2-start/gene-clicked])
        rec-gene-filter @(rf/subscribe [:sd2-start/rec-gene-filter])
        not-rec-gene-filter @(rf/subscribe [:sd2-start/not-rec-gene-filter])
        gene-selection-list @(rf/subscribe [:sd2-start/gene-selection-list])
        plot-data @(rf/subscribe [:sd2-start/plot-data])
        show-growth-curves @(rf/subscribe [:sd2-start/show-growth-curves])]
    [h-box
     :style {:display (when-not show "none")
             :height "100%"
             :min-height "920px"}
     :children
     [[v-box
       :size "4"
       :attr {:id "gene-selector-pane"}
       :style {:padding "25px 50px"
               :min-width "740px"
               :background "#f0f0f0"}
       :children [[h-box
                   :align :center
                   :min-height "40px"
                   :children (if gene-clicked
                               [[:span {:style {:font-weight "500"
                                                :line-height "1.1"
                                                :font-size "36px"}}
                                 gene-clicked]]
                               [[:span {:style {:font-weight "500"
                                                :line-height "1.1"
                                                :font-size "24px"}}
                                 "Select a target gene"]])]
                  [gap :size "15px"]
                  [h-box
                   :align :center
                   :children [[checkbox
                               :model rec-gene-filter
                               :on-change (fn [e] (rf/dispatch [:sd2-start/set-rec-genes-filter e]))
                               :label [:div {:style {:background-color "#d7e4f4"
                                                     :padding "0px 5px 0px 5px"
                                                     :font-weight "bold"}}
                                       "recommended"]]
                              [gap :size "20px"]
                              [checkbox
                               :model not-rec-gene-filter
                               :on-change (fn [e] (rf/dispatch [:sd2-start/set-not-rec-genes-filter e]))
                               :label [:div {:style {:background-color "#ffdbb8"
                                                     :padding "0px 5px 0px 5px"
                                                     :font-weight "bold"}}
                                       "not-recommended"]]
                              [gap :size "20px"]
                              [checkbox
                               :model show-growth-curves
                               :on-change (fn [e] (rf/dispatch [:sd2-start/set-show-growth-curves e]))
                               :label [:div {:style {:padding "0px 5px 0px 5px"}}
                                       "show growth curves"]]]]
                  [gap :size "10px"]
                  [h-box
                   :align :center
                   :style {:margin-left "-1px"}
                   :children [[info-button
                               :style {:fill "#878484"}
                               :info [:span (str "Genes are ordered by their final "
                                                 "optical density (OD600) values.")]]
                              [gap :size "5px"]
                              [:span "sort order"]]]
                  [gap :size "15px"]
                  [gene-selector gene-selection-list gene-clicked]]]
      [box :size "8" :margin "70px 0px 0px 5px"
       :child (if (seq plot-data)
                [:div {:style {:display (when-not show-growth-curves "none")}}
                 (.log js/console :plot-data plot-data)
                 [viz/vega-lite (time-series plot-data) {:actions false :renderer "canvas"} :start-page]]
                [:div])]]]))




