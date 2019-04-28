(ns inferdb.spreadsheets.events
  (:require [re-frame.core :as rf]
            [inferdb.spreadsheets.db :as db]
            [inferdb.spreadsheets.events.interceptors :as interceptors]))

(rf/reg-event-db
 :initialize-db
 [interceptors/check-spec]
 (fn [db _]
   (db/default-db)))

(rf/reg-event-db
 :after-selection-end
 [interceptors/check-spec]
 (fn [db [_ row column row2 column2 selection-layer-level]]
   (db/selection db row column row2 column2 selection-layer-level)))

(rf/reg-event-db
 :after-deselect
 [interceptors/check-spec]
 (fn [db _]
   (db/clear-selections db)))
