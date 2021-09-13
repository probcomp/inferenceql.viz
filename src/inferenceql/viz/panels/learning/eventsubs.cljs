(ns inferenceql.viz.panels.learning.eventsubs
  (:require [re-frame.core :as rf]
            [goog.string :refer [format]]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]
            [inferenceql.viz.config :refer [config]]))

(rf/reg-sub
 :learning/iteration
 (fn [db _]
   ;; TODO: move default value to db.
   (get-in db [:learning-panel :iteration] 2)))

(rf/reg-event-db
  :learning/set-iteration
  event-interceptors
  (fn [db [_ new-val]]
    (assoc-in db [:learning-panel :iteration] new-val)))

(rf/reg-sub
 :learning/col-selection
 (fn [db _]
   ;; TODO: move default value to db.
   (let [default (set (map keyword (get (first (:transitions config))
                                        "names")))]
     (.log js/console :here------ default)
     (get-in db [:learning-panel :col-selection] default))))

(rf/reg-event-db
  :learning/select-cols
  event-interceptors
  (fn [db [_ new-val]]
    (assoc-in db [:learning-panel :col-selection] new-val)))
