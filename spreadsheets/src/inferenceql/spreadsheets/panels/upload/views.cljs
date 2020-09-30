(ns inferenceql.spreadsheets.panels.upload.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [re-com.core :refer [border title v-box p h-box line button gap input-text modal-panel
                                 checkbox horizontal-bar-tabs horizontal-tabs]]))

(defn magic-url-form []
  (let [form-data (r/atom {:url ""
                           :use-creds false})]
    (fn []
      [:<>
       [v-box
        :class    "form-group"
        :children [[p {:style {:color "#777"}}
                    "Submit a url which will be used to pull all needed files for loading a particular demo."]
                   [gap :size "50px"]
                   [title :label "Magic Url" :level :level3]
                   [input-text
                    :model       (:url @form-data)
                    :on-change   #(swap! form-data assoc :url %)
                    :class       "form-control"
                    :width       "500px"
                    :placeholder "http:// ..."
                    :attr        {:spell-check "false"}]
                   [gap :size "30px"]
                   [checkbox :label "Use with credentials?"
                             :model (:use-creds @form-data)
                             :on-change #(swap! form-data assoc :use-creds %)]
                   [gap :size "5px"]
                   [p {:style {:color "#777"
                               :padding-left "21px"}}
                    "Check this if your url is password protected. "]]]

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

(defn file-info [file]
  (when file
    (let [type (.-type file)
          type-string (if (= type "") "unknown" type)
          date-string (.toLocaleString (.-lastModifiedDate file))]
      [v-box
       :gap "2px"
       :margin "5px 8px 0px 8px"
       :children [[:div (str "Size: " (.-size file) " bytes")]
                  [:div (str "Last modified: " date-string)]]])))

(defn local-file-form []
  (let [form-data (r/atom {:dataset-name "data"
                           :dataset-file nil
                           :dataset-schema-file nil
                           :model-name "model"
                           :model-file nil})]
    (fn []
      [:<>
       [p {:style {:color "#777"}}
        "Select local files to upload into the app."]
       [gap :size "50px"]
       [v-box
        :class    "form-group"
        :children [[gap :size "5px"]
                   [title :label "Dataset (.csv)" :level :level3]
                   [:input {:type "file" :multiple false :accept ".csv"
                            :on-change #(let [^js/File file (-> % .-target .-files (aget 0))]
                                          (swap! form-data assoc :dataset-file file))}]
                   [file-info (:dataset-file @form-data)]
                   [gap :size "5px"]
                   [title :label "Schema (.edn)" :level :level3]
                   [:input {:type "file" :multiple false :accept ".edn"
                            :on-change #(let [^js/File file (-> % .-target .-files (aget 0))]
                                          (swap! form-data assoc :dataset-schema-file file))}]
                   [file-info (:dataset-schema-file @form-data)]]]
       [gap :size "30px"]
       [v-box
        :class    "form-group"
        :children [[gap :size "5px"]
                   [title :label "Model (.edn or .json)" :level :level3]
                   [:input {:type "file" :multiple false :accept ".edn,.json"
                            :on-change #(let [^js/File file (-> % .-target .-files (aget 0))]
                                          (swap! form-data assoc :model-file file))}]
                   (when-let [file (:model-file @form-data)]
                    [file-info file])]]
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
  (let [tab-options [{:id ::magic-url :label "Magic url"}
                     {:id ::local-file :label "Local files"}]
        selected-tab-id (r/atom ::magic-url)
        update-selected-tab #(reset! selected-tab-id %)]
    (fn []
      [border
       :border "1px solid #eee"
       :child  [v-box
                :min-height "650px"
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
                           [gap :size "10px"]
                           (case @selected-tab-id
                             ::magic-url [magic-url-form]
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