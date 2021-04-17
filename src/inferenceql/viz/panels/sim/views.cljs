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


(defn view []
  [h-box
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
               {:on-click (fn [e] (rf/dispatch [:sim/clear])
                                  (.blur (.-target e)))}
               "Clear"]]])
