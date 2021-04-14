(ns inferenceql.viz.panels.sd2.views
  (:require [reagent.core :as r]
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
            [inferenceql.viz.config :refer [config]]))

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
  [js-code]
  (let [dom-nodes (r/atom {})]
    (r/create-class
     {:display-name "js-model-code"

      :component-did-mount
      (fn [this]
        (.highlightBlock js/hljs (:program-display @dom-nodes)))

      :reagent-render
      (fn []
        [:pre.program-display {:ref #(swap! dom-nodes assoc :program-display %)}
         [:code {:class "js"}
          js-code]])})))


(defn xcat-category [view cat-name hidden]
  (let [hidden (r/atom true)
        stat-types (medley/map-vals :stattype (:columns view))
        params (reduce-kv
                (fn [acc col-name col-gpm]
                  (let [;; If there is no category for a given column, this means
                        ;; that there is no associated data with that column in the rows within
                        ;; that category. Because the types are collapsed, we can generate
                        ;; a new (empty) category for that column.
                        col-cat (get-in col-gpm [:categories cat-name] (column/generate-category col-gpm))
                        exported-cat (pgpms/export-category (get stat-types col-name) col-cat)]
                    (merge acc exported-cat)))
                {}
                (:columns view))]
    (fn []
      (let [display (if @hidden "none" "block")
            more-icon-path (if @hidden "resources/icons/expand_more_black_48dp.svg"
                                       "resources/icons/expand_less_black_48dp.svg")]
        [:div
         [v-box
          :gap "5px"
          :children [[h-box
                      :gap "5px"
                      :children [[:button.toolbar-button.pure-button.more-button
                                  {:class (when false "pure-button-active pure-button-hover")
                                   :on-click (fn [e]
                                               ;; set atom
                                               (swap! hidden not)
                                               (.blur (.-target e)))}
                                  [:object.more-icon {:type "image/svg+xml" :data more-icon-path}
                                   "expand content"]]
                                 [:div {:style {:font-size "24px" :font-weight "500"
                                                :line-height "1.1" :color "inherit"}}
                                  cat-name]]]

                     [:div {:style {:display display
                                    :margin-left "40px"}}
                       [js-code-block (js-fn-text stat-types params)]]]]]))))

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


(defn xcat-view [view-id view constraints]
  (let [columns (-> view :columns keys)
        columns (map name columns)
        weights (->> (view/category-weights view constraints)
                     (medley/map-vals Math/exp)
                     (sort-by first))
        scale (scale weights)]
    [:div {:style {:width "750px"}}
      [:h2 view-id]
      [:h4 "columns: " (string/join ", " columns)]
      [:div.cats
        (for [[cat-name weight] weights]
          [:div.cat-group {:style {:border-color (scale weight)}}
            [:div.cat-name (str (name cat-name) ":")]
            [:div.cat-weight (format "%.3f" weight)]])]
      (for [[cat-name _] weights]
        [xcat-category view cat-name true])]))

(defn view [model constraints]
  [:div
    [:h1 "xcat-model"]
    (for [[view-id view] (:views model)]
      [xcat-view view-id view constraints])])
