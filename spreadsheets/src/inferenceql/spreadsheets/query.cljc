(ns inferenceql.spreadsheets.query
  "This file defines functions for parsing, transforming, and executing IQL-SQL
  queries. The public API for this file are the functions are `q`, `pq`, and
  `query-plan`."
  #?(:clj (:require [inferenceql.spreadsheets.io :as sio])
     :cljs (:require-macros [inferenceql.spreadsheets.io :as sio]))
  (:require [clojure.edn :as edn]
            [datascript.core :as d]
            [instaparse.core :as insta]
            [metaprob.generative-functions :refer [gen]]
            [metaprob.prelude :as mp]
            [inferenceql.multimixture :as mmix]
            [inferenceql.multimixture.basic-queries :as queries]))

(def entity-var '?entity)

(defn constrain
  [gfn target constraints]
  (gen [& args]
    (let [trace (mmix/with-row-values {} constraints)]
      (-> (mp/infer-and-score :procedure gfn
                              :inputs args
                              :observation-trace trace)
          (first)
          (select-keys target)))))

;;; Hiccup tree manipulation

(defn node-type
  [node]
  (first node))

(defn children
  [node]
  (rest node))

(defn only-child
  [node]
  (assert (= (count node) 2))
  (second node))

(defn find-children
  [node k]
  (filter #(and (vector? %) ; so it works when called directly on a node vs via transform
                (= k (node-type %)))
          node))

(defn find-child
  "Returns a node of type `k` from a Hiccup-style instaparse parse tree."
  [node k]
  (first (find-children node k)))

(defn selector
  "Returns the selector node from a query parse tree."
  [ast]
  (some-> ast
          (find-child :selections)
          (children)))

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

;; Literals

(def literal-transformations
  {:string edn/read-string
   :symbol edn/read-string
   :nat    edn/read-string
   :float  edn/read-string
   :int    edn/read-string})

(def global-transformations
  (merge literal-transformations
         {:column-name keyword
          :model-name  keyword}))

(defn transform
  "Navigates a parse tree transforming literal nodes "
  [ast]
  (insta/transform global-transformations ast))

;; Selections

(defn transform-column-selection
  "Returns the `:find` and `:where` clauses for a `:column-selection` parse tree
  node as a map."
  [column]
  (let [col-sym (symbol (str "?" (name column)))]
    {:name [column]
     :find [col-sym]
     :where `[[(~'get-else ~'$ ~entity-var ~column :iql/no-value) ~col-sym]]}))

(defn transform-probability-of
  [target & more]
  (let [target (only-child target)
        model-name (or (some-> (find-child more :model)
                               (only-child))
                       :model)

        prob-gensym (gensym "prob")
        prob-name (keyword prob-gensym)
        prob-sym (symbol (str "?" prob-gensym))

        target-sym (genvar "target")
        target-clause (if (map? target)
                        `[(~'ground ~target) ~target-sym]
                        `[(datascript.core/pull ~'$ [~target] ~entity-var) ~target-sym])

        constraints-sym (genvar "constraints")
        conditions-clauses (let [constraints (some-> (find-child more :constraints)
                                                     (children))
                                 row-sym (genvar "row")
                                 column-events (mapv only-child (find-children constraints :column-event))
                                 row-clause (cond (find-child constraints :star)
                                                  `[(datascript.core/pull ~'$ ~'[*] ~entity-var) ~row-sym]

                                                  column-events
                                                  `[(datascript.core/pull ~'$ ~column-events ~entity-var) ~row-sym]

                                                  :else
                                                  `[(~'ground {}) ~row-sym])
                                 event-sym (genvar "events")
                                 events (reduce merge {} (filter map? constraints))
                                 event-clause `[(~'ground ~events) ~event-sym]]
                             [row-clause event-clause `[(merge ~row-sym ~event-sym) ~constraints-sym]])]
    {:name [prob-name]
     :find [prob-sym]
     :where (let [model-sym (genvar "model")]
              `[[(clojure.core/get ~'?models ~model-name) ~model-sym]
                ~target-clause
                ~@conditions-clauses
                [(~*logpdf-symbol* ~model-sym ~target-sym ~constraints-sym) ~prob-sym]])}))

(def selection-transformations
  (merge global-transformations
         {:column-selection transform-column-selection
          :probability-of transform-probability-of}))

(defn selection-clauses
  [ast]
  (merge-with into {:where [[entity-var :iql/type :iql.type/row]]}
              (if (find-child ast :star)
                {:find `[[(~'pull ~entity-var [~'*]) ~'...]]}
                (->> (children ast)
                     (map #(insta/transform selection-transformations %))
                     (apply merge-with into {:find [entity-var]})))))

;; Conditions

(def condition-transformations
  (merge global-transformations
         {:presence-condition (fn [c] [entity-var c '_])
          :absence-condition  (fn [c] `[(~'missing? ~'$ ~entity-var ~c)])

          :and-condition #(list 'and %1 %2)
          :or-condition  #(list 'or  %1 %2)

          :equality-condition  (fn [c v] [entity-var c v])

          :predicate symbol
          :predicate-condition (fn [c p v]
                                 (let [sym (genvar)]
                                   [[entity-var c sym]
                                    [(list p sym v)]]))}))

(defn condition-clauses
  "Given a conditions node, returns a sequence of Datalog clauses."
  [ast]
  (map #(insta/transform condition-transformations %)
       (children ast)))

;;; Generate

(defn model-lookup
  [models model-name]
  (if-let [model (get models model-name)]
    model
    (throw (ex-info "Invalid model name" {:name model-name}))))

(defn generate-target
  [ast]
  (-> ast
      (find-child :generated)
      (children)))

(defn generate-constraints
  [ast]
  (as-> ast $
    (find-child $ :bindings)
    (children $)
    (apply merge $)))

(defn generate-model
  [ast]
  (-> ast
      (find-child :model)
      (only-child)))

(defn generate-transformations
  "Returns the instaparse transformation map for the provided model registry (map
  from mode names to generative functions)."
  [models]
  {:model-lookup #(model-lookup models %)
   :model-generate #(constrain (generate-model %)
                               (generate-target %)
                               (generate-constraints %))
   :binding hash-map})

(defn generate
  [ast models limit]
  (if-not limit
    (throw (ex-info "Cannot GENERATE without LIMIT" {}))
    (let [ast (insta/transform (generate-transformations models) ast)
          target (generate-target ast)
          model (generate-model ast)
          constraints (generate-constraints ast)]
      (for [row (queries/simulate model constraints limit)]
        (select-keys row target)))))

;;; Parsing

(def parse
  "An instaparse parser for IQL SQL queries. The grammar is inlined at macro
  expansion time so that it can be used in the ClojureScript context where we
  don't have access to file resources."
  (insta/parser (sio/inline-resource "query.bnf")
                :string-ci true))

;;; Query execution

(defn query-plan
  "Given a query parse tree returns a query plan for the top-most query.
  Subqueries will not be considered and are handled in a different step by the
  interpreter. See `q` for details."
  [ast]
  (let [{sel-find :find sel-where :where} (selection-clauses (find-child ast :selections))
        cond-where                        (condition-clauses (find-child ast :conditions))]
    {:find sel-find
     :in '[$ ?models]
     :where `[~@sel-where ~@cond-where]}))

(defn iql-db
  "Converts a vector of maps into Datalog database that can be queried with `q`."
  [table]
  (->> table
       (map #(assoc % :iql/type :iql.type/row))
       (d/db-with (d/empty-db))))

(defn execute
  "Like `q`, only operates on a query parse tree rather than a query string."
  ([ast rows]
   (execute ast rows {}))
  ([ast rows models]
   (let [ast (insta/transform global-transformations ast)
         limit (some-> ast (find-child :limit) (only-child))
         names (-> ast (find-child :selections) (selection-clauses) (:name))
         db (iql-db (if-let [source (some-> ast
                                            (find-child :source)
                                            (children)
                                            (first))]
                      (case (node-type source)
                        :query (execute source rows)
                        :generate (generate source models limit))
                      rows))
         query (query-plan ast)
         rows (cond->> (d/q query db models)
                names (map #(zipmap (into [:db/id] names)
                                    %))
                true (sort-by :db/id)
                true (map #(->> %
                                (remove (comp #{:iql/no-value} val))
                                (into {})))
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
   (let [parse-tree (parse query)]
     (if-not (insta/failure? parse-tree)
       (execute parse-tree rows models)
       parse-tree))))
