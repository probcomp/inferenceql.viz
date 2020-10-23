(ns inferenceql.spreadsheets.components.query.subs
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

(defn ^:sub column-renames
  "Returns a map of columns that have been renamed as a result of executing the last query.

  Columns can be renames in queries using the AS keyword.
  Returns: {:old-column-name :new-column-name, ...}"
  [column-details]
  (->> column-details
       (filter #(= (:detail-type %) :rename))
       (map (juxt :old-name :new-name))
       (into {})))
(rf/reg-sub :query/column-renames
            :<- [:query/column-details]
            column-renames)

(defn ^:sub new-columns-schema
  "Returns a schema for new columns that were created as a result of executing the last query.

  Returns: {:new-column-name :gaussian, ...}"
  [column-details]
  (->> column-details
       (filter #(= (:detail-type %) :new-column-schema))
       (map (juxt :name :stat-type))
       (into {})))
(rf/reg-sub :query/new-columns-schema
            :<- [:query/column-details]
            new-columns-schema)

(defn ^:sub schema
  "Returns a schema for the query whose results are currently displayed.

  This schema includes new columns that have been generated and columns that have been renamed.
  Returns: {:column-name-1 :gaussian, :column-name-2 :categorical, ...}"
  [[dataset column-renames new-columns-schema]]
  (let [schema (:schema dataset)
        schema-with-renames (medley/map-keys #(get column-renames % %) schema)]
    (merge new-columns-schema schema-with-renames)))
(rf/reg-sub :query/schema
            :<- [:query/dataset]
            :<- [:query/column-renames]
            :<- [:query/new-columns-schema]
            schema)
