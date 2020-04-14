(ns inferenceql.spreadsheets.panels.viz.events
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]
            [inferenceql.spreadsheets.panels.viz.vega :as vega]))

(rf/reg-event-fx
 :viz/run
 event-interceptors
 (fn [{:keys [db]} [_]]
   (let [new-points (vega/points 100)]
     ;; TODO: make simulations on tick work.
     {:db (update-in db [:viz-panel :points] concat new-points)})))

(rf/reg-event-db
 :viz/stop
 event-interceptors
 (fn [db [_ f path value]]))

(rf/reg-event-db
 :viz/clear
 event-interceptors
 (fn [db [_ f path value]]
   (assoc-in db [:viz-panel :points] [])))
