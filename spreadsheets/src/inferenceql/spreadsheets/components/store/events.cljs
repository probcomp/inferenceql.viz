(ns inferenceql.spreadsheets.components.store.events
  "Contains events related to the store panel."
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]))

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