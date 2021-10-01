(ns inferenceql.viz.panels.learning.eventsubs
  (:require [re-frame.core :as rf]
            [goog.string :refer [format]]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]
            [inferenceql.viz.config :refer [config transitions]]))

(rf/reg-sub
 :learning/iteration
 (fn [db _]
   ;; TODO: move default value to db.
   (get-in db [:learning-panel :iteration] 0)))

(rf/reg-event-db
  :learning/set-iteration
  event-interceptors
  (fn [db [_ new-val]]
    (assoc-in db [:learning-panel :iteration] new-val)))

(rf/reg-sub
 :learning/col-selection
 (fn [db _]
   ;; TODO: move default value to db.
   (let [default (set (map keyword (get (first transitions)
                                        "col_names")))]
     (get-in db [:learning-panel :col-selection] default))))

(rf/reg-event-db
  :learning/select-cols
  event-interceptors
  (fn [db [_ new-val]]
    (assoc-in db [:learning-panel :col-selection] new-val)))

(rf/reg-sub
 :learning/plot-type
 (fn [db _]
   ;; TODO: move default value to db.
   (get-in db [:learning-panel :plot-type] :select-vs-simulate)))

(rf/reg-event-db
  :learning/set-plot-type
  event-interceptors
  (fn [db [_ new-val]]
    (assoc-in db [:learning-panel :plot-type] new-val)))

(rf/reg-sub
 :learning/marginal-types
 (fn [db _]
   ;; TODO: move default value to db.
   (get-in db [:learning-panel :marginal-types] #{:1D})))

(rf/reg-event-db
  :learning/set-marginal-types
  event-interceptors
  (fn [db [_ new-val]]
    (assoc-in db [:learning-panel :marginal-types] new-val)))

(rf/reg-sub
 :learning/cluster-selected
 (fn [db _]
   (get-in db [:learning-panel :cluster-selected])))

(rf/reg-event-db
  :learning/select-cluster
  event-interceptors
  (fn [db [_ new-selection]]
    (assoc-in db [:learning-panel :cluster-selected] new-selection)))

(rf/reg-sub
 :learning/show-plot-options
 ;; TODO: move default value to db.
 (fn [db _]
   (get-in db [:learning-panel :show-plot-options] false)))

(rf/reg-event-db
  :learning/toggle-plot-options
  event-interceptors
  (fn [db [_]]
    (update-in db [:learning-panel :show-plot-options] not)))

(rf/reg-sub
 :learning/mi-threshold
 ;; TODO: move default value to db.
 (fn [db _]
   (get-in db [:learning-panel :mi-threshold] 0)))

(rf/reg-event-db
  :learning/set-mi-threshold
  event-interceptors
  (fn [db [_ new-val]]
    (assoc-in db [:learning-panel :mi-threshold] new-val)))
