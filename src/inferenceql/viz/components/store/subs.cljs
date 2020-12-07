(ns inferenceql.viz.components.store.subs
  (:require [re-frame.core :as rf]))

(defn ^:sub datasets
  "Returns a map of all datasets."
  [db _]
  (get-in db [:store-component :datasets]))
(rf/reg-sub :store/datasets
            datasets)

(defn ^:sub models
  "Returns a map of all models."
  [db _]
  (get-in db [:store-component :models]))
(rf/reg-sub :store/models
            models)

(defn ^:sub geodata
  "Returns a map of all geodata."
  [db _]
  (get-in db [:store-component :geodata]))
(rf/reg-sub :store/geodata
            geodata)
