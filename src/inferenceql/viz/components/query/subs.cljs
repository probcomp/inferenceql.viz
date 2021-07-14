(ns inferenceql.viz.components.query.subs
  (:require [re-frame.core :as rf]
            [medley.core :as medley]))

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

(rf/reg-sub
 :query/column-details
 (fn [db _]
   (get-in db [:query-component :column-details])))

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

(defn ^:sub schema-base
  "Returns the base schema for the dataset referenced in the query executed."
  [db _]
  (get-in db [:query-component :schema-base]))

(rf/reg-sub :query/schema-base
            schema-base)

(defn ^:sub simulatable-cols
  "Returns a set of column names that are simulatable.

  These are columns that apppear in the dataset's schema and therefore they are
  assumed to be modeled."
  [schema]
  (-> schema keys set))
(rf/reg-sub :query/simulatable-cols
            :<- [:query/schema-base]
            simulatable-cols)

(defn ^:sub schema
  "Returns a schema for the query whose results are currently displayed.

  This schema includes new columns that have been generated and columns that have been renamed.
  Returns: {:column-name-1 :numerical, :column-name-2 :nominal, ...}"
  [db _]
  (get-in db [:query-component :schema]))
(rf/reg-sub :query/schema
            schema)
