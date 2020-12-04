(ns inferenceql.spreadsheets.panels.override.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf]))

(defn- code-editor
  "Reagent component for entering a javascript function as text."
  [an-atom]
  [:textarea.code-editor
   {:value        @an-atom
    :on-change    #(reset! an-atom (-> % .-target .-value))}])

(defn js-function-entry-modal
  "Reagent component that provides editing and setting of a js-function.
  It is intended to be set as the contents of the modal"
  [col-name fn-text]
  (let [source-text (r/atom (or fn-text ""))
        ;; This gets called below when the set button is clicked.
        set-fn (fn [event]
                 (rf/dispatch [:override/set-column-function col-name @source-text])
                 (rf/dispatch [:override/clear-modal]))]
    (fn []
      [:div.js-function-entry-modal
       [code-editor source-text]
       [:div.modal-footer
        [:button {:type "button"
                  :title "Cancel"
                  :class "btn btn-default"
                  :on-click #(rf/dispatch [:override/clear-modal])}
                 "Cancel"]
        [:button {:type "button"
                  :title "Set"
                  :class "btn btn-default"
                  :on-click set-fn}
                 "Set"]]])))
