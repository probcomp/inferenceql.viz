(ns inferenceql.viz.panels.jsmodel.views
  (:require [re-com.core :refer [border v-box title button gap]]
            [reagent.core :as r]
            [re-frame.core :as rf]
            ["highlight.js/lib/core" :as yarn-hljs]
            ["highlight.js/lib/languages/javascript" :as yarn-hljs-js]))

;; We are using the minimal version of highlight.js where
;; every language used has to be registered individually.
(.registerLanguage yarn-hljs "javascript" yarn-hljs-js)

(defn js-code-block
  "Display of Javascript code with syntax highlighting.

  Args:
    `code` -- (string) The Javascript source code to display.

  Returns: A reagent component."
  [js-code]
  (let [dom-nodes (r/atom {})]
    (r/create-class
     {:display-name "js-model-code"

      ;;:component-did-mount
      #_(fn [this]
          (.highlightElement yarn-hljs (:program-display @dom-nodes)))

      ;;:component-did-update
      #_(fn [this]
          (.highlightElement yarn-hljs (:program-display @dom-nodes)))

      :reagent-render
      (fn [js-code]
        [:pre#program-display {:ref #(swap! dom-nodes assoc :program-display %)}
         [:code {:class "js"}
          js-code]])})))

(defn display
  "Display of js-model source code with syntax highlighting.

  Intended to be set as the contents of a modal dialog.
  Returns: A reagent component. "
  []
  (let [source-code @(rf/subscribe [:jsmodel/source-code])]
    [border
     :border "1px solid #eee"
     :child  [v-box
              :min-height "777px"
              :min-width "800px"
              :padding "10px 30px 30px 30px"
              :style    {:background-color "cornsilk"}
              :children [[title :label "Javascript model export" :level :level1]
                         [gap :size "10px"]
                         [js-code-block source-code]]]]))
