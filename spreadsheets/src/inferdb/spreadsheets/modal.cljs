;;;; Taken from this project on github
;;;; https://github.com/benhowell/re-frame-modal

(ns inferdb.spreadsheets.modal
  (:require
   [reagent.core :as r]
   [re-frame.core :refer [dispatch subscribe]]))


(defn modal-panel
  [{:keys [child size show?]}]
  [:div {:class "modal-wrapper"}
   [:div {:class "modal-backdrop"
          :on-click (fn [event]
                      (do
                        (dispatch [:modal {:show? (not show?)
                                           :child nil
                                           :size :default}])
                        (.preventDefault event)
                        (.stopPropagation event)))}]
   [:div {:class "modal-child"
          :style {:width (case size
                           :extra-small "15%"
                           :small "30%"
                           :large "70%"
                           :extra-large "85%"
                           "50%")}} child]])

(defn modal []
  (let [modal (subscribe [:modal])]
    (fn []
      [:div
       (if (:show? @modal)
         [modal-panel @modal])])))


(defn- close-modal []
  (dispatch [:modal {:show? false :child nil}]))

(defn code-editor [an-atom]
  [:textarea.code-editor
   {:value        @an-atom
    :on-change    #(reset! an-atom (-> % .-target .-value))}])

(defn function-entry [col-num]
  (let [source-text (r/atom "")
        set-fn (fn [event]
                 (dispatch [:set-column-function col-num @source-text])
                 (close-modal))]
    (fn []
      [:div
       {:style {:background-color "white"
                :padding          "16px"
                :border-radius    "6px"
                :text-align "center"}}
       [code-editor source-text]
       [:div {:class "modal-footer"}
        [:button {:type "button"
                  :title "Cancel"
                  :class "btn btn-default"
                  :on-click #(close-modal)}
                 "Cancel"]
        [:button {:type "button"
                  :title "Set"
                  :class "btn btn-default"
                  :on-click set-fn}
                 "Set"]]])))


(defn hello-bootstrap []
  [:div {:class "modal-content panel-danger"}
   [:div {:class "modal-header panel-heading"}
    [:button {:type "button" :title "Cancel"
              :class "close"
              :on-click #(close-modal)}
     [:i {:class "material-icons"} "close"]]
    [:h4 {:class "modal-title"} "Hello Bootstrap modal!"]]
   [:div {:class "modal-body"}
    [:div [:b (str "You can close me by clicking the Ok button, the X in the"
                 " top right corner, or by clicking on the backdrop.")]]]
   [:div {:class "modal-footer"}
    [:button {:type "button" :title "Ok"
              :class "btn btn-default"
              :on-click #(close-modal)} "Ok"]]])
