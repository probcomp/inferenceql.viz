(ns inferenceql.viz.panels.jsmodel.views
  (:require [re-com.core :refer [border v-box title button gap]]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [clojure.string :refer [replace]]
            [goog.string :refer [format]]
            ["highlight.js/lib/core" :as yarn-hljs]
            ["highlight.js/lib/languages/javascript" :as yarn-hljs-js]))

;; We are using the minimal version of highlight.js where
;; every language used has to be registered individually.
(.registerLanguage yarn-hljs "javascript" yarn-hljs-js)


(defn add-cluster-spans [highlighted-js-text]
  (let [form-1 "<span class=\"hljs-keyword\">if</span> (cluster_id == <span class=\"hljs-number\">%s</span>) {"
        form-2 "} <span class=\"hljs-keyword\">else</span> <span class=\"hljs-keyword\">if</span> (cluster_id == <span class=\"hljs-number\">%s</span>) {"
        form-1-rep (fn [id] (str (format "<span class=\"cluster\" data=\"%s\">" id)
                                 (format form-1 id)
                                 "</span>"))
        form-2-rep (fn [id] (str (format "<span class=\"cluster\" data=\"%s\">" id)
                                 (format form-2 id)
                                 "</span>"))]
    (-> highlighted-js-text
        (replace form))
    highlighted-js-text))

(defn js-code-block
  "Display of Javascript code with syntax highlighting.

  Args:
    `code` -- (string) The Javascript source code to display.

  Returns: A reagent component."
  [js-code]
  (let [dom-nodes (r/atom {})
        highlight (fn [js-text]
                    (.-value (.highlight yarn-hljs js-text #js {"language" "js"})))]
    (r/create-class
     {:display-name "js-model-code"

      :component-did-mount
      (fn [this]
        (.addEventListener
         (:code-elem @dom-nodes)
         "click"
         (fn [e] (.log js/console :here (.-target e)))))

      :reagent-render
      (fn [js-code]
        (let [tagged-code (-> js-code highlight add-cluster-spans)]
          [:pre#program-display
           [:code {:class "js"
                   :ref #(swap! dom-nodes assoc :code-elem %)
                   :dangerouslySetInnerHTML {:__html tagged-code}}]]))})))

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
