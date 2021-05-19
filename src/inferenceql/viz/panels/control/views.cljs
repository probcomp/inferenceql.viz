(ns inferenceql.viz.panels.control.views
  (:require [re-frame.core :as rf]
            [reagent-forms.core :as forms]
            [re-com.core :refer [h-box]]
            [inferenceql.viz.panels.more.views :as more]
            [reagent.core :as r]
            [cljs.core.async :refer [go]]
            [cljs.core.async.interop :refer-macros [<p!]]))

(def reagent-forms-function-map
  "Function map that allows a reagent-forms template
  to communicate with the reframe db."
  {:get (fn [path] @(rf/subscribe [:control/reagent-form path]))
   :save! (fn [path value] (rf/dispatch [:control/set-reagent-forms path value]))
   :update! (fn [path save-fn value]
              ;; save-fn should accept two arguments: old-value, new-value
              (rf/dispatch [:control/update-reagent-forms save-fn path value]))
   :doc (fn [] @(rf/subscribe [:control/reagent-forms]))})

(defn selection-color
  "A reagent component for a single select option in color-selection reagent component"
  [current-selection id text]
  [:div.list-group-item.no-select
   {:key id
    ;; A css class gets attached that is the string portion of the `id` keyword.
    ;; For example: ":red" will become the ".red" css class.
    :title (str text " selection layer")
    :class [(name id) (when (= current-selection id) "active")]
    :on-click #(rf/dispatch [:control/set-selection-color id])}
   text])

(defn selection-color-selector
  "A reagant component for selecting the table selection color."
  []
  (let [cur-val @(rf/subscribe [:control/selection-color])]
    [:div#color-selector
      [:div.list-group
       [selection-color cur-val :blue "BLUE"]
       [selection-color cur-val :green "GREEN"]
       [selection-color cur-val :red "RED"]]]))

(defn panel
  "A reagant component. Acts as control and input panel for the app."
  [input-text results query-fn]
  (let []
    [:div#toolbar
     [:div#search-section
       [:textarea#search-input {:on-change #(reset! input-text (-> % .-target .-value))
                                ;; This submits the query when enter is pressed, but allows the user
                                ;; to enter a linebreak in the textarea with shift-enter.
                                :on-key-press (fn [e] (if (and (= (.-key e) "Enter") (.-shiftKey e))
                                                        (do
                                                          (go
                                                           (let [r (<p! (query-fn @input-text))]
                                                             (reset! results r)))
                                                          (.preventDefault e))))
                                :placeholder (str "Write a query here.\n"
                                                  "[enter] - inserts a newline\n"
                                                  "[shift-enter] - executes query")
                                ;; This random attribute value for autoComplete is needed to turn
                                ;; autoComplete off in Chrome. "off" and "false" do not work.
                                :autoComplete "my-search-field"
                                ;; Disables text correction on iOS Safari.
                                :autoCorrect "off"
                                :autoCapitalize "none"
                                ;; HTML5 attr, browser support limited.
                                :spellCheck "false"
                                :value @input-text}]
       [h-box
        :attr {:id "search-buttons"}
        :justify :end
        :children [[:button.toolbar-button.pure-button
                    {:on-click (fn [e]
                                 (go
                                   (let [r (<p! (query-fn @input-text))]
                                     (reset! results r)))
                                 (.blur (.-target e)))}
                    "Run query"]
                   [:button.toolbar-button.pure-button
                    {:on-click (fn [e]
                                  (reset! results nil)
                                  (.blur (.-target e)))}
                    "Clear results"]]]]]))
