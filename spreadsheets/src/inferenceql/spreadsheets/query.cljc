(ns inferenceql.spreadsheets.query
  "This file defines functions for parsing, transforming, and executing IQL-SQL
  queries. The public API for this file are the functions are `q`, `pq`, and
  `query-plan`."
  #?(:clj (:require [inferenceql.spreadsheets.io :as sio])
     :cljs (:require-macros [inferenceql.spreadsheets.io :as sio]))
  (:require [clojure.edn :as edn]
            [datascript.core :as d]
            [instaparse.core :as insta]
            [metaprob.generative-functions :as g :refer [gen]]
            [metaprob.prelude :as mp]
            [inferenceql.multimixture :as mmix]
            [inferenceql.multimixture.basic-queries :as queries]))

(def entity-var '?entity)
(def default-model-key :model)

(defn constrain
  [gfn target constraints]
  (assert vector? target)
  (assert map? constraints)

  (let [constraint-trace (mmix/with-row-values {} constraints)]
    (g/make-generative-function
     (gen [& args]
       (-> (mp/infer-and-score :procedure gfn :inputs args :observation-trace constraint-trace)
           (first)
           (select-keys target)))
     (gen [partial-trace]
       (gen [& args]
         (let [infer #(mp/infer-and-score :procedure gfn :inputs args :observation-trace %)
               target-constraint-trace (merge-with merge constraint-trace partial-trace)

               score-denominator (if (empty? constraints)
                                   0
                                   (last (infer constraint-trace)))
               [val trace score-numerator] (infer target-constraint-trace)
               score (- score-numerator score-denominator)
               target-val (select-keys val target)]
           [target-val trace score]))))))

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
         {:selection-name keyword
          :column-name    keyword
          :model-name     keyword}))

(defn transform
  "Navigates a parse tree transforming literal nodes "
  [ast]
  (insta/transform global-transformations ast))

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
      (children)
      (vec)))

(defn generate-constraints
  [ast]
  (or (as-> ast $
        (find-child $ :bindings)
        (children $)
        (apply merge $))
      {}))

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
    (throw (ex-info "Cannot SELECT from GENERATE without LIMIT" {}))
    (let [ast (insta/transform (generate-transformations models) ast)
          target (generate-target ast)
          model (generate-model ast)
          constraints (generate-constraints ast)]
      (for [row (queries/simulate model constraints limit)]
        (select-keys row target)))))

;; Selections

(defn transform-column-selection
  "Returns the `:find` and `:where` clauses for a `:column-selection` parse tree
  node as a map."
  ([column]
   (transform-column-selection column nil))
  ([column name-node]
   (let [name (or (some-> name-node only-child)
                  column)
         variable (symbol (str "?" name))]
     {:name [(keyword name)]
      :find [variable]
      :where `[[(~'get-else ~'$ ~entity-var ~column :iql/no-value) ~variable]]})))

(defn transform-probability-of
  [& more]
  (let [model-var (genvar "model-")
        model (or (some-> more
                          (find-child :model)
                          (only-child))
                  default-model-key)

        selection-name (or (some-> more
                                   (find-child :as-clause)
                                   (only-child))
                           (gensym "prob"))
        prob-var (symbol (str "?" (name selection-name)))

        target-sym (genvar "target-")
        target-clauses (let [targets (-> more
                                         (find-child :target)
                                         (children))

                             row-sym (genvar "target-row-events-")
                             row-events (->> (find-children targets :column-event)
                                             (mapv only-child))
                             row-clause (cond (find-child targets :star)
                                              `[(datascript.core/pull ~'$ ~'[*] ~entity-var) ~row-sym]

                                              (seq row-events)
                                              `[(datascript.core/pull ~'$ ~row-events ~entity-var) ~row-sym]

                                              :else
                                              `[(~'ground {}) ~row-sym])

                             binding-sym (genvar "target-binding-events-")
                             binding-events (->> (find-children targets :binding-event)
                                                 (map #(insta/transform {:binding-event identity
                                                                         :binding hash-map}
                                                                        %))
                                                 (apply merge {}))

                             event-clause `[(~'ground ~binding-events) ~binding-sym]]
                         [row-clause event-clause `[(merge ~row-sym ~binding-sym) ~target-sym]])

        constraints-sym (genvar "constraints-")
        conditions-clauses (let [constraints (some-> more
                                                     (find-child :constraints)
                                                     (children))

                                 row-sym (genvar "constraint-row-events-")
                                 row-events (->> (find-children constraints :column-event)
                                                 (mapv only-child))
                                 row-clause (cond (find-child constraints :star)
                                                  `[(datascript.core/pull ~'$ ~'[*] ~entity-var) ~row-sym]

                                                  (seq row-events)
                                                  `[(datascript.core/pull ~'$ ~row-events ~entity-var) ~row-sym]

                                                  :else
                                                  `[(~'ground {}) ~row-sym])

                                 binding-sym (genvar "constraint-binding-events-")
                                 binding-events (->> (find-children constraints :binding-event)
                                                     (map #(insta/transform {:binding-event identity
                                                                             :binding hash-map}
                                                                            %))
                                                     (apply merge {}))
                                 event-clause `[(~'ground ~binding-events) ~binding-sym]]
                             [row-clause event-clause `[(merge ~row-sym ~binding-sym) ~constraints-sym]])
        logpdf-clauses `[[(~*logpdf-symbol* ~model-var ~target-sym ~constraints-sym) ~prob-var]]]
    {:name [selection-name]
     :find [prob-var]
     :in [model-var]
     :inputs [model]
     :where (reduce into [target-clauses
                          conditions-clauses
                          logpdf-clauses])}))

(defn selection-transformations
  [models]
  (merge global-transformations
         (generate-transformations models)
         {:column-selection transform-column-selection
          :probability-of transform-probability-of}))

(defn selection-clauses
  [ast models]
  (merge-with into {:where [[entity-var :iql/type :iql.type/row]]}
              (if (find-child ast :star)
                {:find `[[(~'pull ~entity-var [~'*]) ~'...]]}
                (->> (children ast)
                     (map #(insta/transform (selection-transformations models) %))
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
  ([ast]
   (query-plan ast {}))
  ([ast models]
   (let [{sel-find :find sel-in :in sel-where :where sel-inputs :inputs} (selection-clauses (find-child ast :selections) models)
         cond-where (condition-clauses (find-child ast :conditions))]
     {:query {:find sel-find
              :in (into '[$] sel-in)
              :where (into sel-where cond-where)}
      :inputs sel-inputs})))

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
         keyfn (or (some-> ast
                           (find-child :ordering)
                           (children)
                           (first))
                   :db/id)
         cmp (case (some-> ast
                           (find-child :ordering)
                           (find-child :direction)
                           (only-child)
                           (node-type))
               :ascending compare
               :descending #(compare %2 %1)
               nil compare)
         limit (some-> ast (find-child :limit) (only-child))
         ;; TODO: fix redundant call to selection-clauses
         names (-> ast (find-child :selections) (selection-clauses models) (:name))
         db (iql-db (if-let [source (some-> ast
                                            (find-child :source)
                                            (children)
                                            (first))]
                      (case (node-type source)
                        :query (execute source rows)
                        :generate (generate source models limit))
                      rows))
         {:keys [query inputs]} (query-plan ast models)
         rows (cond->> (apply d/q query db inputs)
                names (map #(zipmap (into [:db/id] names)
                                    %))
                true (sort-by keyfn cmp)
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
