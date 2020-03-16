(ns inferenceql.spreadsheets.query
  "This file defines functions for parsing, transforming, and executing IQL SQL
  queries. The public API for this file is the functions are `q`, `pq`, and
  `query-plan`."
  #?(:clj (:require [inferenceql.spreadsheets.io :as sio])
     :cljs (:require-macros [inferenceql.spreadsheets.io :as sio]))
  (:require [clojure.edn :as edn]
            [clojure.pprint :as pprint]
            [datascript.core :as d]
            [instaparse.core :as insta]
            [inferenceql.multimixture.basic-queries]))

(def parser (insta/parser (sio/inline-resource "query.bnf")))

(def logpdf-sym 'inferenceql.multimixture.basic-queries/logpdf)

(defn genvar
  "Generates a fresh variable symbol for use in Datalog queries."
  ([]
   (symbol (str "?" (gensym))))
  ([prefix-string]
   (symbol (str "?" (gensym prefix-string)))))

;;; Transformation functions

(defn transform-selections
  [& selections]
  (if (= '(*) selections)
    {:find '[[(pull ?e [*]) ...]]
     :where []}
    {:name (map :name selections)
     :find (into '[?e] (map :find-sym) selections)
     :where (mapcat :where-clauses selections)}))

(defn transform-probability-of
  [target constraints model-name]
  (let [prob-gensym (gensym "prob")
        prob-name (keyword prob-gensym)
        prob-sym (symbol (str "?" prob-gensym))
        target-sym (genvar "target")
        target-clause (if (map? target)
                        `[(~'ground ~target) ~target-sym]
                        `[(datascript.core/pull ~'$ [~target] ~'?e) ~target-sym])
        constraints-sym (genvar "constraints")
        conditions-clauses (let [row-sym (genvar "row")
                                 row-clause (if-let [columns (or (some #{'*} constraints)
                                                                 (seq (filter keyword? constraints)))]
                                              `[(datascript.core/pull ~'$ ~(vec columns) ~'?e) ~row-sym]
                                              `[(~'ground {}) ~row-sym])
                                 event-sym (genvar "events")
                                 events (reduce merge {} (filter map? constraints))
                                 event-clause `[(~'ground ~events) ~event-sym]]
                             [row-clause event-clause `[(merge ~row-sym ~event-sym) ~constraints-sym]])]
    {:name prob-name
     :find-sym prob-sym
     :where-clauses (let [model-sym (genvar "model")]
                      `[[(clojure.core/get ~'?models ~model-name) ~model-sym]
                        ~target-clause
                        ~@conditions-clauses
                        [(~logpdf-sym ~model-sym ~target-sym ~constraints-sym) ~prob-sym]])}))

(defn transform-column-selection
  [column]
  (let [col-sym (symbol (str "?" (name column)))]
    {:name column
     :find-sym col-sym
     :where-clauses `[[~'?e ~column ~col-sym]]}))

(def transform-map
  "A map of transformations to be performed on nodes in the query parse tree. Used
  with `instaparse.core/transform` by `query-plan`. Nodes are transformed by
  `transform` in a depth-first manner."
  {:star        (constantly '*)
   :column-name keyword
   :model-name  keyword
   :predicate   symbol

   :presence-condition  (fn [c]     ['?e c '_])
   :absence-condition   (fn [c]     [(list 'missing? '$ '?e c)])
   :and-condition       (fn [c1 c2] (list 'and c1 c2))
   :or-condition        (fn [c1 c2] (list 'or c1 c2))
   :equality-condition  (fn [c v]   ['?e c v])
   :predicate-condition (fn [c p v]
                          (let [sym (genvar)]
                            [['?e c sym]
                             [(list p sym v)]]))

   :selections transform-selections
   :column-selection transform-column-selection
   :probability-of transform-probability-of
   :constraints vector
   :binding hash-map

   :symbol edn/read-string
   :nat    edn/read-string
   :float  edn/read-string
   :int    edn/read-string
   :string edn/read-string})

(defn parse
  "Parses a string containing an InferenceQL query and produces a map representing
  the query to be performed."
  [& args]
  (let [ast (apply parser args)]
    (insta/transform transform-map ast)))

(defn find-child
  "Returns a node of type `k` from a Hiccup-style Instaparse parse tree."
  [node k]
  (->> (rest node)
       (filter vector?)
       (filter #(= k (first %)))
       (first)))

(defn subquery
  "Returns the subquery node from a query parse tree."
  [ast]
  (some-> ast
          (find-child :source)
          (find-child :query)))

(defn selector
  "Returns the selector node from a query parse tree."
  [ast]
  (some-> ast
          (find-child :selections)
          (rest)))

(defn conditions
  "Returns the conditions node from a query parse tree."
  [ast]
  (some-> ast
          (find-child :conditions)
          (rest)))

(defn iql-db
  "Converts a vector of maps into a IQL database."
  [table]
  (->> table
       (map #(assoc % :iql/type :iql.type/row))
       (d/db-with (d/empty-db))))

(defn query-plan
  "Given a query parse tree returns a query plan for the top-most query.
  Subqueries will not be considered and are handled in a different step by the
  interpreter. See `q` for details."
  [ast]
  (let [{find-syms :find find-wheres :where} (second ast)]
    {:find find-syms
     :in '[$ ?models]
     :where `[[~'?e :iql/type :iql.type/row]
              ~@find-wheres
              ~@(conditions ast)]}))

(defn execute
  "Like `q`, only operates on a query parse tree rather than a query string."
  ([ast rows]
   (execute ast rows {}))
  ([ast rows models]
   (let [{names :name} (second ast)
         db (iql-db (if-let [subquery (subquery ast)]
                      (execute subquery rows)
                      rows))
         query (query-plan ast)
         rows (cond->> (d/q query db models)
                names (map #(zipmap names (rest %)))
                true (map #(dissoc % :db/id :iql/type)))
         metadata {:iql/columns (or names (into [] (comp (mapcat keys) (distinct)) rows))}]
     (with-meta rows metadata))))

(defn q
  "Returns the result of executing a query on a set of rows. A registry
  mapping model names to model values models can be provided as an optional
  third argument."
  ([query rows]
   (q query rows {}))
  ([query rows models]
   (let [parse-tree (parse query)]
     (if-not (insta/failure? parse-tree)
       (execute parse-tree rows models)
       parse-tree))))

(defn pq
  "Like `q`, only pretty-prints the resulting table if any rows are returned."
  [& args]
  (let [rows (apply q args)
        columns (:iql/columns (meta rows))]
    (if (insta/failure? rows)
      rows
      (pprint/print-table
       (map name columns)
       (for [row rows]
         (reduce-kv (fn [m k v]
                      (assoc m (name k) v))
                    {}
                    row))))))

(comment

  (def db
    [{:name "cat" :sciname "Felis catus"                   }
     {:name "dog" :sciname "Canis lupus familiaris" :id 2  }
     {:name "bird"                                  :id 19}])

  (pq "SELECT name FROM data" db)
  (pq "SELECT * FROM data" db)
  (pq "SELECT * FROM data WHERE sciname IS NULL" db)
  (pq "SELECT * FROM data WHERE sciname IS NOT NULL" db)
  (pq "SELECT * FROM data WHERE id=19" db)
  (pq "SELECT sciname FROM data WHERE name=\"cat\"" db)
  (q "SELECT * FROM (SELECT * FROM data)" db)

  (pq "SELECT name FROM (SELECT name FROM (SELECT name FROM data))" db)
  (pq "SELECT * FROM (SELECT name, sciname FROM data)" db)
  (pq "SELECT name FROM (SELECT sciname FROM data)" db)

  (require '[inferenceql.multimixture :as mmix])
  (require '[clojure.walk :as walk])
  (edn/read-string (clojure.core/slurp "/Users/zane/Downloads/model.edn"))
  (def spec
    (walk/postwalk-replace
     {"elephant" :elephant
      "rain" :rain
      "teacher_sick" :teacher_sick
      "student_happy" :student_happy}
     (edn/read-string (clojure.core/slurp "/Users/zane/Downloads/model.edn"))))
  (def model (mmix/row-generator spec))
  (def models {:model model})

  (pprint/print-table
   (take 10 (repeatedly model)))

  (require '[clojure.java.io :as io])
  (require '[clojure.data.csv :as csv])
  (def db
    (clojure.core/with-open [reader (io/reader "/Users/zane/Downloads/data.csv")]
      (let [data (csv/read-csv reader)
            headers (map keyword (first data))
            rows (rest data)]
        (mapv #(zipmap headers %)
              rows))))

  (pq "SELECT * FROM data" db models)
  (pq "SELECT rain, (PROBABILITY OF elephant GIVEN rain UNDER model) FROM data" db models)
  (query-plan (parse "SELECT rain, (PROBABILITY OF elephant GIVEN rain=\"no\" UNDER model) FROM data WHERE rain=\"yes\"") )
  (pq "SELECT rain, (PROBABILITY OF rain=\"no\" GIVEN rain=\"no\" UNDER model) FROM data WHERE rain=\"no\"" db models)

  )
