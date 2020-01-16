(ns inferenceql.datalog.table
  "Functions for storing tabular data and metadata about that data in a Datahike
  database."
  (:require [datahike.api :as d]))

(def schema
  "Datahike schema for storing a data table and metadata about that data table."
  [{:db/ident       :iql/type
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc         "InferenceQL type. One of #:iql.type{:column :row}."}

   {:db/ident :iql.type/column}
   {:db/ident :iql.type/row}

   {:db/ident       :iql.column/name
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/one
    :db/doc         "Human-readable name for a column."}

   {:db/ident       :iql.column/category
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/many
    :db/doc         "Possible values a categorical column could take."}

   {:db/ident       :iql.column/stattype
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc         "A column's statistical type."}])

(defn column-fact
  [variable value-type]
  {:db/ident       variable
   :db/valueType   value-type
   :db/cardinality :db.cardinality/one
   :iql/type       :iql.type/column})

(defn column-name-fact
  [ident name]
  {:db/ident        ident
   :iql.column/name name})

(defn row-fact
  "When applied to a row returns a Datahike fact that, when transacted, will store
  that row in a database that uses `schema`."
  [row]
  (assoc row :iql/type :iql.type/row))

(defn pull-row
  "Like `datahike.api/pull`, only always returns every known fact about the row."
  [db eid]
  (d/pull db '[*] eid))

(def rules
  "Datahike rules for use with `schema`."
  '[;; Satisfied if `?e` is an entity that represents a row.
    [(row ?e)
     [?e :iql/type :iql.type/row]]
    ;; Satisfied if `?a` is the ident for a column entity.
    [(column ?a)
     [?e :iql/type :iql.type/column]
     [?e :db/ident ?a]]])
