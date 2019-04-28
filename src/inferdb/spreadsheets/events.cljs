(ns inferdb.spreadsheets.events
  (:require [re-frame.core :as rf]
            [inferdb.spreadsheets.db :as db]))

(rf/reg-event-db
 :initialize-db
 (fn [db _]
   (db/default-db)))
