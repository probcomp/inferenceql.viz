(ns inferenceql.viz.panels.control.eventsubs
  (:require [re-frame.core :as rf]
            [goog.string :refer [format]]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]))

(rf/reg-sub
  :control/iteration
  (fn [db _]
    (get-in db [:control-panel :iteration])))

(rf/reg-event-db
  :control/set-iteration
  event-interceptors
  (fn [db [_ new-val]]
    (assoc-in db [:control-panel :iteration] new-val)))

(rf/reg-sub
  :control/col-selection
  (fn [db _]
    (get-in db [:control-panel :col-selection])))

(rf/reg-event-db
  :control/select-cols
  event-interceptors
  (fn [db [_ new-val]]
    (assoc-in db [:control-panel :col-selection] new-val)))

(rf/reg-sub
  :control/plot-type
  (fn [db _]
    (get-in db [:control-panel :plot-type])))

(rf/reg-event-db
  :control/set-plot-type
  event-interceptors
  (fn [db [_ new-val]]
    (assoc-in db [:control-panel :plot-type] new-val)))

(rf/reg-sub
  :control/marginal-types
  (fn [db _]
    (get-in db [:control-panel :marginal-types])))

(rf/reg-event-db
  :control/set-marginal-types
  event-interceptors
  (fn [db [_ new-val]]
    (assoc-in db [:control-panel :marginal-types] new-val)))

(rf/reg-sub
  :control/cluster-selected
  (fn [db _]
    (get-in db [:control-panel :cluster-selected])))

(rf/reg-event-db
  :control/select-cluster
  event-interceptors
  (fn [db [_ new-selection]]
    (assoc-in db [:control-panel :cluster-selected] new-selection)))

(rf/reg-sub
  :control/show-plot-options
  (fn [db _]
    (get-in db [:control-panel :show-plot-options])))

(rf/reg-event-db
  :control/toggle-plot-options
  event-interceptors
  (fn [db [_]]
    (update-in db [:control-panel :show-plot-options] not)))

(rf/reg-sub
  :control/mi-threshold
  (fn [db _]
    (get-in db [:control-panel :mi-threshold])))

(rf/reg-event-db
  :control/set-mi-threshold
  event-interceptors
  (fn [db [_ new-val]]
    (assoc-in db [:control-panel :mi-threshold] new-val)))
