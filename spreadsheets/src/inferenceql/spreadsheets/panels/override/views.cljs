;;;; Taken from this project on github and then modified
;;;; https://github.com/benhowell/re-frame-modal

(ns inferenceql.spreadsheets.panels.override.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf]))

(defn- modal-panel
  "Reagent component that renders a modal backdrop and the modal contents.
  Clicking the backdrop closes the modal. Modal contents gets sized according to
  `size` setting."
  [{:keys [child size]}]
  [:div.modal-wrapper
   [:div {:class "modal-backdrop"
          :on-click (fn [event]
                      (rf/dispatch [:override/clear-modal])
                      (.preventDefault event)
                      (.stopPropagation event))}]
   [:div {:class "modal-child"
          :style {:width (case size
                           :extra-small "15%"
                           :small "30%"
                           :large "70%"
                           :extra-large "85%"
                           "50%")}}
    child]])

(defn modal
  "Reagent component for rendering modal contents."
  []
  (let [modal (rf/subscribe [:override/modal])]
    (fn []
      [:div
       (when (:child @modal)
         [modal-panel @modal])])))

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