(ns inferdb.spreadsheets.core
  (:require [goog.dom :as dom]
            [re-frame.core :as rf]
            [reagent.core :as reagent]
            [inferdb.spreadsheets.events]
            [inferdb.spreadsheets.subs]
            [inferdb.spreadsheets.views :as views]
            [inferdb.spreadsheets.search :as search]))

(enable-console-print!)
(set! *warn-on-infer* true)

(rf/dispatch-sync [:initialize-db])

(defn ^:export -main
  []
  (println "searching...")
  (println (search/search-by-example
            {:percent_married_children 0.265
             :percent_black 0.09}
            #{:cluster-for-percap}
            2))
  (println "searched")
  (reagent/render [views/app] (dom/$ "app")))
