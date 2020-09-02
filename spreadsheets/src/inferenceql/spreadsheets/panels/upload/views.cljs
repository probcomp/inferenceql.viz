(ns inferenceql.spreadsheets.panels.upload.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [re-com.core :refer [border title v-box h-box line button input-text modal-panel]]))

(defn panel-content []
  (let [form-data (r/atom {:dataset-name "data"
                           :dataset-file nil
                           :dataset-schema-file nil
                           :model-name "model"
                           :model-file nil})]
    (fn []
      [border
       :border "1px solid #eee"
       :child  [v-box
                :min-height "500px"
                :min-width "800px"
                :padding "30px"
                :gap "30px"
                :style    {:background-color "cornsilk"}
                :children [[title :label "Add a dataset and model" :level :level1]
                           [v-box
                            :class    "form-group"
                            :children [[title :label "New dataset" :level :level2]
                                       [title :label "Name" :level :level4]
                                       [input-text
                                        :model       (:dataset-name @form-data)
                                        :on-change   #(swap! form-data assoc :dataset-name %)
                                        :class       "form-control"
                                        :disabled?   true
                                        :attr        {:id "dataset-name-input"
                                                      :autoComplete "my-search-field"
                                                      :autoCorrect "off"
                                                      :autoCapitalize "none"
                                                      :spellCheck "false"}]
                                       [title :label "Dataset (.csv)" :level :level4]
                                       [:input {:type "file" :multiple false :accept ".csv"
                                                :on-change #(let [^js/File file (-> % .-target .-files (aget 0))]
                                                              (swap! form-data assoc :dataset-file file))}]
                                       [title :label "Schema (.edn)" :level :level4]
                                       [:input {:type "file" :multiple false :accept ".edn"
                                                :on-change #(let [^js/File file (-> % .-target .-files (aget 0))]
                                                              (swap! form-data assoc :dataset-schema-file file))}]]]
                           [v-box
                            :class    "form-group"
                            :children [[title :label "New model" :level :level2]
                                       [title :label "Name" :level :level4]
                                       [input-text
                                        :model       (:model-name @form-data)
                                        :on-change   #(swap! form-data assoc :model-name %)
                                        :class       "form-control"
                                        :disabled?   true
                                        :attr        {:id "model-name-input"
                                                      :autoComplete "my-search-field"
                                                      :autoCorrect "off"
                                                      :autoCapitalize "none"
                                                      :spellCheck "false"}]
                                       [title :label "Model (.edn)" :level :level4]
                                       [:input {:type "file" :multiple false :accept ".edn"
                                                :on-change #(let [^js/File file (-> % .-target .-files (aget 0))]
                                                              (swap! form-data assoc :model-file file))}]]]
                           [line :color "#ddd" :style {:margin "10px 0 10px"}]
                           [h-box
                            :gap      "12px"
                            :children [[button
                                        :label "Submit"
                                        :class "btn-primary"
                                        :on-click #(rf/dispatch [:upload/read-schema-file @form-data])]
                                       [button
                                        :label "Cancel"
                                        :on-click #(rf/dispatch [:upload/set-display false])]]]]]])))

(defn panel
  "A reagant component for uploading new models and datasets."
  []
  (let [show @(rf/subscribe [:upload/display])]
    [:div#upload-panel {:style {:display (if show "block" "none")}}
     [modal-panel
      :backdrop-color   "grey"
      :backdrop-opacity 0.6
      :child [panel-content]
      :wrap-nicely? false]]))


