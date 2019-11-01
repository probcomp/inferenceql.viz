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
    [xform [:from [:table table]]]
    {:what xform
     :table table}))

(defn- transform-what
  [& columns]
  (map #(select-keys % columns)))

(def ^:private transform-map
  {:select transform-select
   :what transform-what
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
  [{:keys [what table] :as query} env]
  (into [] what (get env table)))

(comment

  (clojure.repl/doc into)

  ;; SELECT a, b, c
  (into []
        (map #(select-keys % ["a" "b" "c"]))
        [{"a" 1 "b" 2 "c" 3 "d" 4}
         {"a" 1 "b" 2 "c" 3 "d" 4}
         {"a" 1 "b" 2 "c" 3 "d" 4}
         {"a" 1 "b" 2 "c" 3 "d" 4}])

  (let [xform (comp (map inc)
                    (remove even?))]
    (into [] xform (range 10)))

  (parse "hi" :start :column)

  (edn/read-string "hi")

  (let [query (parse "SELECT \"x\", y, z FROM table")]
    (execute query {'table [{"x" 1 "y" 2 "z" 3 "q" 0}]}))

  (parse "SELECT hi" :start :select)
  (parse "SELECT *, hi")

  )
