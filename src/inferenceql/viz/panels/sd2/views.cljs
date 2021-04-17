(ns inferenceql.viz.panels.sd2.views
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

(defn scroll-into-view
  [this _old-argv]
  (.scrollIntoView js/window
                   (rdom/dom-node this)
                   (clj->js {:scrollMode "if-needed" :behavior "smooth" :block "center" :inline "start"})))

(defn cats-string [col-params]
  (let [kv-strings (for [[cat-val cat-weight] col-params]
                     (format "\"%s\": %.3f" cat-val cat-weight))]
    (string/join ", " kv-strings)))

(defn template-data [stat-types params]
  (let [last-index (-> params count dec)
        columns (reduce
                 (fn [acc [index [col-name col-params]]]
                   (conj acc (case (get stat-types col-name)
                               :categorical
                               {:col-name (name col-name) :categorical true
                                :cats-string (cats-string col-params)
                                :last (= index last-index)}
                               :gaussian
                               {:col-name (name col-name) :gaussian true
                                :mu (format "%.3f" (:mu col-params))
                                :sigma (format "%.3f" (:sigma col-params))
                                :last (= index last-index)})))
                 []
                 (map-indexed vector params))]
    {:columns columns}))

(defn js-fn-text [stat-types params]
  (let [template-data (template-data stat-types params)
        rendered (render (:cluster-fn-template config) template-data)]
    (->> rendered
         (string/split-lines)
         (remove string/blank?)
         (string/join "\n"))))

(defn js-code-block
  [js-code display]
  (let [dom-nodes (r/atom {})]
    (r/create-class
     {:display-name "js-model-code"

      :component-did-update
      scroll-into-view

      :component-did-mount
      (fn [this]
        (.highlightBlock js/hljs (:program-display @dom-nodes)))

      :reagent-render
      (fn []
        [:pre.program-display {:ref #(swap! dom-nodes assoc :program-display %)}
         [:code {:class "js"}
          js-code]])})))

(defn cluster-output [view-id cat-id]
  (let [output (rf/subscribe [:sd2/cluster-output view-id cat-id])]
    (r/create-class
     {:component-did-update
      scroll-into-view

      :reagent-render
      (fn [view-id cat-id]
        (when @output
          [:pre.cat-group-highlighted @output]))})))

(defn xcat-category [view view-id cat-id]
  (let [open (rf/subscribe [:sd2/cluster-open view-id cat-id])
        stat-types (medley/map-vals :stattype (:columns view))
        params (reduce-kv
                (fn [acc col-name col-gpm]
                  (let [;; If there is no category for a given column, this means
                        ;; that there is no associated data with that column in the rows within
                        ;; that category. Because the types are collapsed, we can generate
                        ;; a new (empty) category for that column.
                        col-cat (get-in col-gpm [:categories cat-id] (column/generate-category col-gpm))
                        exported-cat (pgpms/export-category (get stat-types col-name) col-cat)]
                    (merge acc exported-cat)))
                {}
                (:columns view))]
    (r/create-class
     {:reagent-render
      (fn []
        (let [display (if @open "block" "none")
              more-icon-path (if @open "resources/icons/expand_less_black_48dp.svg"
                                       "resources/icons/expand_more_black_48dp.svg")]
          [:div
           [v-box
            :gap "5px"
            :children [[h-box
                        :gap "5px"
                        :children [[:button.toolbar-button.pure-button.more-button
                                    {:class (when false "pure-button-active pure-button-hover")
                                     :on-click (fn [e]
                                                 (rf/dispatch [:sd2/toggle-cluster view-id cat-id])
                                                 (.blur (.-target e)))}
                                    [:object.more-icon {:type "image/svg+xml" :data more-icon-path}
                                     "expand content"]]
                                   [:div {:style {:font-size "24px" :font-weight "500"
                                                  :line-height "1.1" :color "inherit"}}
                                    cat-id]]]
                       [:div {:style {:display display :margin-left "40px"}}
                        [v-box :children [[gap :size "15px"]
                                          [js-code-block (js-fn-text stat-types params) display]
                                          [cluster-output view-id cat-id]
                                          [gap :size "15px"]]]]]]]))})))

(defn scale [weights]
  (let [weights (map second weights)
        min-w (apply min weights)
        max-w (apply max weights)

        lin (.scale js/vega "linear")
        ;; TODO: try with different color scale.
        scheme (.scheme js/vega "blues")
        scale-fn (doto (lin)
                       (.domain [min-w max-w])
                       (.range [0 1]))]
    (fn [weight]
      (scheme (scale-fn weight)))))

(defn cat-weight [view-id cat-id scale weight]
  (let [hl (rf/subscribe [:sd2/cluster-weight-highlighted view-id cat-id])]
    (r/create-class
     {:reagent-render
      (fn [view-id cat-id scale weight]
        [:div {:class ["cat-group-container" (when false "cat-group-highlighted")]}
          [:div.cat-group {:style {:border-color (scale weight)}}
           [:div.cat-name (str (name cat-id) ":")]
           [:div.cat-weight (format "%.3f" weight)]]])})))

(defn cat-output [view-id]
  (let [output (rf/subscribe [:sd2/view-cat-selection view-id])]
    (r/create-class
     {:component-did-update
      scroll-into-view

      :reagent-render
      (fn [view-id]
        (when @output
          [:pre.cat-group-highlighted @output]))})))

(defn cats [view-id weights]
  (let [ scale (scale weights)]
    (r/create-class
     {:reagent-render
      (fn [view-id weights]
        [:div {:id (name view-id)}
         [:div.cats
          [:h4 {:style {:margin "0px" :margin-bottom "5px" :font-size "14px"}}
           "sample a cluster to use"]
          [:div {:style {:margin-left "-10px"}}
           (for [[cat-id weight] weights]
             [cat-weight view-id cat-id scale weight])]]
         [cat-output view-id]])})))

(defn xcat-view [view-id view constraints]
  (let [columns (-> view :columns keys)
        columns (map name columns)
        weights (->> (view/category-weights view constraints)
                     (medley/map-vals Math/exp)
                     (sort-by first))]
    [:div {:style {:width "750px" :margin-left "20px"}}
      [v-box :children [[h-box
                         :gap "15px"
                         :children [[:h2 {:style {:display "inline" :margin "0px"}} view-id]
                                    [:h4 {:style {:display "inline" :margin-top "9px" :margin-bottom "0px"}}
                                     "(" (string/join ", " columns) ")"]]]
                        [:div {:style {:margin-left "20px"}}
                         [v-box :children [[gap :size "20px"]
                                           [cats view-id weights]
                                           [gap :size "20px"]
                                           [:div {:style {:margin-left "-15px"}}
                                             (for [[cat-id _] weights]
                                               [xcat-category view view-id cat-id])]
                                           [gap :size "20px"]]]]]]]))

(defn model-output []
  (let [output (rf/subscribe [:sd2/model-output])]
    (r/create-class
     {:component-did-update
      scroll-into-view

      :reagent-render
      (fn []
        (when @output
          [:div {:style {:width "770px"}}
           [:h1 "model output"]
           [:pre.cat-group-highlighted @output]]))})))

(defn view [model constraints]
  [:div
   [v-box :children [[:h1 "model"]
                     (for [[view-id view] (:views model)]
                       [:<>
                        [gap :size "30px"]
                        [xcat-view view-id view constraints]])
                     [gap :size "40px"]
                     [model-output]]]])

;; TODO: remember to pass in all the component args into render function in
;; type 2 and 3 components.