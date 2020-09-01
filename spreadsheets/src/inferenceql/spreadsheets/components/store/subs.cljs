(ns inferenceql.spreadsheets.components.store.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 :store/datasets
 (fn [db _]
   (get-in db [:store-component :datasets])))

(rf/reg-sub
 :store/models
 (fn [db _]
   (get-in db [:store-component :models])))


