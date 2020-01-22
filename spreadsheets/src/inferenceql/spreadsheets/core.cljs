(ns inferenceql.spreadsheets.core
  (:require [goog.dom :as dom]
            [re-frame.core :as rf]
            [reagent.core :as reagent]
            [inferenceql.spreadsheets.events]
            [inferenceql.spreadsheets.subs]
            [inferenceql.spreadsheets.views :as views]
            [inferenceql.user]))

(enable-console-print!)
(set! *warn-on-infer* true)

(rf/dispatch-sync [:initialize-db])

(defn ^:export -main
  []
  (reagent/render [views/app] (dom/$ "app")))
