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
            [re-com.core :refer [border title v-box p h-box line button gap input-text
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
  (let [level (rf/subscribe [:sim/expr-level])]
    [h-box
     :style {:width "400px"}
     :children [[:span "Expression level: "]
                [:input {:type :range :name :expr-level
                         :min 0 :max 100 :step 1
                                 :value @level
                                 :on-change (fn [e]
                                              ;; TODO find a way to debounce this callback -- still needed?
                                              (rf/dispatch [:sim/set-expr-level
                                                            (js/parseFloat (-> e .-target .-value))]))}]
                [:label @level]]]))

(defn view []
  [v-box
   :children [[h-box
               :gap "10px"
               :children [[:button.toolbar-button.pure-button
                           {:on-click (fn [e] (rf/dispatch [:sim/one])
                                        (.blur (.-target e)))}
                           "Simulate one"]
                          [:button.toolbar-button.pure-button
                           {:on-click (fn [e] (rf/dispatch [:sim/many])
                                              (.blur (.-target e)))}
                           "Simulate many"]
                          [:button.toolbar-button.pure-button
                           {:on-click (fn [e] (rf/dispatch [:sd2/clear-animation])
                                              (.blur (.-target e)))}
                           "Clear"]]]
              [expr-level-slider]]])
