(ns inferenceql.viz.panels.learning.eventsubs
  (:require [re-frame.core :as rf]
            [goog.string :refer [format]]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]))

(rf/reg-sub
 :learning/iteration
 (fn [db _]
   ;; TODO: move default value to db.
   (get-in db [:learning-panel :iteration] 10)))

(rf/reg-event-db
  :learning/set-iteration
  event-interceptors
  (fn [db [_ new-val]]
    (assoc-in db [:learning-panel :iteration] new-val)))
