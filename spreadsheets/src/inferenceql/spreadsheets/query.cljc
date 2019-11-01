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
  (let [table (match (vec args)
                [_ [:from table] _]
                table

                [_ [:from table]]
                table)
        columns (match (vec args)
                  [[:what & columns] [:from table] _]
                  columns

                  [[:what & columns] [:from table]]
                  columns)
        what-xf (map (if (some #{[:star]} columns)
                       identity
                       (let [qualified-columns (map #(if (qualified-symbol? %)
                                                       %
                                                       (symbol (str table)
                                                               (str %)))
                                                    columns)]
                         #(select-keys % qualified-columns))))
        where-xf (match (vec args)
                   [_ _ where-xf]
                   where-xf
                   :else (map identity))]
    {:table table
     :xform (comp where-xf what-xf)}))

(defn transform-result-column
  ([column]
   (symbol (str column)))
  ([table column]
   (symbol (str table) (str column))))

(defn transform-where
  [& stuff]
  (match (vec stuff)
    [[:predicate column [:comparator c] value]]
    (let [comp-f (case c
                   "=" =
                   "!=" not=)]
      (filter #(comp-f (get % column) value)))))

(def ^:private transform-map
  {:select transform-select
   :where transform-where
   :result-column transform-result-column
   :column str
   :string edn/read-string
   :symbol edn/read-string
   :int edn/read-string})

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

  (let [query (parse "SELECT x, y FROM table WHERE table.x=4")]
    (execute query {'table '[{table/x 1 table/y 2 table/z 3 table/q 0}
                             {table/x 4 table/y 5 table/z 6 table/q 7}]}))

  {'table [{"x" 1 "y" 2 "z" 3 "q" 0}
           {"x" 4 "y" 5 "z" 6 "q" 7}]}

  (parse "SELECT test.x, y FROM table")

  (parse "SELECT test.x, y FROM table WHERE x=3")

  (parser "SELECT test.x, y FROM table WHERE x=3")

  )
