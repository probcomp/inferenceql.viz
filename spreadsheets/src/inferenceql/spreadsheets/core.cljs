(ns inferenceql.spreadsheets.core
  (:require [goog.dom :as dom]
            [re-frame.core :as rf]
            [reagent.core :as reagent]
            ;; Core
            [inferenceql.spreadsheets.events]
            [inferenceql.spreadsheets.subs]
            [inferenceql.spreadsheets.views :as views]
            ;; Control Panel
            [inferenceql.spreadsheets.panels.control.events]
            [inferenceql.spreadsheets.panels.control.subs]
            ;; Viz Panel
            [inferenceql.spreadsheets.panels.viz.subs]
            ;; Table Panel
            [inferenceql.spreadsheets.panels.table.events]
            [inferenceql.spreadsheets.panels.table.subs]
            [inferenceql.spreadsheets.panels.table.effects]
            ;; Override Panel
            [inferenceql.spreadsheets.panels.override.events]
            [inferenceql.spreadsheets.panels.override.subs]
            ;; Query Component
            [inferenceql.spreadsheets.components.query.events]
            ;; Highlight Component
            [inferenceql.spreadsheets.components.highlight.events]
            [inferenceql.spreadsheets.components.highlight.subs]
            ;; Library functions for user-defined JS functions.
            [inferenceql.user]))

(enable-console-print!)
(set! *warn-on-infer* true)

(rf/dispatch-sync [:initialize-db])

(defn ^:export -main
  []
  (reagent/render [views/app] (dom/$ "app")))
