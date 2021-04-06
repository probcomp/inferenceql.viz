(ns inferenceql.viz.events
  (:require [re-frame.core :as rf]
            [inferenceql.viz.db :as db]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]))

(rf/reg-event-db
 :initialize-db
 event-interceptors
 (fn [_ _]
   (db/default-db)))

(rf/reg-event-fx
  :alert
  event-interceptors
  (fn [_ [_ msg]]
    {:fx [[:js/alert msg]]}))
