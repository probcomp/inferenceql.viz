(ns inferenceql.spreadsheets.panels.upload.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [re-com.core :refer [border title v-box p h-box line button gap input-text
                                 checkbox horizontal-bar-tabs horizontal-tabs]]))

(defn file-info
  "Displays basic information about a file.

  Args:
    `file` - A Javascript File object.

  Returns: A reagent component."
  [file]
  (when file
    (let [date-string (.toLocaleString (.-lastModifiedDate file))]
      [v-box
       :gap "2px"
       :margin "5px 8px 0px 8px"
       :children [[:div (str "Size: " (.-size file) " bytes")]
                  [:div (str "Last modified: " date-string)]]])))

(defn local-file-form
  "A form for selecting files for changing the dataset, schema, and model.

  The new files selected replace the default :data dataset and :model model.

  Returns: A reagent component."
  []
  (let [form-data (r/atom {:dataset-name "data"
                           :dataset-file nil
                           :dataset-schema-file nil
                           :model-name "model"
                           :model-file nil})]
    (fn []
      [v-box
       :min-height "501px"
       :children [[p {:style {:color "#777"}}
                   "Select local files to upload into the app."]
                  [gap :size "30px"]
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
                              [file-info (:model-file @form-data)]]]
                  [gap :size "1"]
                  [line :color "#ddd" :style {:margin "10px 0px 0px"}]
                  [gap :size "30px"]
                  [h-box
                   :gap      "12px"
                   :children [[button
                               :label "Submit"
                               :class "btn-primary"
                               :on-click #(do
                                            (rf/dispatch [:upload/read-files @form-data])
                                            (rf/dispatch [:modal/clear])
                                            (rf/dispatch [:table/clear]))]
                              [button
                               :label "Cancel"
                               :on-click #(rf/dispatch [:modal/clear])]]]]])))

(defn panel-contents
  "Hiccup for a panel that allows changing the app's dataset and model.

  This is intended to be set as the contents of a modal.

  Returns: A reagent component."
  []
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
                         [local-file-form]]]]))
