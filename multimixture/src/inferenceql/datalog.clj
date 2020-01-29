(ns inferenceql.datalog
  "Functions for storing tables, models, and information about them in a Datahike
  database."
  (:require [datascript.core :as d]
            [inferenceql.multimixture.specification :as spec]))

(def schema
  "Datahike schema for storing models and metadata about those models."
  {:iql/type
   {:db/cardinality :db.cardinality/one
    :db/doc         "InferenceQL type. One of #{:iql.type/model :iql.type/variable :iql.type/column :iql.type/row}."}

   :iql.model/generative-function
   {:db/cardinality :db.cardinality/one
    :db/doc         "Generative function for the model."}

   :iql.model/variables
   {:db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/many
    :db/doc         "Variables that comprise the model."}

   :iql.variable/name
   {:db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity
    :db/doc         "Human-readable name for a variable."}

   :iql.variable/column
   {:db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity
    :db/doc         ":db/ident for the column attribute in the table database."}

   :iql.variable/stattype
   {:db/cardinality :db.cardinality/one
    :db/doc         "Statistical type. One of #:iql.stattype{:gaussian :categorical}"}

   :iql.variable/categories
   {:db/cardinality :db.cardinality/many
    :db/doc         "Categories for the given categorical variable."}

   :iql.column/name
   {:db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity
    :db/doc         "Human-readable name for a column."}

   :iql.column/attribute
   {:db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity
    :db/doc         "Attribute that stores values for the column."}})

(defn ^:private variable-facts
  "Returns facts about the variables defined in the provided multimixutre."
  [mmix]
  (mapv (fn [variable]
          (merge {:iql/type              :iql.type/variable
                  :iql.variable/name     variable
                  :iql.variable/stattype (spec/stattype mmix variable)}
                 (when (spec/nominal? mmix variable)
                   {:iql.variable/categories (spec/categories mmix variable)})))
        (spec/variables mmix)))

(defn model-facts
  "Takes a multimixture specification and produces facts about that specificaiton
  that satisfy `iqldl.model/schema`."
  [mmix row-generator]
  [{:iql/type                      :iql.type/model
    :iql.model/generative-function (row-generator mmix)
    :iql.model/variables           (variable-facts mmix)}])

(defn column-fact
  [name ident value-type]
  {:iql.column/attribute ident
   :db/valueType     value-type
   :db/cardinality   :db.cardinality/one
   :iql/type         :iql.type/column
   :iql.column/name  name})

(defn row-fact
  "When applied to a row returns a Datahike fact that, when transacted, will store
  that row in a database that uses `schema`."
  [row]
  (assoc row :iql/type :iql.type/row))

(defn table-facts
  "Returns a Datahike fact that, when transacted, will store that row in a
  database that uses `schema`. Rows are represented as maps."
  [column-idents column-types rows]
  (let [columns (into #{} (mapcat keys) rows)
        column-facts (mapv (comp #(apply column-fact %)
                                 (juxt identity column-idents column-types))
                           columns)
        row-facts (map #(->> %
                             (map (juxt (comp column-idents key) val))
                             (into {})
                             (row-fact))
                       rows)]
    (into column-facts row-facts)))

(defn model-table-fact
  [variable column]
  {:iql.variable/name variable
   :iql.variable/column [:iql.column/name column]})

(def rules
  '[[(model? ?e)
     [?e :iql/type :iql.type/model]]
    [(variable? ?e)
     [?e :iql/type :iql.type/variable]]
    [(gfn? ?e)
     [_ :iql.model/generative-function ?e]]
    [(model-variable ?m ?v)
     [?m :iql.model/variables ?v]
     [?m :iql/type :iql.type/model]
     [?v :iql/type :iql.type/variable]]
    [(variable-categories ?v ?c)
     [?ve :iql.variable/name ?v]
     [?ve :iql.variable/categories ?c]]
    [(variable-stattype ?v ?s)
     [?e :iql.variable/name ?v]
     [?e :iql.variable/stattype ?s]]
    [(categorical? ?v)
     [?v :iql.variable/stattype :iql.stattype/categorical]]
    [(row ?e)
     [?e :iql/type :iql.type/row]]
    [(column ?a)
     [?e :iql/type :iql.type/column]
     [?e :iql.column/attribute ?a]]])

(defn pull-row
  "Like `datahike.api/pull`, only always returns every known fact about the row."
  [db eid]
  (let [column-idents (into {} (d/q '[:find ?c ?name
                                      :in $ %
                                      :where
                                      (column ?c)
                                      [?e :iql.column/attribute ?c]
                                      [?e :iql.column/name ?name]]
                                    db
                                    rules))]
    (->> (d/pull db (keys column-idents) eid)
         (map (juxt (comp column-idents key)
                    val))
         (into {}))))
