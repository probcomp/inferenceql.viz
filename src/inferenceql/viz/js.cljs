(ns inferenceql.viz.js
  (:require [clojure.edn :as edn]
            [inferenceql.viz.csv :refer [csv-data->clean-maps cast-items-in-row]]
            [inferenceql.viz.panels.table.handsontable :refer [default-hot-settings]]
            [inferenceql.viz.panels.table.views :refer [handsontable]]
            [inferenceql.viz.panels.table.subs :refer [column-settings]]
            [inferenceql.viz.panels.viz.views :as viz-views]
            [inferenceql.viz.panels.viz.vega :as vega]))

(defn ^:export read_schema
  [schema-string]
  (clj->js (edn/read-string schema-string)))

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
     (rdom/render [handsontable
                   {:style {:padding-bottom "10px"}}
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
        data (mapv #(cast-items-in-row schema %) data)

        spec (vega/generate-spec schema data selections)
        comp [viz-views/vega-lite spec {:actions false} nil nil]

        n (dom/createElement "div")]
    (rdom/render comp n)
    n))
