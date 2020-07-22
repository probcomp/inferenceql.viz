(ns inferenceql.spreadsheets.panels.viz.events-circle
  (:require [re-frame.core :as rf]))


(rf/reg-event-db
  :circle/set-threshold
  event-interceptors
  (fn [db [_ new-val]]
    (assoc-in db [:viz-panel :circle :threshold] new-val)))

