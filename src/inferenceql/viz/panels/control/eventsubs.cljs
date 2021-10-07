(ns inferenceql.viz.panels.control.eventsubs
  (:require [re-frame.core :as rf]
            [goog.string :refer [format]]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]
            [inferenceql.viz.config :refer [config transitions]]))

(rf/reg-sub
  :control/iteration
  (fn [db _]
    ;; TODO: move default value to db.
    (get-in db [:control-panel :iteration] 0)))

(rf/reg-event-db
  :control/set-iteration
  event-interceptors
  (fn [db [_ new-val]]
    (assoc-in db [:control-panel :iteration] new-val)))

(rf/reg-sub
  :control/col-selection
  (fn [db _]
    ;; TODO: move default value to db.
    (let [default (set (map keyword (get (first transitions)
                                         "col_names")))]
      (get-in db [:control-panel :col-selection] default))))

(rf/reg-event-db
  :control/select-cols
  event-interceptors
  (fn [db [_ new-val]]
    (assoc-in db [:control-panel :col-selection] new-val)))

(rf/reg-sub
  :control/plot-type
  (fn [db _]
    ;; TODO: move default value to db.
    (get-in db [:control-panel :plot-type] :select-vs-simulate)))

(rf/reg-event-db
  :control/set-plot-type
  event-interceptors
  (fn [db [_ new-val]]
    (assoc-in db [:control-panel :plot-type] new-val)))

(rf/reg-sub
  :control/marginal-types
  (fn [db _]
    ;; TODO: move default value to db.
    (get-in db [:control-panel :marginal-types] #{:1D})))

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
  ;; TODO: move default value to db.
  (fn [db _]
    (get-in db [:control-panel :show-plot-options] false)))

(rf/reg-event-db
  :control/toggle-plot-options
  event-interceptors
  (fn [db [_]]
    (update-in db [:control-panel :show-plot-options] not)))

(rf/reg-sub
  :control/mi-threshold
  ;; TODO: move default value to db.
  (fn [db _]
    (get-in db [:control-panel :mi-threshold] 0)))

(rf/reg-event-db
  :control/set-mi-threshold
  event-interceptors
  (fn [db [_ new-val]]
    (assoc-in db [:control-panel :mi-threshold] new-val)))
