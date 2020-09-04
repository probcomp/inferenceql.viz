(ns inferenceql.spreadsheets.panels.upload.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [re-com.core :refer [border title v-box h-box line button gap input-text modal-panel
                                 horizontal-bar-tabs
                                 horizontal-tabs]]))

(defn web-url-form []
  (let [form-data (r/atom {:web-url ""})]
    (fn []
      [:<>
       [v-box
        :class    "form-group"
        :children [[title :label "url" :level :level4]
                   [input-text
                    :model       (:web-url @form-data)
                    :on-change   #(swap! form-data assoc :web-url %)
                    :class       "form-control"
                    :width       "500px"
                    :placeholder "http:// ..."
                    :attr        {:id "dataset-name-input"
                                  :auto-complete "dummy-value"
                                  :spell-check "false"}]]]
       [gap :size "30px"]
       [line :color "#ddd" :style {:margin "10px 0px 0px"}]
       [gap :size "30px"]
       [h-box
        :gap      "12px"
        :children [[button
                    :label "Submit"
                    :class "btn-primary"
                    :on-click #(rf/dispatch [:upload/read-web-url (:web-url @form-data)])]
                   [button
                    :label "Cancel"
                    :on-click #(rf/dispatch [:upload/set-display false])]]]])))

(defn local-file-form []
  (let [form-data (r/atom {:dataset-name "data"
                           :dataset-file nil
                           :dataset-schema-file nil
                           :model-name "model"
                           :model-file nil})]
    (fn []
      [:<>
       [v-box
        :class    "form-group"
        :children [[title :label "New dataset" :level :level3 :margin-bottom "1px"]
                   [title :label "Name – not editable" :level :level4]
                   [input-text
                    :model       (:dataset-name @form-data)
                    :on-change   #(swap! form-data assoc :dataset-name %)
                    :class       "form-control"
                    :disabled?   true
                    :attr        {:id "dataset-name-input"
                                  :auto-complete "dummy-value"
                                  :spell-check "false"}]
                   [gap :size "5px"]
                   [title :label "Dataset (.csv)" :level :level4]
                   [:input {:type "file" :multiple false :accept ".csv"
                            :on-change #(let [^js/File file (-> % .-target .-files (aget 0))]
                                          (swap! form-data assoc :dataset-file file))}]
                   [gap :size "5px"]
                   [title :label "Schema (.edn)" :level :level4]
                   [:input {:type "file" :multiple false :accept ".edn"
                            :on-change #(let [^js/File file (-> % .-target .-files (aget 0))]
                                          (swap! form-data assoc :dataset-schema-file file))}]]]
       [gap :size "30px"]
       [v-box
        :class    "form-group"
        :children [[title :label "New model" :level :level3 :margin-bottom "1px"]
                   [title :label "Name - not editable" :level :level4]
                   [input-text
                    :model       (:model-name @form-data)
                    :on-change   #(swap! form-data assoc :model-name %)
                    :class       "form-control"
                    :disabled?   true
                    :attr        {:id "model-name-input"
                                  :auto-complete "dummy-value"
                                  :spell-check "false"}]
                   [gap :size "5px"]
                   [title :label "Model (.edn)" :level :level4]
                   [:input {:type "file" :multiple false :accept ".edn"
                            :on-change #(let [^js/File file (-> % .-target .-files (aget 0))]
                                          (swap! form-data assoc :model-file file))}]]]
       [gap :size "30px"]
       [line :color "#ddd" :style {:margin "10px 0px 0px"}]
       [gap :size "30px"]
       [h-box
        :gap      "12px"
        :children [[button
                    :label "Submit"
                    :class "btn-primary"
                    :on-click #(rf/dispatch [:upload/read-schema-file @form-data])]
                   [button
                    :label "Cancel"
                    :on-click #(rf/dispatch [:upload/set-display false])]]]])))

(defn panel-content []
  (let [tab-options [{:id ::web-url :label "Web url"}
                     {:id ::local-file :label "Local files"}]
        selected-tab-id (r/atom ::web-url)
        update-selected-tab #(reset! selected-tab-id %)]
    (fn []
      [border
       :border "1px solid #eee"
       :child  [v-box
                :min-height "777px"
                :min-width "800px"
                :padding "10px 30px 30px 30px"
                :style    {:background-color "cornsilk"}
                :children [[title :label "Change dataset and model" :level :level1]
                           [gap :size "20px"]
                           [v-box
                            :children [[title :label "Method" :level :level3]
                                       [horizontal-bar-tabs :tabs tab-options
                                        :model selected-tab-id
                                        :on-change update-selected-tab]]]
                           [gap :size "70px"]
                           (case @selected-tab-id
                             ::web-url [web-url-form]
                             ::local-file [local-file-form])]]])))

(defn panel
  "A reagant component for uploading new models and datasets."
  []
  (let [show @(rf/subscribe [:upload/display])]
    [:div#upload-panel {:style {:display (if show "block" "none")}}
     [modal-panel
      :backdrop-color   "grey"
      :backdrop-opacity 0.6
      :child [panel-content]
      :wrap-nicely? false
      :backdrop-on-click #(rf/dispatch [:upload/set-display false])]]))