(ns inferenceql.viz.observable.components.control
  (:require [re-com.core :refer [h-box throbber]]
            [reagent.core :as r]
            [cljs.core.async :refer [go]]
            [cljs.core.async.interop :refer-macros [<p!]]))

(defn panel
  "A reagant component. Acts as control and input panel for the app."
  [query query-fn update-results update-failure]
  (let [input-text (r/atom query)
        running (r/atom false)
        run-query #(go
                    (reset! running true)
                    (try
                      (let [r (<p! (query-fn @input-text))]
                        (update-results r))
                      (catch js/Error err
                        (update-failure (with-out-str (print (.-message (ex-cause err))))))
                      (finally (reset! running false))))]
    (fn []
      [:div#toolbar
       [:div#search-section
         [:textarea#search-input {:on-change #((do
                                                 (reset! input-text (-> % .-target .-value))))
                                  ;; This submits the query when enter is pressed, but allows the user
                                  ;; to enter a linebreak in the textarea with shift-enter.
                                  :on-key-press (fn [e] (if (and (= (.-key e) "Enter") (.-shiftKey e))
                                                          (do
                                                            (run-query)
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
          :justify :start
          :children [[:button.toolbar-button.pure-button
                      {:on-click (fn [e]
                                   (run-query)
                                   (.blur (.-target e)))}
                      "Run query"]
                     [:button.toolbar-button.pure-button
                      {:on-click (fn [e]
                                    (update-results nil)
                                    (.blur (.-target e)))}
                      "Clear results"]
                     (when @running
                       [:div {:style {:padding-top "4px" :padding-left "10px" :height "22px"}}
                         [:img {:src "https://cdnjs.cloudflare.com/ajax/libs/galleriffic/2.0.1/css/loader.gif"
                                :height "22px"}]])]]]])))
