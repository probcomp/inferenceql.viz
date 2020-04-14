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

(defn slider [min max step]
  (let [cur-val @(rf/subscribe [:viz/timestep])]
    [:div.slider
      [:span "Timestep: "]
      [:input {:type :range :name :timestep
               :min min :max max :step step
               :value cur-val
               :on-change (fn [e]
                            (let [new-val (js/parseFloat (-> e .-target .-value))]
                              (rf/dispatch [:viz/set-timestep new-val])))}]
      [:label cur-val]]))

(defn panel
  "A reagant component. Acts as control and input panel for the app."
  []
  (let [timestep (rf/subscribe [:viz/timestep])]
    [:div#toolbar
     [:div#search-section
       [slider 0 250 1]
       [:div#search-buttons
         [:button.toolbar-button.pure-button
          {:on-click (fn [e]
                       (rf/dispatch [:viz/run])
                       (.blur (.-target e)))} ; Clear focus off of button after click.
          "Run"]
         [:button.toolbar-button.pure-button
          {:on-click (fn [e]
                       (rf/dispatch [:viz/stop])
                       (.blur (.-target e)))} ; Clear focus off of button after click.
          "Stop"]
         [:button.toolbar-button.pure-button
          {:on-click (fn [e]
                        (rf/dispatch [:viz/clear])
                        (.blur (.-target e)))} ; Clear focus off of button after click.
          "Clear"]]]]))
