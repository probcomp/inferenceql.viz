(ns inferenceql.spreadsheets.panels.control.views
  (:require [re-frame.core :as rf]
            [reagent-forms.core :as forms]))

(defn confidence-slider []
  (let [cur-val @(rf/subscribe [:confidence-threshold])]
    [:div#conf-slider
      [:span "Confidence Threshold: "]
      [:br]
      [:input {:type :range :name :confidence-threshold
               :min 0 :max 1 :step 0.01
                       :value cur-val
                       :on-change (fn [e]
                                    (let [new-val (js/parseFloat (-> e .-target .-value))]
                                      (rf/dispatch [:set-confidence-threshold new-val])))}]
      [:label cur-val]]))

(defn confidence-mode []
  (let [template [:div#conf-mode
                  [:label "Mode:"]
                  [:br]
                  [:select.form-control {:field :list :id :mode}
                   [:option {:key :none} "none"]
                   [:option {:key :row} "row-wise"]
                   [:option {:key :cells-existing} "cell-wise (existing)"]
                   [:option {:key :cells-missing} "cell-wise (missing)"]]]

        ;; Function map that allows `template` reagent-forms template to
        ;; communicate with the reframe db.
        events {:get (fn [path] @(rf/subscribe [:confidence-option path]))
                :save! (fn [path value] (rf/dispatch [:set-confidence-options path value]))
                :update! (fn [path save-fn value]
                           ;; save-fn should accept two arguments: old-value, new-value
                           (rf/dispatch [:update-confidence-options save-fn path value]))
                :doc (fn [] @(rf/subscribe [:confidence-options]))}]
    [forms/bind-fields template events]))

(defn panel
  "A reagant component. Acts as control and input panel for the app."
  []
  (let [input-text (rf/subscribe [:query-string])]
    (fn []
      [:div#toolbar
       [:div#search-section
         [:input#search-input {:type "search"
                               :on-change #(rf/dispatch [:set-query-string (-> % .-target .-value)])
                               :on-key-press (fn [e] (if (= (.-key e) "Enter")
                                                       (rf/dispatch [:parse-query @input-text])))
                               :placeholder "Enter a query..."
                               ;; This random attribute value for autoComplete is needed to turn
                               ;; autoComplete off in Chrome. "off" and "false" do not work.
                               :autoComplete "my-search-field"
                               :value @input-text}]
         [:div#search-buttons
           [:button.toolbar-button.pure-button {:on-click #(rf/dispatch [:parse-query @input-text])} "Run InferenceQL"]
           [:button.toolbar-button.pure-button {:on-click #(rf/dispatch [:clear-virtual-data])} "Delete virtual data"]]]
       [:div.flex-box-space-filler]
       [:div#conf-controls
        [confidence-slider]
        [confidence-mode]]])))
