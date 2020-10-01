(ns inferenceql.spreadsheets.components.store.events
  "Contains events related to the store panel."
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]
            [medley.core :as medley]))

(rf/reg-event-db
  :store/datasets
  event-interceptors
  (fn [db [_ datasets]]
    (update-in db [:store-component :datasets] merge datasets)))

(rf/reg-event-db
  :store/models
  event-interceptors
  (fn [db [_ models]]
    (update-in db [:store-component :models] merge models)))

(rf/reg-event-db
  :store/geodata
  event-interceptors
  (fn [db [_ geodata]]
    (update-in db [:store-component :geodata] merge geodata)))
