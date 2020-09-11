(ns inferenceql.spreadsheets.components.store.events
  "Contains events related to the store panel."
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]
            [medley.core :as medley]))

(rf/reg-event-db
 :store/dataset
 event-interceptors
 (fn [db [_ dataset-name dataset schema default-model]]
   (let [entry {:rows dataset
                :schema schema
                :default-model default-model}]
     (-> db
         (assoc-in [:store-component :datasets dataset-name] entry)))))

(rf/reg-event-db
 :store/model
 event-interceptors
 (fn [db [_ model-name model]]
   (-> db
       (assoc-in [:store-component :models model-name] model))))

;---------------

(rf/reg-event-db
  :store/datasets
  event-interceptors
  (fn [db [_ datasets]]
    ;; TODO: filter out unused keys in each of the entities.
    (-> db
        (update-in [:store-component :datasets] merge datasets))))

(rf/reg-event-db
  :store/models
  event-interceptors
  (fn [db [_ models]]
    (let [models (medley/map-vals :model-obj models)]
      (-> db
          (update-in [:store-component :models] merge models)))))

(rf/reg-event-db
  :store/geodata
  event-interceptors
  (fn [db [_ geodata]]
    ;; TODO: filter out unused keys in each of the entities.
    (-> db
        (update-in [:store-component :geodata] merge geodata))))
