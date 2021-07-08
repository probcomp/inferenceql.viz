(ns inferenceql.viz.eventsubs
  (:require [re-frame.core :as rf]
            [inferenceql.viz.db :as db]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]))

(rf/reg-event-db
 :initialize-db
 event-interceptors
 (fn [_ _]
   (db/default-db)))

(rf/reg-event-db
  :set-page
  event-interceptors
  (fn [db [_ new-val]]
    (assoc-in db [:page] new-val)))

(rf/reg-sub
  :page
  (fn [db _]
    (get-in db [:page])))

