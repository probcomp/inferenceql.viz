(ns inferenceql.spreadsheets.query
  "This file defines functions for parsing, transforming, and executing IQL-SQL
  queries. The public API for this file are the functions are `q`, `pq`, and
  `query-plan`."
  #?(:clj (:require [inferenceql.spreadsheets.io :as sio])
     :cljs (:require-macros [inferenceql.spreadsheets.io :as sio]))
  (:require [clojure.edn :as edn]
            [datascript.core :as d]
            [instaparse.core :as insta]
            [inferenceql.multimixture.basic-queries :as queries]))

;;; Parsing and transformation

(def ^:dynamic *logpdf-symbol*
  "A dynamic variable storing the symbol of the function to be used for
  calculating a model's log probability density function.."
  'inferenceql.multimixture.basic-queries/logpdf)

(defn genvar
  "Generates a fresh variable for use in Datalog queries."
  ([]
   (symbol (str "?" (gensym))))
  ([prefix-string]
   (symbol (str "?" (gensym prefix-string)))))

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
                        [(~*logpdf-symbol* ~model-sym ~target-sym ~constraints-sym) ~prob-sym]])}))

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

   :generated vector
   :constraints vector

   :bindings merge
   :binding hash-map

   :symbol edn/read-string
   :nat    edn/read-string
   :float  edn/read-string
   :int    edn/read-string
   :string edn/read-string})

(def parse
  "An instaparse parser for IQL SQL queries. The grammar is inlined at macro
  expansion time so that it can be used in the ClojureScript context where we
  don't have access to file resources."
  (insta/parser (sio/inline-resource "query.bnf")
                :string-ci true))

(defn parse-and-transform
  "Parses and transforms a string containing an InferenceQL query and produces a
  map representing the query to be performed."
  [& args]
  (let [ast (apply parse args)]
    (insta/transform transform-map ast)))

;;; Hiccup tree manipulation

(defn find-child
  "Returns a node of type `k` from a Hiccup-style instaparse parse tree."
  [node k]
  (->> (rest node)
       (filter vector?)
       (filter #(= k (first %)))
       (first)))

(defn source
  "Returns the source node from a query parse tree."
  [ast]
  (some-> ast
          (find-child :source)
          (second)))

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

(defn limit
  "Returns the limit value from a query parse tree."
  [ast]
  (some-> ast
          (find-child :limit)
          (rest)
          (first)))

;;; Query execution

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

(defn iql-db
  "Converts a vector of maps into Datalog database that can be queried with `q`."
  [table]
  (->> table
       (map #(assoc % :iql/type :iql.type/row))
       (d/db-with (d/empty-db))))

(defn generate
  [ast models limit]
  (if-not limit
    (throw (ex-info "Cannot GENERATE without LIMIT" {}))
    (let [target (second ast)
          model-name (last ast)
          constraints (if (= 4 (count ast)) ; constraints are optional
                        (nth ast 2)
                        {})]
      (if-let [model (get models model-name)]
        (for [row (queries/simulate model constraints limit)]
          (select-keys row target))
        (throw (ex-info "Invalid model name" {:name model-name}))))))

(defn execute
  "Like `q`, only operates on a query parse tree rather than a query string."
  ([ast rows]
   (execute ast rows {}))
  ([ast rows models]
   (let [limit (limit ast)
         {names :name} (second ast)
         db (iql-db (if-let [source (source ast)]
                      (case (first source)
                        :query (execute source rows)
                        :generate (generate source models limit))
                      rows))
         query (query-plan ast)
         rows (cond->> (d/q query db models)
                names (map #(zipmap names (rest %)))
                true (sort-by :db/id)
                true (map #(dissoc % :db/id :iql/type))
                limit (take limit))
         metadata {:iql/columns (or names (into [] (comp (mapcat keys) (distinct)) rows))}]
     (with-meta rows metadata))))

(defn q
  "Returns the result of executing a query on a set of rows. A registry
  mapping model names to model values models can be provided as an optional
  third argument."
  ([query rows]
   (q query rows {}))
  ([query rows models]
   (let [parse-tree (parse-and-transform query)]
     (if-not (insta/failure? parse-tree)
       (execute parse-tree rows models)
       parse-tree))))
