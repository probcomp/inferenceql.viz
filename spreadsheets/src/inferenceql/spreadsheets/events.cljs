(ns inferenceql.spreadsheets.events
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.db :as db]
            [inferenceql.spreadsheets.panels.table.db :as table-db]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]
            [inferenceql.spreadsheets.model :as model]
            [inferenceql.spreadsheets.score :as score]))

(rf/reg-event-db
 :initialize-db
 event-interceptors
 (fn [_ _]
   (db/default-db)))

(rf/reg-event-db
 :compute-row-likelihoods
 event-interceptors
 (fn [db [_]]
   (let [table-rows (table-db/table-rows db)
         likelihoods (score/row-likelihoods model/spec table-rows)]
     (assoc db ::db/row-likelihoods likelihoods))))

(rf/reg-event-db
 :compute-missing-cells
 event-interceptors
 (fn [db [_]]
   (let [table-rows (table-db/table-rows db)
         headers (table-db/table-headers db)
         missing-cells (score/impute-missing-cells model/spec headers table-rows)]
     (assoc db ::db/missing-cells missing-cells))))
