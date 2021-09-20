(ns inferenceql.viz.panels.jsmodel.views
  (:require [re-com.core :refer [border v-box title button gap]]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [clojure.string :refer [replace]]
            [goog.string :refer [format]]
            ["highlight.js/lib/core" :as yarn-hljs]
            ["highlight.js/lib/languages/javascript" :as yarn-hljs-js]
            [hickory.core]
            [hickory.select :as s]
            [hickory.zip]
            [hickory.render]
            [clojure.zip :as zip]
            [clojure.string :as string]))

;; We are using the minimal version of highlight.js where
;; every language used has to be registered individually.
(.registerLanguage yarn-hljs "javascript" yarn-hljs-js)

(defn add-cluster-spans [highlighted-js-text]
  (let [highlighted-js-text (str "<code>" highlighted-js-text "</code>")

        p-zero (->> (hickory.core/parse-fragment highlighted-js-text)
                    (map hickory.core/as-hiccup)
                    (first))

        p-zero-tree (hickory.zip/hiccup-zip p-zero)


        #_pp #_(->> (hickory.core/parse-fragment highlighted-js-text)
                    (map hickory.core/as-hickory)
                    first)
        #_ppp #_(hickory.render/hickory-to-html pp)
        #_nodes #_(s/select (s/and (s/class "hljs-keyword")
                                   (s/tag :span)
                                   (s/find-in-text #"if"))
                            pp)

        nodes (loop [n p-zero-tree]
                (if (not (zip/end? n))
                  (do (.log js/console (zip/node n))
                      (recur (zip/next n)))))

        fix-node (fn [tree]
                   (let [node (zip/node tree)]
                     (cond
                       (= node [:span {:class "hljs-keyword"} "if"])
                       (zip/replace tree [:span {:class "hljs-keyword"} "iffff"])

                       (string? node)
                       ;; TODO: change this to zip/update
                       (zip/replace tree (string/replace node #"&quot;" "\""))

                       :else
                       tree)))

        fixed (loop [tree p-zero-tree]
                (if (not (zip/end? tree))
                  (recur (zip/next (fix-node tree)))
                  (zip/root tree)))]

    (.log js/console :p-zero p-zero)
    (.log js/console :fixed fixed)

    #_(.log js/console :orig highlighted-js-text)
    #_(.log js/console :pp pp)
    #_(.log js/console :ppp ppp)
    #_(.log js/console (map hickory.core/as-hiccup (hickory.core/parse-fragment "&lArr;")))
    #_(.log js/console (map hickory.core/as-hiccup (hickory.core/parse-fragment "&nbsp;")))
    #_(.log js/console (map hickory.core/as-hiccup (hickory.core/parse-fragment "&quot;")))
    fixed))

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
          [:pre#program-display {:ref #(swap! dom-nodes assoc :code-elem %)}
           tagged-code]))})))


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
