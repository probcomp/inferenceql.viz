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
            [clojure.string :as string]
            [clojure.edn :as edn]))

;; We are using the minimal version of highlight.js where
;; every language used has to be registered individually.
(.registerLanguage yarn-hljs "javascript" yarn-hljs-js)

(defn add-cluster-spans [highlighted-js-text]
  (let [highlighted-js-text (str "<code>" highlighted-js-text "</code>")

        p-zero (->> (hickory.core/parse-fragment highlighted-js-text)
                    (map hickory.core/as-hiccup)
                    (first))
        p-zero-tree (hickory.zip/hiccup-zip p-zero)

        ;; Removes n nodes. Returns position before all removals (depth-first).
        remove-n (fn [loc num-to-remove]
                   (loop [l loc n num-to-remove]
                     (cond
                       (> n 1) (recur (zip/next (zip/remove l)) (dec n))
                       (= n 1) (recur (zip/remove l) (dec n))
                       (= n 0) l)))

        view-id (fn [loc]
                  (loop [l loc]
                    (cond
                      (nil? l)
                      nil

                      (= (take 2 (zip/node l)) [:span {:class "hljs-function"}])
                      (let [func-name (-> l
                                          zip/down zip/right
                                          zip/right zip/down
                                          zip/node)]
                        (-> (re-matches #"view_(\d+)_model" func-name)
                            second
                            edn/read-string))

                      :else
                      (recur (zip/left l)))))

        ;; TODO
        if-statement-for-cluster? (fn [])
        ;; TODO
        cluster-id (fn [])

        fix-node (fn [loc]
                   (let [node (zip/node loc)
                         [r1 r2 r3] (take 3 (zip/rights loc))
                         [r2-tag r2-attr r2-content] r2]
                     (cond
                       (and (= node [:span {:class "hljs-keyword"} "if"])
                            (= r1 " (cluster_id == ")
                            (= [:span {:class "hljs-number"}] [r2-tag r2-attr])
                            (number? (edn/read-string r2-content))
                            (= r3 ") {\n    ret_val = {\n     "))
                       (let [cluster-id (edn/read-string r2-content)
                             view-id (view-id loc)]
                         (-> loc
                             (remove-n 4) ; Remove all the nodes we are going to re-insert with edits.
                             (zip/insert-right [:span {:class "cluster-button"
                                                       :style {:background-color "lightsteelblue"}
                                                       :onClick (fn [] (.log js/console "hi" view-id " " cluster-id))}
                                                [:span {:class "hljs-keyword"} "if"]
                                                r1
                                                r2
                                                ")"])
                             (zip/right)
                             (zip/insert-right (subs r3 1))))

                       (string? node)
                       ;; TODO: change this to zip/update
                       ;; TODO: change this to the escape function -- see notion notes.
                       (zip/replace loc (string/replace node #"&quot;" "\""))

                       :else
                       loc)))]
    (loop [loc p-zero-tree]
      (if (not (zip/end? loc))
        (recur (zip/next (fix-node loc)))
        (zip/root loc)))))

(defn js-code-block
  "Display of Javascript code with syntax highlighting.

  Args:
    `code` -- (string) The Javascript source code to display.

  Returns: A reagent component."
  [js-code]
  (let [ highlight (fn [js-text] (.-value (.highlight yarn-hljs js-text #js {"language" "js"})))]
    (r/create-class
     {:display-name "js-model-code"

      :reagent-render
      (fn [js-code]
        (let [tagged-code (-> js-code highlight add-cluster-spans)]
          [:pre#program-display
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
