(ns inferenceql.viz.events
  (:require [re-frame.core :as rf]
            [inferenceql.viz.db :as db]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]))

(rf/reg-event-db
 :app/initialize-db
 event-interceptors
 (fn [_ _]
   (db/default-db)))

(rf/reg-event-fx
  :app/alert
  event-interceptors
  (fn [_ [_ msg]]
    {:fx [[:js/alert msg]]}))
