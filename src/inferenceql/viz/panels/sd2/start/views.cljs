(ns inferenceql.viz.panels.sd2.start.views
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [re-com.core :refer [v-box h-box box gap line]]
            [inferenceql.viz.panels.sd2.start.db :refer [plot-data gene-selection-list]]
            [inferenceql.viz.panels.viz.views :as viz]
            [re-frame.core :as rf]))

(defn time-series [data]
  {:$schema "https://vega.github.io/schema/vega-lite/v5.json",
   :config {:view {:stroke nil}}
   :data {:values data}
   ;;:transform [{:calculate "datum.status == 'rec' ? 1 : 0" :as "zorder"}]
   :resolve {:axis {:x "independent" :y "independent"}}
   :width 800
   :height 800
   :encoding {:color {:field "status", :type "nominal"
                      :scale {:domain ["rec", "not-rec"] :range ["#4e79a7" "#f28e2b"]}}
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
                     {:name "xypoint"
                      :select {:type "point",
                               :fields ["time", "expr-level"]
                               :on "mousedown"
                               :nearest true}}
                     {:name "pts"
                      :select {:type "point",
                               :fields ["gene"],
                               :on "mousedown"
                               :nearest true}}]
            :encoding {:x {:field "time", :type "quantitative"
                           :axis nil}
                       :y {:field "expr-level", :type "quantitative"
                           :axis nil}
                       :opacity {:value 0}}}

           {:mark {:type "line"
                   :strokeWidth 2}
            :encoding {:x {:field "time", :type "quantitative"
                           :axis {:grid true
                                  :values (range 0 60 2)}}
                       :y {:field "expr-level", :type "quantitative"
                           :axis {:grid true}}
                       ;:values (range 0 1.70 0.4)}}
                       ;; TODO: make stroke solid on selection.
                       :strokeDash {:field "gene" :type "nominal" :legend nil}}}


           {:encoding {:x {:aggregate "max"
                           :field "time"
                           :type "quantitative"
                           :axis nil}
                       :y {:aggregate {:argmax "time"}
                           :field "expr-level"
                           :type "quantitative"
                           :axis nil}}
            :layer [{:mark {:type "circle"}
                     :encoding {:detail {:field "gene" :type "nominal"}}}
                    {:mark {:type "text" :align "left" :dx 4}
                     ;; TODO: make text bold on selection.
                     :encoding {:text {:field "gene" :type "nominal"}}}]}]})


(defn gene-selector [gene-clicked]
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
         (.addEventListener js/window "dblclick" dbl-click-handler))

       :component-will-unmount
       (fn [this]
         (.removeEventListener js/window "keydown" keydown-handler)
         (.addEventListener js/window "dblclick" dbl-click-handler))

       :reagent-render
       (fn [gene-clicked]
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
                          :max-height "800px"
                          :margin "20px"
                          :flex-flow "column wrap"
                          :flex "0 0 auto"
                          :justify-content "flex-start"}}
            (for [row gene-selection-list]
              (let [[value gene-key rec] row
                    gene-name (name gene-key)]
                ^{:key gene-name}
                [h-box
                 :style {:margin-left "60px"}
                 :children [(if (= gene-name gene-clicked)
                              [:div "➡️"])
                            [:div {:style {:background-color (if rec "#d7e4f4" "#ffdbb8")
                                           :cursor "pointer"}
                                   :on-click (fn [e]
                                               (rf/dispatch [:sd2-start/set-gene-clicked gene-name]))}
                             gene-name]]]))]))})))


(defn view []
  (let [gene-clicked @(rf/subscribe [:sd2-start/gene-clicked])]
    [h-box
     :children
     [[v-box
       :size "5"
       :style {:padding "20px"
               :background "#f0f0f0"}
       :children [[:div {:style {:margin "20px 0px 0px 80px"}}
                   (if gene-clicked
                     [h-box :children [[:h4 (str gene-clicked " selected")]
                                       [gap :size "10px"]
                                       [:button.toolbar-button.pure-button
                                        {:on-click (fn [e]
                                                     (rf/dispatch [:sim/set-target-gene (keyword gene-clicked)])
                                                     (rf/dispatch [:set-page :knockout-sim])
                                                     (.blur (.-target e)))}
                                        "continue"]]]
                     [:h4 "Select a target gene"])]
                  [gene-selector gene-clicked]]]
      [line :size "1px" :color "whitesmoke"]
      [box :size "8" :margin "40px 0px"
       :child [viz/vega-lite (time-series plot-data) {:actions false} :start-page]]]]))



