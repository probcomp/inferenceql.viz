(ns inferenceql.spreadsheets.query
  #?(:clj (:require [inferenceql.spreadsheets.io :as sio])
     :cljs (:require-macros [inferenceql.spreadsheets.io :as sio]))
  (:require [clojure.core.match :refer [match]]
            [clojure.edn :as edn]
            [instaparse.core :as insta]
            [metaprob.prelude :as mp]
            [inferenceql.multimixture :as mmix]
            [inferenceql.spreadsheets.model :as model]))

(def parser (insta/parser (sio/inline-resource "query.bnf")))
(def ^:private row-generator (mmix/row-generator model/spec))

(defn- transform-from
  ([limit]
   (transform-from {} limit))
  ([obs limit]
   [:generate obs limit]))

(defn- transform-select
  [& args]
  (match (vec args)
    [[:what & columns] [:from table]]
    {:table table
     :xform (if (some #{[:star]} columns)
              (map identity)
              (let [columns (map #(if (qualified-symbol? %)
                                    %
                                    (symbol (str table)
                                            (str %)))
                                 columns)]
                (map #(select-keys % columns))))}))

(defn transform-result-column
  ([column]
   (symbol (str column)))
  ([table column]
   (symbol (str table) (str column))))

(def ^:private transform-map
  {:select transform-select
   :result-column transform-result-column
   :column str
   :string edn/read-string
   :symbol edn/read-string})

(defn parse
  "Parses a string containing an InferenceQL query and produces a map representing
  the query to be performed."
  [& args]
  (let [ast (apply parser args)]
    (insta/transform transform-map ast)))

(defn execute
  [{:keys [xform table]} env]
  (into [] xform (get env table)))

(comment

  (let [query (parse "SELECT x, y FROM table")]
    (execute query {'table '[{table/x 1 table/y 2 table/z 3 table/q 0}
                             {table/x 4 table/y 5 table/z 6 table/q 7}]}))

  {'table [{"x" 1 "y" 2 "z" 3 "q" 0}
           {"x" 4 "y" 5 "z" 6 "q" 7}]}

  (parse "SELECT test.x, y FROM table")

  )
