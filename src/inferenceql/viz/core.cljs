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
            [inferenceql.viz.user]))

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
  (rf/dispatch-sync [:app/initialize-db])
  (rf/dispatch-sync [:upload/read-query-string-params (query-string-params)])
  (rf/dispatch-sync [:control/set-query-string-to-select-all])
  (render-app))
