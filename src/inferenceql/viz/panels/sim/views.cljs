(ns inferenceql.viz.panels.sim.views
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [inferenceql.inference.gpm.view :as view]
            [inferenceql.inference.gpm.column :as column]
            [inferenceql.inference.gpm.primitive-gpms :as pgpms]
            [clojure.pprint :refer [pprint]]
            [re-frame.core :as rf]
            [medley.core :as medley]
            [goog.string :refer [format]]
            [re-com.core :refer [border title v-box p h-box box line button gap input-text
                                 checkbox horizontal-bar-tabs horizontal-tabs
                                 radio-button]]
            [clojure.string :as string]
            [goog.string :refer [format]]
            [cljsjs.highlight]
            [cljsjs.highlight.langs.javascript]
            [cljstache.core :refer [render]]
            [inferenceql.viz.config :refer [config]]
            [yarn.scroll-into-view]))

(defn expr-level-slider []
  (let [level (rf/subscribe [:sim/expr-level])
        conditioned (rf/subscribe [:sim/conditioned])]
    [v-box
     :margin "5px 20px"
     :children [[box :child [:span "Expression level (target gene): "]]
                [h-box
                 :style {:margin-left "20px"
                         :margin-top "5px"}
                 :children [[checkbox
                             :model @conditioned
                             :on-change #(rf/dispatch [:sim/set-conditioned %])]
                            [gap :size "10px"]
                            [box
                             :child [:input {:type :range :name :expr-level
                                             :disabled (not @conditioned)
                                             :min 0 :max 100 :step 1
                                                     :value @level
                                                     :on-change (fn [e]
                                                                  ;; TODO find a way to debounce this callback -- still needed?
                                                                  (rf/dispatch [:sim/set-expr-level
                                                                                (js/parseFloat (-> e .-target .-value))]))}]]
                            [gap :size "10px"]
                            [box :child [:label (if @conditioned
                                                  (str "constrained at " @level)
                                                  "(unconstrained)")]]]]]]))

(defn view [target-gene essential-genes]
  [v-box
   :children [[:h4 (str "Target gene: " (name target-gene))]
              [:h4 (str "Essential genes: " (string/join ", " (map name essential-genes)))]
              [:h2 "simulation controls: "]
              [gap :size "5px"]
              [expr-level-slider]
              [gap :size "20px"]
              [h-box
               :style {:margin "5px 15px 0px 15px"}
               :gap "10px"
               :children [[:button.toolbar-button.pure-button
                           {:on-click (fn [e] (rf/dispatch [:sim/simulate-one])
                                        (.blur (.-target e)))}
                           "Simulate 1 point"]
                          [:button.toolbar-button.pure-button
                           {:on-click (fn [e] (rf/dispatch [:sd2/clear-animation])
                                              (rf/dispatch [:sim/simulate-many])
                                              (.blur (.-target e)))}
                           "Simulate 10 points"]
                          [:button.toolbar-button.pure-button
                           {:on-click (fn [e] (rf/dispatch [:sd2/clear-animation])
                                              (rf/dispatch [:sim/clear-simulations])
                                              (.blur (.-target e)))}
                           "Clear simulations"]]]]])