(ns inferenceql.viz.js
  (:require [clojure.edn :as edn]
            [goog.labs.format.csv :as goog.csv]
            [goog.dom :as dom]
            [reagent.dom :as rdom]
            [inferenceql.query.data :refer [row-coercer]]
            [inferenceql.viz.csv]
            [inferenceql.viz.panels.table.handsontable :refer [default-hot-settings]]
            [inferenceql.viz.panels.table.views :refer [handsontable]]
            [inferenceql.viz.panels.table.subs :refer [column-settings]]
            [inferenceql.viz.panels.viz.views :as viz-views]
            [inferenceql.viz.panels.viz.vega :as vega]
            ;; Functions for running iql queries from JS.
            [inferenceql.query.js]))

(defn ^:export read_schema
  [schema-string]
  (clj->js (edn/read-string schema-string)))

#_(defn ^:export read_and_coerce_csv
    [csv-text schema]
    (let [csv-vecs (-> csv-text goog.csv/parse js->clj)]
      (csv-data->clean-maps schema csv-vecs {:keywordize-cols true})))

(defn ^:export simple_test
  []
  (let [node (dom/createElement "div")]
    (rdom/render [:h1 "hello!"] node)
    node))

(defn ^:export table
  ([data]
   (table data {}))
  ([data options]
   (let [options (js->clj options)
         {:strs [cols height v-scroll]} options

         ;; Potentially grabbing the columns from the keys in the first row of data.
         cols (or cols (->> data first js-keys))

         height (cond
                  (false? v-scroll) "auto"
                  (some? height) height
                  :else
                  ;; TODO: may need to adjust these sizes.
                  (let [data-height (+ (* (count data) 22) 38)]
                    (min data-height 500)))

         node (dom/createElement "div")
         settings (-> default-hot-settings
                      (assoc-in [:settings :data] data)
                      (assoc-in [:settings :colHeaders] cols)
                      (assoc-in [:settings :columns] (column-settings cols))
                      (assoc-in [:settings :height] height)
                      (assoc-in [:settings :width] "100%"))]
     ;; todo: move component up
     (rdom/render [handsontable
                   {:style {:padding-bottom "5px"}}
                   settings]
                  node)
     node)))

(defn ^:export plot
  [data schema selections]
  (let [selections (for [cols selections]
                     (for [col cols]
                       (keyword col)))

        schema (js->clj schema :keywordize-keys true)
        data (js->clj data :keywordize-keys true)

        coercer (row-coercer schema)
        data (mapv coercer data)

        spec (vega/generate-spec schema data selections)
        comp [viz-views/vega-lite spec {:actions false} nil nil]

        n (dom/createElement "div")]
    (rdom/render comp n)
    n))
