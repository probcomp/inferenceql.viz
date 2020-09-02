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

(defn ^:sub model
  "Returns the model referenced in the query text."
  [[model-name models]]
  (get models model-name))
(rf/reg-sub :query/model
            :<- [:query/model-name]
            :<- [:store/models]
            model)

(defn ^:sub schema
  "Returns a schema for the query whose results are currently displayed.
  If we are displaying virtual data, the schema from the model used is returned.
  If we are displaying observed data, the schema from the dataset is returned."
  [[dataset model virtual]]
  (if virtual
    (:vars model)
    (:schema dataset)))
(rf/reg-sub :query/schema
            :<- [:query/dataset]
            :<- [:query/model]
            :<- [:query/virtual]
            schema)

(defn ^:sub model-schema
  "Returns a schema for the model used by the query whose results are currently displayed."
  [model]
  (:vars model))
(rf/reg-sub :query/model-schema
            :<- [:query/model]
            model-schema)
