(ns inferenceql.viz.panels.upload.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [re-com.core :refer [border title v-box p h-box line button gap input-text
                                 checkbox horizontal-bar-tabs horizontal-tabs
                                 radio-button]]))

(defn file-info
  "Reagent component that displays basic information about a local file.

  Args:
    `file` - A Javascript File object."
  [file]
  (when file
    (let [date-string (.toLocaleString (.-lastModifiedDate file))]
      [v-box
       :gap "2px"
       :margin "5px 8px 0px 8px"
       :children [[:div (str "Size: " (.-size file) " bytes")]
                  [:div (str "Last modified: " date-string)]]])))

(defn input-type-selector
  "Reagent component that allows the user to select between file upload or url upload.

  Args:
    `model` - An atom that will be used to store the state of the selector."
  [model]
  [h-box
   :gap "10px"
   :margin "5px 0px 5px 0px"
   :children [[radio-button
               :label "file"
               :value :file
               :model @model
               :on-change #(reset! model %)]
              [radio-button
               :label "url"
               :value :url
               :model @model
               :on-change #(reset! model %)]]])

(defn local-file-input
  "Reagent component that allows the user to select a local file.

  Args:
    `model` - An atom that will be used to store specified file. Saves a tuple of
      [:file file-object].
    `accept` - The accept string to pass to the browser file input. When nil, file selector will
      allow all types of files."
  [model accept]
  (let [[type obj] @model
        model-to-display (when (= type :file) obj)]
    [v-box
     :children [[:input {:type "file" :multiple false :accept accept
                         :on-change #(let [^js/File file (-> % .-target .-files (aget 0))]
                                       (reset! model [:file file]))}]
                [file-info model-to-display]]]))

(defn url-input
  "Reagent component that allows the user specify a file via a url.

  Args:
    `model` - An atom that will be used to store the url. Saves a tuple of [:url url]."
  [model]
  (let [[type obj] @model
        model-to-display (when (= type :url) obj)]
    [input-text
     :model model-to-display
     :on-change #(reset! model [:url %])
     :class "form-control"
     :height "30px"
     :width "500px"
     :placeholder "http:// ..."
     :attr {:spell-check "false"}]))

(defn file-input
  "Reagent component that allows the user to specify a file locally or via a url.

  Args:
    `label` - The string to use as the label for this file input.
    `model` - An atom that will be used to store specified file. Saves a tuple of
      either [:file file-object] or [:url url].
    `options` - An options map."
  [label model options]
  (let [input-type (r/atom :file)]
    (fn []
      [v-box
       :children [[h-box
                   :gap "30px"
                   :children [[title :label label :level :level3]
                              [input-type-selector input-type]]]
                  (case @input-type
                    :file [local-file-input model (:accept options)]
                    :url [url-input model])]])))

(defn files-form
  "Reagent component form for specifying files for changing the dataset, schema, and model.

  The new files selected replace the default :data dataset and :model model."
  []
  (let [form-data (r/atom {:dataset nil
                           :schema nil
                           :model nil})]
    (fn []
      [v-box
       :min-height "501px"
       :children [[p {:style {:color "#777"}} "Select files to upload into the app."]
                  [gap :size "30px"]
                  [v-box
                   :class    "form-group"
                   :children [[gap :size "5px"]
                              [file-input "Dataset (.csv)" (r/cursor form-data [:dataset]) {:accept ".csv"}]
                              [gap :size "5px"]
                              [file-input "Schema (.edn)" (r/cursor form-data [:schema]) {:accept ".edn"}]]]
                  [gap :size "30px"]
                  [v-box
                   :class    "form-group"
                   :children [[gap :size "5px"]
                              [file-input "Model (.edn or .json)" (r/cursor form-data [:model]) {:accept ".edn,.json"}]]]
                  [gap :size "1"]
                  [line :color "#ddd" :style {:margin "10px 0px 0px"}]
                  [gap :size "30px"]
                  [h-box
                   :gap      "12px"
                   :children [[button
                               :label "Submit"
                               :class "btn-primary"
                               :on-click #(do
                                            (rf/dispatch [:upload/read-form @form-data])
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
                         [files-form]]]]))
