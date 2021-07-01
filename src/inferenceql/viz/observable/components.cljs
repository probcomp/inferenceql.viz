(ns inferenceql.viz.observable.components
  (:require [goog.dom :as dom]
            [reagent.dom :as rdom]
            [reagent.core :as r]
            [cljs-bean.core :refer [->clj]]
            [inferenceql.viz.observable.components.viz :refer [vega-lite]]
            [inferenceql.viz.observable.components.hot :refer [handsontable-wrapper]]
            [inferenceql.viz.observable.components.vega :as vega]
            [inferenceql.viz.observable.components.control :as control]
            [inferenceql.viz.observable.util :refer [clj-schema]]))

(defn ^:export table
  ([data]
   (table data {}))
  ([data options]
   (let [data (->clj data)
         options (->clj options)

         node (dom/createElement "div")
         component [handsontable-wrapper data options]]
     (rdom/render component node)
     node)))

(defn ^:export plot
  [data schema selections]
  (if (some? data)
    (let [selections (for [cols selections]
                       (for [col cols]
                         (keyword col)))
          schema (clj-schema schema)
          data (js->clj data :keywordize-keys true)
          spec (vega/generate-spec schema data selections)
          comp [vega-lite spec {:actions false} nil nil]
          node (dom/createElement "div")]
     (rdom/render comp node)
     node)))

(defn ^:export mini-app [query-fn options]
  (let [node (dom/createElement "div")

        options (->clj options)
        query (get options :query "SELECT * FROM data;")

        input-text (r/atom query)
        results (r/atom nil)
        failure (r/atom nil)
        running (r/atom false)

        update-results #(do
                          (reset! failure nil)
                          (reset! results %)
                          (set! (.-value node) %)
                          (.dispatchEvent node (js/CustomEvent. "input")))

        update-failure #(do
                          (reset! failure %)
                          (reset! results nil)
                          (set! (.-value node) nil)
                          (.dispatchEvent node (js/CustomEvent. "input")))

        ;; TODO find a good way to remove this trackers
        ;; when component is unmounted. Otherwise, we have a
        ;; memory leak.
        ;;_ (r/track! #(set! (.-query node) @input-text))

        comp (fn []
               [:div
                [control/panel input-text running query-fn update-results update-failure]
                (if (some? @failure)
                  [:div {:class "observablehq--inspect"
                         :style {:whitespace "pre" :color "red"}}
                   @failure]
                  [handsontable-wrapper @results options])])]

    (rdom/render [comp] node)
    (set! (.-value node) nil)
    node))
