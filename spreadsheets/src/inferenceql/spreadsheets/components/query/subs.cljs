(ns inferenceql.spreadsheets.components.query.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
 :query/dataset-name
 (fn [db _]
   (get-in db [:query-component :dataset-name])))

(rf/reg-sub
 :query/model-name
 (fn [db _]
   (get-in db [:query-component :model-name])))

(rf/reg-sub
 :query/virtual
 (fn [db _]
   (get-in db [:query-component :virtual])))

(defn ^:sub dataset
  "Returns the dataset referenced in the query text."
  [[dataset-name datasets]]
  (get datasets dataset-name))
(rf/reg-sub :query/dataset
            :<- [:query/dataset-name]
            :<- [:store/datasets]
            dataset)
