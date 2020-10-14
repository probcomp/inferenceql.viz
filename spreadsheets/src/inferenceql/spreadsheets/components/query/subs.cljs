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

(defn ^:sub geo-id-col
  "Returns the geo-id-col associated with the dataset referenced in the query text."
  [dataset]
  (keyword (:geo-id-col dataset)))
(rf/reg-sub :query/geo-id-col
            :<- [:query/dataset]
            geo-id-col)

(defn ^:sub geodata
  "Returns the geodata associated with the dataset referenced in the query text."
  [[dataset geodata]]
  (get geodata (:geodata-name dataset)))
(rf/reg-sub :query/geodata
            :<- [:query/dataset]
            :<- [:store/geodata]
            geodata)

(defn ^:sub model
  "Returns the model referenced in the query text."
  [[model-name models]]
  (get models model-name))
(rf/reg-sub :query/model
            :<- [:query/model-name]
            :<- [:store/models]
            model)

;; TODO: not sure what this should do for virtual data or column renames yet.
;; Perhaps there could be a :query/column-renames
;; This would map all the columns back to the original schema columns.
;; And this could be used whether we are displaying virtual data or not.
(defn ^:sub schema
  "Returns a schema for the query whose results are currently displayed."
  [[dataset model virtual]]
  (:schema dataset))
(rf/reg-sub :query/schema
            :<- [:query/dataset]
            :<- [:query/model]
            :<- [:query/virtual]
            schema)
