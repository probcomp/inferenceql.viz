(ns inferenceql.viz.observable.components
  (:require [goog.dom :as dom]
            [reagent.dom :as rdom]
            [reagent.core :as r]
            [cljs-bean.core :refer [->clj]]
            [clojure.walk :refer [postwalk]]
            [inferenceql.viz.observable.components.viz :refer [vega-lite]]
            [inferenceql.viz.observable.components.hot :refer [handsontable-wrapper]]
            [inferenceql.viz.observable.components.message :refer [failure-msg]]
            [inferenceql.viz.observable.components.vega :as vega]
            [inferenceql.viz.observable.components.control :as control]
            [inferenceql.viz.observable.util :refer [clj-schema]]))

(defn ^:export js-handsontable-wrapper
  [props]
  (let [{:keys [data options]} (->clj props)]
    (let [data (->clj data)
          options (->clj options)]
      [handsontable-wrapper data options])))

(def ^:export react-js-handsontable-wrapper (r/reactify-component js-handsontable-wrapper))

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
    (let [selections (postwalk #(if (string? %) (keyword  %) %)
                               selections)
          schema (clj-schema schema)
          data (->clj data)
          spec (vega/generate-spec schema data selections)
          comp [vega-lite spec {:actions false} nil nil]
          node (dom/createElement "div")]
      (rdom/render comp node)
      node)))

(defn ^:export mini-app [query-fn options]
  (let [node (dom/createElement "div")

        options (->clj options)
        query (get options :query "SELECT * FROM data;")

        results (r/atom nil)
        failure (r/atom nil)
        update-results #(do
                          (reset! failure nil)
                          (reset! results (->clj %))
                          (set! (.-value node) %)
                          (.dispatchEvent node (js/CustomEvent. "input")))
        update-failure #(do
                          (reset! failure %)
                          (reset! results nil)
                          (set! (.-value node) nil)
                          (.dispatchEvent node (js/CustomEvent. "input")))
        comp (fn []
               [:div
                [control/panel query query-fn update-results update-failure]
                (if (some? @failure)
                  [failure-msg @failure]
                  [handsontable-wrapper @results options])])]
    (rdom/render [comp] node)
    (set! (.-value node) nil)
    node))
