(ns inferenceql.datalog.model
  "Functions for storing models and information about them in a Datahike
  database."
  (:require [inferenceql.multimixture.specification :as spec]))

(def schema
  "Datahike schema for storing models and metadata about those models."
  [{:db/ident       :iql/type
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc         "InferenceQL type. One of #{:iql.type/model :iql.type/variable}."}

   {:db/ident :iql.type/model}
   {:db/ident :iql.type/variable}

   {:db/ident       :iql.model/generative-function
    ;; :db/valueType <generative function>
    :db/cardinality :db.cardinality/one
    :db/doc         "Generative function for the model."}

   {:db/ident       :iql.model/variables
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/many
    :db/doc         "Variables that comprise the model."}

   {:db/ident       :iql.variable/column
    :db/valueType   :db.type/keyword
    :db/cardinality :db.cardinality/one
    :db/unique      :db.unique/identity
    :db/doc         ":db/ident for the column attribute in the table database."}

   {:db/ident       :iql.variable/stattype
    :db/valueType   :db.type/ref
    :db/cardinality :db.cardinality/one
    :db/doc         "Statistical type. One of #:iql.stattype{:gaussian :categorical}"}

   {:db/ident :iql.stattype/gaussian}
   {:db/ident :iql.stattype/categorical}

   {:db/ident       :iql.variable/categories
    :db/valueType   :db.type/string
    :db/cardinality :db.cardinality/many
    :db/doc         "Categories for the given categorical variable."}])

(defn ^:private category-facts
  "Returns facts about the categories for the categorical variables in the
  provided multimixture."
  [mmix]
  (->> (spec/variables mmix)
       (filter #(spec/nominal? mmix %))
       (mapv (fn [variable]
               {:iql.variable/categories (spec/categories mmix variable)}))))

(defn ^:private variable-facts
  "Returns facts about the variables defined in the provided multimixutre."
  [mmix]
  (mapv (fn [variable]
          (merge {:db/ident              variable
                  :iql/type              :iql.type/variable
                  :iql.variable/stattype (spec/stattype mmix variable)}
                 (when (spec/nominal? mmix variable)
                   {:iql.variable/categories (spec/categories mmix variable)})))
        (spec/variables mmix)))

(defn model-facts
  "Takes a multimixture specification and produces facts about those columns that
  satisfy `iqldl.model/schema`."
  [mmix row-generator]
  {:pre [(every? keyword? (spec/variables mmix))]}
  [{:iql/type                      :iql.type/model
    :iql.model/generative-function (row-generator mmix)
    :iql.model/variables           (variable-facts mmix)}])

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
     [?v :iql.variable/categories ?c]]
    [(variable-stattype ?v ?s-ident)
     [?v :iql.variable/stattype ?s]
     [?s :db/ident ?s-ident]]
    [(categorical? ?v)
     [?v :iql.variable/stattype :iql.stattype/categorical]]])
