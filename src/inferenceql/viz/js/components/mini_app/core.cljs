(ns inferenceql.viz.js.components.mini-app.core
  (:require [goog.dom :as dom]
            [reagent.dom :as rdom]
            [reagent.core :as r]
            [cljs-bean.core :refer [->clj]]
            [inferenceql.viz.js.components.table.views :refer [handsontable-simple]]
            [inferenceql.viz.js.components.mini-app.views :refer [failure-msg control-panel]]))

(defn mini-app
  "Javascript interface to mini-app UI component. Returns a DOM node with the mini-app.
  `query-fn` - is a function that takes a query string to execute and returns a promise which will
    resolve to the result-set of the query.
  `options` - is a Javascript object with keys that pertain to the various options available to
    the handsontable component and an additional `query` key which specifies the starting query
    for the mini-app."
  [query-fn options]
  (let [options (->clj options)
        query (get options :query "SELECT * FROM data;")

        results (r/atom nil)
        failure (r/atom nil)
        node (dom/createElement "div")

        ;; Success callback.
        success-cb #(do (reset! failure nil)
                        (reset! results (->clj %))
                        ;; Attach the results to the value property of the DOM node.
                        ;; Relevant when used with Observable's [view of] operator.
                        (set! (.-value node) %)
                        ;; Emit an event to signal update.
                        ;; Relevant when used with Observable's [view of] operator.
                        (.dispatchEvent node (js/CustomEvent. "input")))
        ;; Failure callback.
        failure-cb #(do (reset! failure %)
                        (reset! results nil)
                        (set! (.-value node) nil)
                        (.dispatchEvent node (js/CustomEvent. "input")))
        component (fn []
                    [:div
                     [control-panel query query-fn success-cb failure-cb]
                     (if (some? @failure)
                       [failure-msg @failure]
                       [handsontable-simple :reagent-observable
                        {:style {:padding-bottom "5px"}} @results options])])]
    (rdom/render [component] node)
    ;; Starting value of the component is nil.
    ;; Relevant when used with Observable's [view of] operator.
    (set! (.-value node) nil)
    node))

