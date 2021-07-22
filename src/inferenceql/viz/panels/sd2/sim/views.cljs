(ns inferenceql.viz.panels.sd2.sim.views
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
                                 radio-button hyperlink]]
            [clojure.string :as string]
            [goog.string :refer [format]]
            [cljstache.core :refer [render]]
            [inferenceql.viz.config :refer [config]]
            [komponentit.autocomplete :as autocomplete]
            [komponentit.mixins :as mixins]
            [inferenceql.viz.panels.sd2.model.views :as sd2-model]
            [inferenceql.viz.panels.viz.views :as viz]))

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


(defn simulator-controls [target-gene essential-genes all-essential-genes]
  [v-box
   :children [[h-box
               :style {:margin-bottom "30px"}
               :align :center
               :children [[:span {:style {:font-weight "500"
                                          :line-height "1.1"
                                          :font-size "24px"}}
                           "GENE KNOCKOUT ➔"]
                          [gap :size "20px"]
                          [:span {:style {:font-weight "500"
                                          :line-height "1.1"
                                          :font-size "40px"
                                          :margin-top "-5px"}}
                           (name target-gene)]
                          [gap :size "20px"]
                          [hyperlink
                           :on-click (fn [e]
                                       (rf/dispatch [:sd2/clear-animation])
                                       (rf/dispatch [:sim/clear-simulations])
                                       (rf/dispatch [:set-page :start])
                                       (.blur (.-target e)))
                           :style {:font-weight "500"
                                   :font-size "18px"
                                   :margin-top "-3px"}
                           :label "edit"]]]
              [:h4 {:style {:font-size "16px"}}
               "ESSENTIAL GENES"]
              [gap :size "5px"]
              ;; TODO: remove ability to add already included gene.
              ;; TODO: remove drop down when new gene is deleted
              [box
               :style {:margin-left "20px"}
               :child [autocomplete/multiple-autocomplete
                       {:value essential-genes
                        :placeholder (when-not (seq essential-genes) "Select one or more genes")

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
                        :max-results 50}]]
              [gap :size "30px"]
              [:h4 {:style {:font-size "16px"}}
               "SIMULATION CONTROLS"]
              [expr-level-slider]
              [gap :size "10px"]
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

(defn view [show]
  (let [vega-lite-spec @(rf/subscribe [:viz/vega-lite-spec])
        models @(rf/subscribe [:store/models])
        columns-used @(rf/subscribe [:sim/columns-used])
        constraints @(rf/subscribe [:sim/constraints])
        target-gene @(rf/subscribe [:sim/target-gene])
        essential-genes @(rf/subscribe [:sim/essential-genes])
        all-essential-genes @(rf/subscribe [:sim/all-essential-genes])]
    [h-box
     :style {:display (when-not show "none")}
     :children [[v-box
                 :size "6"
                 :style {:background "#f0f0f0"
                         :min-width "740px"}
                 :children [[box
                             :style {:padding "30px 20px 0px 30px"}
                             :child [simulator-controls target-gene essential-genes all-essential-genes]]
                            [gap :size "40px"]
                            [line :size "1px" :color "#cccccc"]
                            [gap :size "30px"]
                            [box
                             :style {:padding "0px 20px 20px 30px"}
                             :child [sd2-model/view (:model models) columns-used constraints]]]]
                (if (some? vega-lite-spec)
                  [box :size "8" :margin "40px 0px"
                   :child [viz/vega-lite vega-lite-spec {:actions false :renderer "canvas"} :knockout-sim-page]]
                  [box :size "8"
                   :style {:justify-content "center"
                           :margin "141px 0px"
                           :font-size "18px"
                           :color "grey"}
                   :child [:div "Pair plots will appear here for each essential gene selected"]])]]))
