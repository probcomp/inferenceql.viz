(ns inferenceql.viz.panels.sd2.model.views
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [inferenceql.inference.gpm.view :as view]
            [inferenceql.inference.gpm.column :as column]
            [inferenceql.inference.gpm.primitive-gpms :as pgpms]
            [clojure.pprint :refer [pprint]]
            [clojure.edn :as edn]
            [clojure.string :as string]
            [cljstache.core :refer [render]]
            [re-frame.core :as rf]
            [re-com.core :refer [border title v-box p h-box line button gap input-text
                                 checkbox horizontal-bar-tabs horizontal-tabs
                                 radio-button info-button]]
            [medley.core :as medley]
            [goog.string :refer [format]]
            [oops.core :refer [ocall]]
            [vega :as yarn-vega]
            ["highlight.js/lib/core" :as yarn-hljs]
            ["highlight.js/lib/languages/javascript" :as yarn-hljs-js]
            [inferenceql.viz.config :refer [config]]))

;; We are using the minimal version of highlight.js where
;; every language used has to be registered individually.
(.registerLanguage yarn-hljs "javascript" yarn-hljs-js)

(defn sd2-cat-rename
  "utility fn"
  [cat-id]
  (let [cat-num (some->> (name cat-id)
                         (re-matches #"cluster_(.+)")
                         (second))]
    (if cat-num
      (str "regime_" cat-num)
      ;; Alternate name for AUX category.
      "regime_prior")))

(defn sd2-view-rename
  "utility fn"
  [view-id]
  (let [view-num (some->> (name view-id)
                          (re-matches #"view_(.+)")
                          (second))]
    (if view-num
      (str "regulon_" view-num)
      (name view-id))))

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
  (r/create-class
   {:display-name "js-model-code"

    :component-did-update
    (fn [this]
      (.highlightElement yarn-hljs (rdom/dom-node this)))

    :reagent-render
    (fn [js-code]
      ^{:key js-code} [:pre.program-display
                       [:code {:class "js"}
                        js-code]])}))

(defn cluster-output [view-id cat-id]
  (let [output (rf/subscribe [:sd2/cluster-output view-id cat-id])]
    (fn [view-id cat-id]
      (when @output
        [:pre.cat-group-highlighted @output]))))

(defn points-badge [points-count]
  (when (> points-count 0)
    [:div.points-badge points-count]))

(defn xcat-category [column-gpms view-id cat-id]
  (let [open (rf/subscribe [:sd2/cluster-open view-id cat-id])
        points-count (rf/subscribe [:sim/points-count view-id cat-id])

        stat-types (medley/map-vals :stattype column-gpms)
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
                column-gpms)
        display (if @open "block" "none")
        more-icon-path (if @open "resources/icons/expand_less_black_48dp.svg"
                                 "resources/icons/expand_more_black_48dp.svg")]
    [:div {:id (str (name view-id) "--" (name cat-id))}
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
                             [:div
                              {:style {:font-size "18px" :font-weight "500"
                                       :line-height "1.1" :color "inherit"
                                       :cursor "pointer"}
                               :on-click (fn [e]
                                           (rf/dispatch [:sd2/toggle-cluster view-id cat-id]))}
                              (sd2-cat-rename cat-id)]
                             [points-badge @points-count]]]
                 [:div {:style {:display display :margin-left "40px"}}
                  [v-box :children [[gap :size "15px"]
                                    [js-code-block (js-fn-text stat-types params)]
                                    [cluster-output view-id cat-id]
                                    [gap :size "15px"]]]]]]]))

(defn scale [weights]
  (let [weights (map second weights)
        min-w (apply min weights)
        max-w (apply max weights)

        lin (.scale yarn-vega "linear")
        ;; TODO: try with different color scale.
        scheme (.scheme yarn-vega "blues")

        scale-fn (let [s (lin)]
                   (ocall s "domain" [min-w max-w])
                   (ocall s "range" [0 1]))]
    (fn [weight]
      (scheme (scale-fn weight)))))

(defn cat-weight [view-id cat-id scale weight]
  [:div {:class ["cat-group-container" (when false "cat-group-highlighted")]
         :on-click #(rf/dispatch [:sd2/toggle-cluster view-id cat-id])}
    [:div.cat-group {:style {:border-color (scale weight)
                             :cursor "pointer"}}
     [:div.cat-name (sd2-cat-rename cat-id)]
     [:div.cat-weight (format "%.3f" weight)]]])

(defn cat-output [view-id]
  (let [output (rf/subscribe [:sd2/view-cat-selection view-id])]
    (fn [view-id]
      (when @output
        [:pre.cat-group-highlighted (sd2-cat-rename @output)]))))

(defn cats [view-id weights]
  (let [scale (scale weights)]
    [:div
     [:div.cats
      [:h4 {:style {:margin "0px" :margin-bottom "5px" :font-size "14px"}}
       "regime weights"]
      [:div {:style {:margin-left "-10px"}}
       (for [[cat-id weight] weights]
         ^{:key [view-id cat-id]} [cat-weight view-id cat-id scale weight])]]
     [cat-output view-id]]))

(defn xcat-view [view-id view columns-used constraints]
  (let [columns (-> view :columns keys)
        columns (filter columns-used columns)
        columns (map name columns)
        sort-fn (fn [cat-1 cat-2]
                  (let [cat-1-num (some-> cat-1 name (string/split #"_" 2) second edn/read-string)
                        cat-2-num (some-> cat-2 name (string/split #"_" 2) second edn/read-string)]
                    (if (and (number? cat-1-num) (number? cat-2-num))
                      (< cat-1-num cat-2-num)
                      (> cat-1 cat-2))))
        weights (->> (view/category-weights view constraints)
                     (medley/map-vals Math/exp)
                     (sort-by first sort-fn))
        column-gpms (medley/filter-keys columns-used (:columns view))]
    [:div {:id (name view-id) :style {:margin-left "20px"}}
      [v-box :children [[h-box
                         :gap "15px"
                         :children [[:h4 {:style {:display "inline" :margin "0px"}} (sd2-view-rename view-id)]
                                    [:h4 {:style {:display "inline" :margin "0px"}}
                                     (if (seq columns)
                                       (str "(" (string/join ", " columns) ")")
                                       "[not used]")]]]
                        (if (seq columns)
                          [:div {:style {:margin-left "20px"}}
                           [v-box :children [[gap :size "15px"]
                                             [cats view-id weights]
                                             [:div {:style {:margin-left "-15px"}}
                                               (for [[cat-id _] weights]
                                                 ^{:key [view-id cat-id]} [xcat-category column-gpms view-id cat-id])]]]])]]]))

(defn model-output []
  (let [output (rf/subscribe [:sd2/model-output])]
    (fn []
      [v-box :children [[h-box
                         :children [[:h4 {:style {:font-size "16px"}}
                                     "SIMULATOR OUTPUT"]
                                    [gap :size "5px"]
                                    [info-button
                                     :style {:fill "#878484"}
                                     :info [:span "When \"Simulate 1 point\" is clicked, the model's output will appear here."]]]]
                        (if @output
                          [:pre.cat-group-highlighted @output])]])))

(defn view [model columns-used constraints]
  [:div
   [v-box :children [[h-box
                      :children [[:h4 {:style {:font-size "16px"}}
                                  "SIMULATOR"]
                                 [gap :size "5px"]
                                 [info-button
                                  :style {:fill "#878484"}
                                  :info [:span (str "Our model is a probabilistic program. "
                                                    "It can be executed to produce virtual data.")]]]]
                     [gap :size "30px"]
                     (for [[view-id view] (:views model)]
                       ^{:key view-id}
                       [v-box :children [[xcat-view view-id view columns-used constraints]
                                         [gap :size "30px"]]])
                     [gap :size "40px"]
                     [model-output]]]])

;; TODO: remember to pass in all the component args into render function in
;; type 2 and 3 components.