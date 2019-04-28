(ns inferdb.spreadsheets.events
  (:require [re-frame.core :as rf]
            [inferdb.spreadsheets.db :as db]
            [inferdb.spreadsheets.events.interceptors :as interceptors]))

(rf/reg-event-db
 :initialize-db
 [interceptors/check-spec]
 (fn [db _]
   (db/default-db)))
