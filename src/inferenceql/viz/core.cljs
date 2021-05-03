(ns ^:figwheel-hooks inferenceql.viz.core
  (:require [goog.dom :as dom]
            [re-frame.core :as rf]
            [reagent.dom :as rdom]
            ;; Core
            [inferenceql.viz.util :refer [query-string-params]]
            [inferenceql.viz.events]
            [inferenceql.viz.effects]
            [inferenceql.viz.views :as views]
            [inferenceql.viz.vega :as vega.init]
            ;; Control Panel
            [inferenceql.viz.panels.control.events]
            [inferenceql.viz.panels.control.subs]
            ;; Viz Panel
            [inferenceql.viz.panels.viz.events]
            [inferenceql.viz.panels.viz.subs]
            ;; Table Panel
            [inferenceql.viz.panels.table.events]
            [inferenceql.viz.panels.table.subs]
            [inferenceql.viz.panels.table.handsontable-events]
            [inferenceql.viz.panels.table.handsontable-effects]
            ;; More Panel
            [inferenceql.viz.panels.more.events]
            [inferenceql.viz.panels.more.subs]
            ;; Override Panel
            [inferenceql.viz.panels.override.events]
            [inferenceql.viz.panels.override.subs]
            ;; Modal Panel
            [inferenceql.viz.panels.modal.events]
            [inferenceql.viz.panels.modal.subs]
            ;; Upload Panel
            [inferenceql.viz.panels.upload.events]
            [inferenceql.viz.panels.upload.effects]
            ;; JSmodel Panel
            [inferenceql.viz.panels.jsmodel.subs]
            ;; Query Component
            [inferenceql.viz.components.query.events]
            [inferenceql.viz.components.query.subs]
            ;; Highlight Component
            [inferenceql.viz.components.highlight.events]
            [inferenceql.viz.components.highlight.subs]
            ;; Store Component
            [inferenceql.viz.components.store.events]
            [inferenceql.viz.components.store.subs]
            ;; Library functions for user-defined JS functions.
            [inferenceql.viz.user]
            ;; Functions for running iql queries from JS.
            [inferenceql.query.js]
            ;; Misc requires for observable.
            [inferenceql.viz.config :as config]
            [inferenceql.viz.csv :refer [csv-data->clean-maps
                                         cast-items-in-row]]

            [clojure.edn :as edn]
            [inferenceql.viz.panels.table.handsontable :refer [default-hot-settings]]
            [inferenceql.viz.panels.table.views :refer [handsontable]]
            [inferenceql.viz.panels.table.subs :refer [column-settings]]
            [inferenceql.viz.panels.viz.views :as viz-views]
            [inferenceql.viz.panels.viz.vega :as vega]))

(enable-console-print!)
(set! *warn-on-infer* true)

(vega.init/add-custom-vega-color-schemes)

(defn ^:after-load render-app
  "Renders the primary reagent component for the app onto the DOM element, #app

  Tagged with :after-load so that figwheel will call this function after every hot-reload."
  []
  (rdom/render [views/app] (dom/$ "app")))

(defn ^:export -main
  "The main entry point for the app.

  Called from javascript in resources/index.html on initial page load."
  []
  ;; We only initialize the app-db on first load. This is so figwheel's hot code reloading does
  ;; not reset the state of the app.
  (rf/dispatch-sync [:app/initialize-db]))

;--------------------------------------------------------------------------------

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

(defn ^:export read_schema
  [schema-string]
  (clj->js (edn/read-string schema-string)))

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
