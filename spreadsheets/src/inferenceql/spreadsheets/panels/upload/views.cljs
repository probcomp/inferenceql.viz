(ns inferenceql.spreadsheets.panels.upload.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [re-com.core :refer [border title v-box p h-box line button gap input-text modal-panel
                                 horizontal-bar-tabs
                                 horizontal-tabs]]))

(defn url-form []
  (let [form-data (r/atom {:url ""
                           :username nil
                           :password nil})]
    (fn []
      [:<>
       [v-box
        :class    "form-group"
        :children [[title :label "Url" :level :level3]
                   [p {:style {:color "#777"}}
                    "This will pull all needed files from the folder specified by the url. "]
                   [input-text
                    :model       (:url @form-data)
                    :on-change   #(swap! form-data assoc :url %)
                    :class       "form-control"
                    :width       "500px"
                    :placeholder "http:// ..."
                    :attr        {:spell-check "false"}]
                   [gap :size "30px"]
                   [p {:style {:color "#777"}}
                    "Add credentials if your url is password protected."]
                   [title :label "Username" :level :level4]
                   [input-text
                    :model       (:username @form-data)
                    :on-change   #(swap! form-data assoc :username %)
                    :class       "form-control"
                    :width       "150px"
                    :height      "30px"
                    :attr        {:spell-check "false"}]

                   [title :label "Password" :level :level4]
                   [input-text
                    :model       (:password @form-data)
                    :on-change   #(swap! form-data assoc :password %)
                    :class       "form-control"
                    :width       "150px"
                    :height      "30px"
                    :attr        {:spell-check "false"}]]]
       [gap :size "50px"]
       [line :color "#ddd" :style {:margin "0px 0px 0px"}]
       [gap :size "30px"]
       [h-box
        :gap      "12px"
        :children [[button
                    :label "Submit"
                    :class "btn-primary"
                    :on-click #(rf/dispatch [:upload/read-url @form-data])]
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
        :children [[title :label "Dataset-related" :level :level3 :margin-bottom "1px"]
                   [title :label "Name – not editable" :level :level4]
                   [input-text
                    :model       (:dataset-name @form-data)
                    :on-change   #(swap! form-data assoc :dataset-name %)
                    :class       "form-control"
                    :disabled?   true
                    :height      "25px"
                    :width       "190px"
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
        :children [[title :label "Model-related" :level :level3 :margin-bottom "1px"]
                   [title :label "Name - not editable" :level :level4]
                   [input-text
                    :model       (:model-name @form-data)
                    :on-change   #(swap! form-data assoc :model-name %)
                    :class       "form-control"
                    :disabled?   true
                    :height      "25px"
                    :width       "190px"
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
                    :on-click #(rf/dispatch [:upload/read-files @form-data])]
                   [button
                    :label "Cancel"
                    :on-click #(rf/dispatch [:upload/set-display false])]]]])))

(defn panel-content []
  (let [tab-options [{:id ::url :label "Web url"}
                     {:id ::local-file :label "Local files"}]
        selected-tab-id (r/atom ::url)
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
                           ;;[p "This will ...."]
                           [gap :size "70px"]
                           (case @selected-tab-id
                             ::url [url-form]
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