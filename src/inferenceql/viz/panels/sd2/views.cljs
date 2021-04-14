(ns inferenceql.viz.panels.sd2.views
  (:require [reagent.core :as r]
            [inferenceql.inference.gpm.view :as view]
            [inferenceql.inference.gpm.column :as column]
            [inferenceql.inference.gpm.primitive-gpms :as pgpms]
            [clojure.pprint :refer [pprint]]
            [re-frame.core :as rf]
            [medley.core :as medley]
            [goog.string :refer [format]]
            [re-com.core :refer [border title v-box p h-box line button gap input-text
                                 checkbox horizontal-bar-tabs horizontal-tabs
                                 radio-button]]
            [clojure.string :as string]
            [goog.string :refer [format]]
            [cljsjs.highlight]
            [cljsjs.highlight.langs.javascript]
            [cljstache.core :refer [render]]
            [inferenceql.viz.config :refer [config]]))

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

(defn cats-string [col-params]
  (let [kv-strings (for [[cat-val cat-weight] col-params]
                     (format "\"%s\": %.3f" cat-val cat-weight))]
    (string/join ", " kv-strings)))

(defn template-data [stat-types params]
  (let [last-index (-> params count dec)
        columns (reduce
                 (fn [acc [index [col-name col-params]]]
                   (conj acc (case (get stat-types col-name)
                               :categorical
                               {:col-name (name col-name) :categorical true
                                :cats-string (cats-string col-params)
                                :last (= index last-index)}
                               :gaussian
                               {:col-name (name col-name) :gaussian true
                                :mu (format "%.3f" (:mu col-params))
                                :sigma (format "%.3f" (:sigma col-params))
                                :last (= index last-index)})))
                 []
                 (map-indexed vector params))]
    {:columns columns}))

(defn js-fn-text [stat-types params]
  (let [template-data (template-data stat-types params)
        rendered (render (:cluster-fn-template config) template-data)]
    (->> rendered
         (string/split-lines)
         (remove string/blank?)
         (string/join "\n"))))

(defn js-code-block
  [js-code]
  (let [dom-nodes (r/atom {})]
    (r/create-class
     {:display-name "js-model-code"

      :component-did-mount
      (fn [this]
        (.highlightBlock js/hljs (:program-display @dom-nodes)))

      :reagent-render
      (fn []
        [:pre.program-display {:ref #(swap! dom-nodes assoc :program-display %)}
         [:code {:class "js"}
          js-code]])})))


(defn xcat-category [view cat-name]
  (let [stat-types (medley/map-vals :stattype (:columns view))
        params (reduce-kv
                (fn [acc col-name col-gpm]
                  (let [;; If there is no category for a given column, this means
                        ;; that there is no associated data with that column in the rows within
                        ;; that category. Because the types are collapsed, we can generate
                        ;; a new (empty) category for that column.
                        col-cat (get-in col-gpm [:categories cat-name] (column/generate-category col-gpm))
                        exported-cat (pgpms/export-category (get stat-types col-name) col-cat)]
                    (merge acc exported-cat)))
                {}
                (:columns view))]
    [:div
     [:h3 cat-name]
     [js-code-block (js-fn-text stat-types params)]]))

(defn scale [weights]
  (let [weights (map second weights)
        min-w (apply min weights)
        max-w (apply max weights)

        lin (.scale js/vega "linear")
        ;; TODO: try with different color scale.
        scheme (.scheme js/vega "blues")
        scale-fn (doto (lin)
                       (.domain [min-w max-w])
                       (.range [0 1]))]
    (fn [weight]
      (scheme (scale-fn weight)))))


(defn xcat-view [view-id view constraints]
  (let [columns (-> view :columns keys)
        columns (map name columns)
        weights (->> (view/category-weights view constraints)
                     (medley/map-vals Math/exp)
                     (sort-by first))
        scale (scale weights)]
    [:div {:style {:width "750px"}}
      [:h2 view-id]
      [:h4 "columns: " (string/join ", " columns)]
      [:div.cats
        (for [[cat-name weight] weights]
          [:div.cat-group {:style {:border-color (scale weight)}}
            [:div.cat-name (str (name cat-name) ":")]
            [:div.cat-weight (format "%.3f" weight)]])]
      (for [[cat-name _] weights]
        [xcat-category view cat-name])]))

(defn view [model constraints]
  [:div
    [:h1 "xcat-model"]
    (for [[view-id view] (:views model)]
      [xcat-view view-id view constraints])])
