(ns inferenceql.spreadsheets.panels.viz.events
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]))

(rf/reg-event-db
  :viz/set-pts-store
  event-interceptors
  (fn [db [_ new-val]]
    (assoc-in db [:viz-panel :pts-store] (js->clj new-val))))
