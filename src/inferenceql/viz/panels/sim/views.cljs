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
            [yarn.scroll-into-view]
            [yarn.react-chips]
            [komponentit.autocomplete :as autocomplete]
            [komponentit.mixins :as mixins]))

(defn expr-level-slider []
  (let [level (rf/subscribe [:sim/expr-level])
        conditioned (rf/subscribe [:sim/conditioned])
        settings (rf/subscribe [:sim/expr-level-slider-settings])]
    [v-box
     :margin "5px 20px"
     :children [[box :child [:span "Expression level (gene knockout): "]]
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
                                             :min (:min @settings) :max (:max @settings) :step (:step @settings)
                                             :value (or @level (:initial @settings))
                                             :on-change (fn [e]
                                                          ;; TODO find a way to debounce this callback -- still needed?
                                                          (rf/dispatch [:sim/set-expr-level
                                                                        (js/parseFloat (-> e .-target .-value))]))}]]
                            [gap :size "10px"]
                            [box :child [:label (if @conditioned
                                                  (str "constrained at " (format "%.2f" (or @level (:initial @settings))))
                                                  "(unconstrained)")]]]]]]))


(defn view [target-gene essential-genes all-essential-genes]
  [v-box
   :children [[h-box
               :style {:margin-bottom "30px"}
               :gap "25px"
               :children [[:h3 {:style {:display "inline" :margin-top "25px"
                                        :margin-bottom "0px"}} "GENE KNOCKOUT"]
                          [:h1 {:style {:display "inline" :margin-top "0px"
                                        :margin-bottom "0px" :font-size "56px"}} (name target-gene)]]]

              [:h4 "ESSENTIAL GENES"]
              [gap :size "5px"]

              ;; TODO: remove ability to add already included gene.
              ;; TODO: remove drop down when new gene is deleted
              [box
               :style {:margin-left "20px"}
               :child [autocomplete/multiple-autocomplete
                       {:value essential-genes

                        :on-change
                        #_(fn [item] (swap! value conj (:key item)))
                        #(rf/dispatch [:sim/add-essential-gene (:key %)])

                        :on-remove
                        #_(fn [v] (swap! value disj v))
                        #(do
                           #_(this-as this
                               (.log js/console :--------- this)
                               (.blur this))
                           (rf/dispatch [:sim/remove-essential-gene %]))

                        :search-fields [:value]
                        :items (zipmap all-essential-genes (map name all-essential-genes))
                        :max-results 20}]]
              [gap :size "10px"]

              #_[:> (r/adapt-react-class js/Chips)
                 {:value ["foo"]
                  :suggestions ["bar" "biz" "baz"]
                  :onChange #(.log js/console :-------output %)}]

              [:h4 "SIMULATION CONTROLS"]
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
