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


(defn slider []
  (let [cur-val @(rf/subscribe [:control/confidence-threshold])]
    [:div#conf-slider
      [:span "Confidence Threshold: "]
      [:br]
      [:input {:type :range :name :confidence-threshold
               :min 0 :max 1 :step 0.01
                       :value cur-val
                       :on-change (fn [e]
                                    (let [new-val (js/parseFloat (-> e .-target .-value))]
                                      (rf/dispatch [:control/set-confidence-threshold new-val])))}]
      [:label cur-val]]))

(defn rna-seq-values []
  [:div#rna-seq-values
   [slider]])

(defn experimental-conditions []
  (let [template [:div
                  [:label "Arabinose:"]
                  [:select.form-control {:field :list :id :arabinose}
                   [:option {:key :low} "0.0"]
                   [:option {:key :high} "0.012500225"]]
                  [:br]
                  [:label "Iptg:"]
                  [:select.form-control {:field :list :id :iptg}
                   [:option {:key :high} "7.98e-05"]
                   [:option {:key :low} "0.0"]]
                  [:br]
                  [:label "Timepoint:"]
                  [:select.form-control {:field :list :id :timepoint}
                   [:option {:key :18h} "18.0"]
                   [:option {:key :5h} "5.0"]]]]
    [:div#experimental-conditions
     [forms/bind-fields template reagent-forms-function-map]]))

(defn panel
  "A reagant component. Acts as control and input panel for the app."
  []
  (let [input-text (rf/subscribe [:control/query-string])
        label-info (rf/subscribe [:table/rows-label-info])]
    [:div#toolbar
     [:div#search-section
       [:input#search-input {:type "search"
                             :on-change #(rf/dispatch [:control/set-query-string (-> % .-target .-value)])
                             :on-key-press (fn [e] (if (= (.-key e) "Enter")
                                                     (rf/dispatch [:query/parse-query @input-text @label-info])))

                             :placeholder "Enter a query..."
                             ;; This random attribute value for autoComplete is needed to turn
                             ;; autoComplete off in Chrome. "off" and "false" do not work.
                             :autoComplete "my-search-field"
                             :value @input-text}]
       [:div#search-buttons
         [:button.toolbar-button.pure-button
          {:on-click #(rf/dispatch [:query/parse-query @input-text @label-info])} "Run InferenceQL"]
         [:button.toolbar-button.pure-button
          {:on-click #(rf/dispatch [:table/clear])} "Clear results"]]]
     [experimental-conditions]
     [rna-seq-values]
     [:div.flex-box-space-filler]
     [selection-color-selector]]))
