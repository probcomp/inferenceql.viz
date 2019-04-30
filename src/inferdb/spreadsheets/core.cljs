(ns inferdb.spreadsheets.core
  (:require [goog.dom :as dom]
            [re-frame.core :as rf]
            [reagent.core :as reagent]
            [inferdb.spreadsheets.events]
            [inferdb.spreadsheets.subs]
            [inferdb.spreadsheets.views :as views]))

(enable-console-print!)
(set! *warn-on-infer* true)

(rf/dispatch-sync [:initialize-db])

(defn ^:export -main
  []
  (reagent/render [views/app] (dom/$ "app")))
