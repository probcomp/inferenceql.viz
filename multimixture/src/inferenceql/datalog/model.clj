(ns inferenceql.datalog.model
  "Functions for storing models and information about them in a Datahike
  database."
  (:require [inferenceql.multimixture.search :as search]
            [inferenceql.multimixture.specification :as spec]))

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

(def ^:private stattype-idents
  "Keys are the strings from model JSON used to reprsent statistical types. Values
  are the ident `schema` uses to represent the same statistical type."
  {"gaussian"    :iql.stattype/gaussian
   "categorical" :iql.stattype/categorical})

(defn model-facts
  "Takes model JSON where the column names are keywords/idents and produces
  facts about those columns that satisfy `iqldl.model/schema`."
  [{variables "columns" :as model-json}]
  (let [generative-function (-> model-json spec/from-json search/optimized-row-generator)
        variable-facts (mapv (fn [[column stattype]]
                               {:iql/type :iql.type/variable
                                :iql.variable/column column
                                :iql.variable/stattype (get stattype-idents stattype)})
                             variables)]
    {:iql.model/generative-function generative-function
     :iql.model/variables variable-facts}))
