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

(defn- find-node [k xs]
  (some #(when (and (coll? %)
                    (= (first %) k))
           %)
        xs))

(defn- transform-select
  [& args]
  (let [[_ table]   (find-node :from args)
        [_ & columns] (find-node :what args)
        what-xf (map (if (some #{[:star]} columns)
                       identity
                       (let [qualified-columns (map #(if (qualified-symbol? %)
                                                       %
                                                       (symbol (str table)
                                                               (str %)))
                                                    columns)]
                         #(select-keys % qualified-columns))))
        where-xf (if-let [[_ where-xf] (find-node :where args)]
                   where-xf
                   (map identity))
        limit-xf (if-let [[_ limit] (find-node :limit args)]
                   (take limit)
                   (map identity))]
    {:table table
     :xform (comp limit-xf where-xf what-xf)}))

(defn transform-result-column
  ([column]
   (let [col-symbol (symbol (str column))]
     {:name col-symbol :func #(get % col-symbol)}))
  ([table column]
   (let [col-symbol (symbol (str table) (str column))]
     {:name col-symbol :func #(get % col-symbol)})))

(defn transform-what [& result-column-transformers]
  [:what (reduce (fn [acc transformer]
            (fn [row]
              (assoc (acc row)
                     (:name transformer)
                     ((:func transformer) row))))
          (constantly {})
          result-column-transformers)])

(defn transform-where
  [& stuff]
  (match (vec stuff)
    [[:predicate column [:comparator c] value]]
    (let [comp-f (case c
                   "=" =
                   "!=" not=)]
      [:where (filter #(comp-f (get % column) value))])))

(def ^:private transform-map
  {:select transform-select
   :where transform-where
   :result-column transform-result-column
   :column str
   :null (constantly nil)
   :string edn/read-string
   :symbol edn/read-string
   :nat edn/read-string
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

  (let [query (parse "SELECT x, y FROM table WHERE table.x!=NULL LIMIT 2")]
    (execute query {'table '[{table/x 1 table/y 2 table/z 3 table/q 0}
                             {table/x 3 table/y 4 table/z 5 table/q 6}
                             {table/x 7 table/y 8 table/z 9 table/q 0}
                             {table/y 5 table/z 6 table/q 7}]}))

  {'table [{"x" 1 "y" 2 "z" 3 "q" 0}
           {"x" 4 "y" 5 "z" 6 "q" 7}]}

  (parse "SELECT test.x, y FROM table")

  (parse "SELECT test.x, y FROM table WHERE x=3 LIMIT 1")
  (parser "SELECT test.x, y FROM table WHERE x=3 LIMIT 1")
  (find-node :from [[:from 99]])
  (find-node :where (parser "SELECT test.x, y FROM table WHERE x=3 LIMIT 1"))

  (parse "SELECT test.x, y FROM table WHERE x=3")
  (parse "SELECT test.x, y FROM table WHERE x=3 LIMIT 1")
  (parse "SELECT test.x, y FROM table WHERE x=3 LIMIT 1")
  (parse "SELECT test.x, y FROM table WHERE x=NULL")

  (parser "SELECT test.x, y FROM table WHERE x=3")
  (parser "SELECT test.x, y FROM table WHERE x=3 LIMIT 1")

  )
