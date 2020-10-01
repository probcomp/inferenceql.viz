(ns ^:figwheel-hooks inferenceql.spreadsheets.core
  (:require [goog.dom :as dom]
            [re-frame.core :as rf]
            [reagent.dom :as rdom]
            ;; Core
            [inferenceql.spreadsheets.events]
            [inferenceql.spreadsheets.effects]
            [inferenceql.spreadsheets.views :as views]
            [inferenceql.spreadsheets.vega :as vega.init]
            ;; Control Panel
            [inferenceql.spreadsheets.panels.control.events]
            [inferenceql.spreadsheets.panels.control.subs]
            ;; Viz Panel
            [inferenceql.spreadsheets.panels.viz.events]
            [inferenceql.spreadsheets.panels.viz.subs]
            ;; Table Panel
            [inferenceql.spreadsheets.panels.table.events]
            [inferenceql.spreadsheets.panels.table.subs]
            ;; Override Panel
            [inferenceql.spreadsheets.panels.override.events]
            [inferenceql.spreadsheets.panels.override.subs]
            ;; Upload Panel
            [inferenceql.spreadsheets.panels.upload.events]
            [inferenceql.spreadsheets.panels.upload.subs]
            [inferenceql.spreadsheets.panels.upload.effects]
            ;; Query Component
            [inferenceql.spreadsheets.components.query.events]
            [inferenceql.spreadsheets.components.query.subs]
            ;; Highlight Component
            [inferenceql.spreadsheets.components.highlight.events]
            [inferenceql.spreadsheets.components.highlight.subs]
            ;; Store Component
            [inferenceql.spreadsheets.components.store.events]
            [inferenceql.spreadsheets.components.store.subs]
            ;; Library functions for user-defined JS functions.
            [inferenceql.user]))

(enable-console-print!)
(set! *warn-on-infer* true)

(vega.init/add-custom-vega-color-schemes)

;; The main rendering function. Called from javascript in resources/index.html
;; on initial page load.
(defn ^:export -main
  []
  (rdom/render [views/app] (dom/$ "app")))

;; We only initialize the app-db on first load. This is so figwheel's hot code reloading does
;; not reset the state of the app.
(defonce initialize-db-done
         (do
           (rf/dispatch-sync [:initialize-db])
           true))

;; Figwheel calls this every time code is saved, compiled, and loaded back into
;; the live app
(defn ^:after-load re-render []
  (-main))

;; TODO: reorganize this a bit. So that defonce is not needed.
