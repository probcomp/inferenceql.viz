(ns inferenceql.spreadsheets.components.store.events
  "Contains events related to the store panel."
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]))

(rf/reg-event-db
 :store/dataset
 event-interceptors
 (fn [db [_ dataset-name schema default-model dataset-data]]
   (let [entry {:rows dataset-data
                :schema schema
                :default-model default-model}]
     (assoc-in db [:store-component :datasets dataset-name] entry))))

(rf/reg-event-db
 :store/model
 event-interceptors
 (fn [db [_ model-name model-data]]
   (assoc-in db [:store-component :models model-name] model-data)))