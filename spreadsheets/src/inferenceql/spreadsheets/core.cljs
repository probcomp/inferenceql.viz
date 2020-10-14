(ns ^:figwheel-hooks inferenceql.spreadsheets.core
  (:require [goog.dom :as dom]
            [re-frame.core :as rf]
            [reagent.dom :as rdom]
            ;; Core
            [inferenceql.spreadsheets.events]
            [inferenceql.spreadsheets.subs]
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
            ;; More Panel
            [inferenceql.spreadsheets.panels.more.events]
            [inferenceql.spreadsheets.panels.more.subs]
            ;; Override Panel
            [inferenceql.spreadsheets.panels.override.events]
            [inferenceql.spreadsheets.panels.override.subs]
            ;; Modal Panel
            [inferenceql.spreadsheets.panels.modal.events]
            [inferenceql.spreadsheets.panels.modal.subs]
            ;; Query Component
            [inferenceql.spreadsheets.components.query.events]
            ;; Highlight Component
            [inferenceql.spreadsheets.components.highlight.events]
            [inferenceql.spreadsheets.components.highlight.subs]
            ;; Library functions for user-defined JS functions.
            [inferenceql.user]))

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
  (rf/dispatch-sync [:initialize-db])
  (render-app))
