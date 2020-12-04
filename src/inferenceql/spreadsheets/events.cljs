(ns inferenceql.spreadsheets.events
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.db :as db]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]))

(rf/reg-event-db
 :initialize-db
 event-interceptors
 (fn [_ _]
   (db/default-db)))
