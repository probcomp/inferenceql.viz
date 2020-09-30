(ns inferenceql.spreadsheets.panels.more.subs
  (:require [re-frame.core :as rf]))

(defn ^:sub show-menu
  "Returns whether we should show the more menu."
  [db _]
  (get-in db [:more-panel :show-menu]))
(rf/reg-sub :more/show-menu
            show-menu)
