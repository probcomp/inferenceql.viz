(ns inferenceql.spreadsheets.panels.control.views
  (:require [re-frame.core :as rf]
            [reagent-forms.core :as forms]))

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
  [:div.list-group-item
   {:key id
    ;; A css class gets attached that is the string portion of the `id` keyword.
    ;; For example: ":red" will become the ".red" css class.
    :class [(name id) (when (= current-selection id) "active")]
    :on-click #(rf/dispatch [:control/set-selection-color id])}
   text])

(defn selection-color-selector
  "A reagant component for selecting the table selection color."
  []
  (let [cur-val @(rf/subscribe [:control/selection-color])]
    [:div#color-selector
      [:span "Selection layers"]
      [:br]
      [:div.list-group
       [selection-color cur-val :blue "Blue"]
       [selection-color cur-val :green "Green"]
       [selection-color cur-val :red "Red"]]]))

(defn panel
  "A reagant component. Acts as control and input panel for the app."
  []
  (let [input-text (rf/subscribe [:control/query-string])
        label-info (rf/subscribe [:table/rows-label-info])]
    [:div#toolbar
     [:div#search-section
       [:textarea#search-input {:on-change #(rf/dispatch [:control/set-query-string (-> % .-target .-value)])
                                ;; This submits the query when enter is pressed, but allows the user
                                ;; to enter a linebreak in the textarea with shift-enter.
                                :on-key-press (fn [e] (if (and (= (.-key e) "Enter") (not (.-shiftKey e)))
                                                        (do
                                                          (.preventDefault e)
                                                          (rf/dispatch [:query/parse-query @input-text @label-info]))))
                                :placeholder "Enter a query..."
                                ;; This random attribute value for autoComplete is needed to turn
                                ;; autoComplete off in Chrome. "off" and "false" do not work.
                                :autoComplete "my-search-field"
                                ;; Disables text correction on iOS Safari.
                                :autocorrect "off"
                                :autocapitalize "none"
                                ;; HTML5 attr, browser support limited.
                                :spellcheck "false"
                                :value @input-text}]
       [:div#search-buttons
         [:button.toolbar-button.pure-button
          {:on-click #(rf/dispatch [:query/parse-query @input-text @label-info])} "Run InferenceQL"]
         [:button.toolbar-button.pure-button
          {:on-click #(rf/dispatch [:table/clear])} "Clear results"]]]
     [:div.flex-box-space-filler]
     [selection-color-selector]]))
