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

(defn confidence-slider []
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

(defn confidence-mode []
  (let [template [:div#conf-mode
                  [:label "Mode:"]
                  [:br]
                  [:select.form-control {:field :list :id :confidence-mode}
                   [:option {:key :none} "none"]
                   [:option {:key :row} "row-wise"]
                   [:option {:key :cells-existing} "cell-wise (existing)"]
                   [:option {:key :cells-missing} "cell-wise (missing)"]]]]
    [forms/bind-fields template reagent-forms-function-map]))

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
          ;; This button performs a no-op currently.
          {:on-click #(do)} "Delete virtual data"]]]
     [:div.flex-box-space-filler]
     [:div#conf-controls
      [confidence-slider]
      [confidence-mode]]]))
