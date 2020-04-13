(ns inferenceql.spreadsheets.query
  "This file defines functions for parsing, transforming, and executing IQL-SQL
  queries. The public API for this file is the functions are `q`, `pq`, and
  `query-plan`."
  #?(:clj (:require [inferenceql.spreadsheets.io :as sio])
     :cljs (:require-macros [inferenceql.spreadsheets.io :as sio]))
  (:require [clojure.edn :as edn]
            [clojure.spec.alpha :as s]
            [clojure.walk :as walk]
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

(def default-environment
  {'clojure.core/merge clojure.core/merge
   'datascript.core/pull datascript.core/pull
   'inferenceql.multimixture.basic-queries/logpdf inferenceql.multimixture.basic-queries/logpdf

   'clojure.core/=  =
   'clojure.core/>  >
   'clojure.core/>= >=
   'clojure.core/<  <
   'clojrue.core/<= <=})

(def input-symbols
  {clojure.core/merge '?merge
   datascript.core/pull '?pull
   inferenceql.multimixture.basic-queries/logpdf '?logpdf

   =  '?=
   >  '?>
   >= '?>=
   <  '?<
   <= '?<=})

;;; Parsing and transformation

(defn genvar
  "Generates a fresh variable for use in Datalog queries."
  ([]
   (symbol (str "?" (gensym))))
  ([prefix-string]
   (symbol (str "?" (gensym prefix-string)))))

;; Transformation

(defn as-map
  [ks]
  (fn [& children]
    (let [[pos-children kw-children] (split-at (count ks) children)
          pos-map (zipmap ks pos-children)
          kw-map (->> kw-children
                      (map (fn [node]
                             (if-let [k (::key (meta node))]
                               {k node}
                               (let [[k & vs] node]
                                 {k (or (when (coll? vs)
                                          (if (= 1 (count vs))
                                            (first vs)
                                            (vec vs)))
                                        true)}))))
                      (reduce merge))]
      (->> (merge pos-map kw-map)
           (into {})))))

(defn meta-transform
  [transform-map parse-tree]
  (let [meta-preserving-map (reduce-kv (fn [m k f]
                                         (assoc m k (comp #(with-meta % {::key k})
                                                          f)))
                                       {}
                                       transform-map)]
    (insta/transform meta-preserving-map parse-tree)))

(def literal-transformations
  {:string edn/read-string
   :symbol edn/read-string
   :nat    edn/read-string
   :float  edn/read-string
   :int    edn/read-string})

(defn transform
  [parse-tree]
  (->> parse-tree
       (insta/transform literal-transformations)
       (insta/transform {:name       keyword
                         :predicate  symbol
                         :star       (constantly :star)
                         :ascending  (constantly :ascending)
                         :descending (constantly :descending)})
       (meta-transform (->> {:query            (as-map [:selections])
                             :probability-of   (as-map [:target])
                             :column-selection (as-map [:column])
                             :generate         (as-map [:target])
                             :ordering         (as-map [:column])
                             :model-lookup     (as-map [:model-name])

                             :selections     vector
                             :conditions     vector
                             :constraints    vector
                             :variable-names vector
                             :target         vector

                             :binding hash-map}))))

;; Selections

(defn probability-selection-clauses
  [{:keys [target constraints model selection-name]}]
  (let [selection-name (or selection-name (keyword (gensym "prob")))
        prob-var (symbol (str "?" (name selection-name)))

        model-var (genvar "model-")
        target-sym (genvar "target-")
        constraints-sym (genvar "constraints-")

        target-clauses (let [row-sym (genvar "target-row-events-")
                             row-events (filterv keyword? target)
                             row-clause (cond (= [:star] target)
                                              `[(d/pull ~'$ ~'[*] ~entity-var) ~row-sym]

                                              (seq row-events)
                                              `[(d/pull ~'$ ~row-events ~entity-var) ~row-sym]

                                              :else
                                              `[(~'ground {}) ~row-sym])

                             binding-sym (genvar "target-binding-events-")
                             binding-events (transduce (filter map?) merge {} target)

                             event-clause `[(~'ground ~binding-events) ~binding-sym]]
                         [row-clause event-clause `[(merge ~row-sym ~binding-sym) ~target-sym]])
        constraints-clauses (let [row-sym (genvar "constraint-row-events-")
                                  row-events (filterv keyword? constraints)
                                  row-clause (cond (= [:star] constraints)
                                                   `[(d/pull ~'$ ~'[*] ~entity-var) ~row-sym]

                                                   (seq row-events)
                                                   `[(d/pull ~'$ ~row-events ~entity-var) ~row-sym]

                                                   :else
                                                   `[(~'ground {}) ~row-sym])

                                  binding-sym (genvar "constraint-binding-events-")
                                  binding-events (transduce (filter map?) merge {} constraints)
                                  event-clause `[(~'ground ~binding-events) ~binding-sym]]
                              [row-clause event-clause `[(merge ~row-sym ~binding-sym) ~constraints-sym]])
        logpdf-clauses `[[(queries/logpdf ~model-var ~target-sym ~constraints-sym) ~prob-var]]]
    {:name   [selection-name]
     :find   [prob-var]
     :in     [model-var]
     :inputs [model]
     :where  (reduce into [target-clauses constraints-clauses logpdf-clauses])}))

(defn column-selection-clauses
  "Returns the `:find` and `:where` clauses for a `:column-selection` parse tree
  node as a map."
  [{:keys [column selection-name]}]
  (let [name (name (or selection-name column))
        variable (symbol (str "?" name))]
    {:name [(keyword name)]
     :find [variable]
     :where `[[(~'get-else ~'$ ~entity-var ~column :iql/no-value) ~variable]]}))

(s/def ::selection
  (s/or :star #{[:star]}
        :column-selection (s/keys :req-un [::column])
        :probability-selection (s/keys :req-un [::target])))

(defn selection-clauses
  [selection]
  (let [[tag selection] (s/conform ::selection selection)]
    (case tag
      :column-selection (column-selection-clauses selection)
      :probability-selection (probability-selection-clauses selection))))

(defn selections-clauses
  [selections]
  (merge-with into {:where [[entity-var :iql/type :iql.type/row]]}
              (if (= [:star] selections)
                {:find `[[(~'pull ~entity-var [~'*]) ~'...]]}
                (->> selections
                     (map selection-clauses)
                     (apply merge-with into {:find [entity-var]})))))

;;; Conditions

(def condition-transformations
  (merge literal-transformations
         {:presence-condition (fn [c] [[entity-var c '_]])
          :absence-condition  (fn [c] `[[(~'missing? ~'$ ~entity-var ~c)]])

          :and-condition (fn [cs1 cs2] `[(~'and ~@cs1 ~@cs2)])
          :or-condition (fn [cs1 cs2] `[(~'or ~@cs1 ~@cs2)])

          :equality-condition  (fn [c v] [[entity-var c v]])

          :predicate symbol
          :predicate-condition (fn [c p v]
                                 (let [sym (genvar)]
                                   [[entity-var c sym]
                                    [(list (symbol "clojure.core" (name p)) sym v)]]))}))

(defn conditions-clauses
  "Given a conditions node, returns a sequence of Datalog clauses."
  [conditions]
  (mapcat #(insta/transform condition-transformations %)
          conditions))

;;; Parsing

(def parse
  "An instaparse parser for IQL SQL queries. The grammar is inlined at macro
  expansion time so that it can be used in the ClojureScript context where we
  don't have access to file resources."
  (insta/parser (sio/inline-resource "query.bnf")
                :string-ci true))

;;; Query execution

(defn inputize
  [query-plan]
  (let [replaced-symbols (->> (select-keys (:query query-plan) [:find :where])
                              (tree-seq coll? seq)
                              (filter symbol?)
                              (distinct)
                              (filter (set (keys default-environment))))
        input-names (zipmap (keys default-environment)
                            (map input-symbols
                                 (vals default-environment)))]
    (-> query-plan
        (update :query #(walk/postwalk-replace input-names %))
        (update-in [:query :in] into (map input-names replaced-symbols))
        (update :inputs into (map (fn [sym] {:function-name sym})
                                  replaced-symbols)))))

(defn query-plan
  "Given a query parse tree returns a query plan for the top-most query.
  Subqueries will not be considered and are handled in a different step by the
  interpreter. See `q` for details."
  [{:keys [selections conditions]}]
  (let [{sel-find :find sel-in :in sel-where :where sel-inputs :inputs} (selections-clauses selections)
        cond-where (conditions-clauses conditions)]
    {:query {:find sel-find
             :in (into '[$] sel-in)
             :where (into sel-where cond-where)}
     :inputs sel-inputs}))

(defn iql-db
  "Converts a vector of maps into Datalog database that can be queried with `q`."
  [table]
  (->> table
       (map #(assoc % :iql/type :iql.type/row))
       (d/db-with (d/empty-db))))

(s/def ::selections any?)

(s/def ::target any?)

(s/def ::model-lookup (s/keys :req-un [::model-name]))

(s/def ::function-lookup (s/keys :req-un [::function-name]))

(s/def ::generate (s/keys :req-un [::target ::model]
                          :opt-un [::constraints]))

(s/def ::model
  (s/or :model-lookup ::model-lookup
        :generate ::generate))

(s/def ::source
  (s/or :data true?
        :generate ::generate
        :select (s/keys :req-un [::selections])))

(s/def ::model-name keyword?)

(s/def ::input
  (s/or :model-lookup ::model-lookup
        :function-lookup ::function-lookup
        :generate ::generate))

(defn input
  [x environment]
  (let [[tag _] (s/conform ::input x)]
    (case tag
      :model-lookup    (get environment (:model-name x))
      :function-lookup (get environment (:function-name x))
      :generate        (let [{:keys [model target constraints]} x]
                         (constrain (input model environment)
                                    (or target {})
                                    (or constraints {}))))))

(defn execute
  "Like `q`, only operates on a query parse tree rather than a query string."
  ([parse-map rows]
   (execute parse-map rows {}))
  ([{:keys [selections source ordering limit] :as parse-map} rows models]
   (let [environment (merge default-environment models)
         keyfn (get-in parse-map [:ordering :column] :db/id)
         cmp (case (get-in parse-map [:ordering :direction])
               :ascending compare
               :descending #(compare %2 %1)
               nil compare)
         names (-> selections (selections-clauses) (:name)) ; TODO: fix redundant call to selections-clauses
         db (iql-db (let [[tag _] (s/conform ::source source)]
                      (case tag
                        :data rows
                        :generate (if-not limit
                                    (throw (ex-info "Cannot SELECT from GENERATE without LIMIT" {}))
                                    (let [model (input source environment)]
                                      (repeatedly limit model)))
                        :query (execute source rows))))
         {query :query input-descs :inputs} (inputize (query-plan parse-map))
         inputs (map #(input % environment) input-descs)
         rows (cond->> (apply d/q query db inputs)
                names (map #(zipmap (into [:db/id] names) ; TODO: Can this not be hard-coded?
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
       (execute (transform parse-tree) rows models)
       parse-tree))))
