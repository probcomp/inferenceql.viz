(ns inferenceql.spreadsheets.panels.upload.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [re-com.core :refer [border title v-box h-box line button input-text modal-panel]]))

(defn panel-content []
  (let [form-data (r/atom {:dataset-name nil
                           :dataset-file nil
                           :model-name nil
                           :model-file nil})]

    (fn []
      [border
       :border "1px solid #eee"
       :child  [v-box
                :min-height "500px"
                :min-width "800px"
                :padding  "10px"
                :style    {:background-color "cornsilk"}
                :children [[title :label "Add a new dataset and model" :level :level2]
                           [v-box
                            :class    "form-group"
                            :children [[:label {:for "dataset-file-select"} "Dataset"]
                                       [input-text
                                        :model       (:dataset-name @form-data)
                                        :on-change   #(swap! form-data assoc :dataset-name %)
                                        :placeholder "Enter name for dataset"
                                        :class       "form-control"
                                        :attr        {:id "dataset-name-input"}]
                                       [:input {:type "file" :multiple false :accept ".csv"
                                                :on-change #(let [^js/File file (-> % .-target .-files (aget 0))]
                                                              (swap! form-data assoc :dataset-file file))}]]]
                           [v-box
                            :class    "form-group"
                            :children [[:label {:for "model-name"} "Model"]
                                       [input-text
                                        :model       (:model-name @form-data)
                                        :on-change   #(swap! form-data assoc :model-name %)
                                        :placeholder "Enter name for model"
                                        :class       "form-control"
                                        :attr        {:id "model-name-input"}]
                                       [:input {:type "file" :multiple false :accept ".edn"
                                                :on-change #(let [^js/File file (-> % .-target .-files (aget 0))]
                                                              (swap! form-data assoc :model-file file))}]]]
                           [line :color "#ddd" :style {:margin "10px 0 10px"}]
                           [h-box
                            :gap      "12px"
                            :children [[button
                                        :label "Submit"
                                        :class "btn-primary"
                                        :on-click #(rf/dispatch [:upload/read-files @form-data])]
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
      :child [panel-content]]]))


