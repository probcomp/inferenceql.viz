(ns inferenceql.spreadsheets.panels.crosscat.subs
  (:require [re-frame.core :as rf]))

(defn ^:sub option
  "Returns the value of option."
  [db _]
  (get-in db [:crosscat-panel :option]))
(rf/reg-sub :crosscat/option
            option)
