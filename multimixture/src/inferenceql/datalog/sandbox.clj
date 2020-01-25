(ns inferenceql.datalog.sandbox
  (:require [clojure.core.match :as match]
            [clojure.set :as set]
            [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [datahike.api :as d]
            [expound.alpha :refer [expound]]))

;; https://docs.datomic.com/on-prem/query.html

(defn variable?
  [s]
  (and (symbol? s)
       (-> (name s) (string/starts-with? "?"))))

(defn src-var?
  [s]
  (and (symbol? s)
       (-> (name s) (string/starts-with? "$"))))

(defn rules-var?
  [s]
  (= '% s))

(def plain-symbol?
  (every-pred symbol?
              (complement (some-fn variable? src-var? rules-var?))))

(defn find-form
  [query]
  (->> query
       (drop-while (complement #{:find}))
       (take-while (complement #{:in :where}))
       (rest)))

(defn in-form
  [query]
  (->> query
       (drop-while (complement #{:in}))
       (take-while (complement #{:where}))
       (rest)))

(defn where-form
  [query]
  (->> query
       (drop-while (complement #{:where}))
       (rest)))

(s/def ::constant (complement (some-fn variable? symbol? list?)))

(s/def ::fn-arg
  (s/or :variable variable?
        :underscore #{'_}
        :constant (complement variable?)))

(s/def ::find-elem
  (s/or :variable variable?
        :pull-expr (s/cat :pull #{'pull}
                          :variable variable?
                          :pattern any?) ; TODO
        :aggregate (s/cat :aggregate-fn-name (s/and symbol? (complement variable?))
                          :fn-args (s/+ ::fn-arg))))

(s/def ::find-spec
  (s/or :find-rel    (s/tuple ::find-elem)
        :find-coll   (s/tuple (s/tuple ::find-elem #{'...}))
        :find-tuple  (s/tuple (s/coll-of ::find-elem))
        :find-scalar (s/tuple ::find-elem #{'.})))

(defn find-variables
  [form]
  (let [find-elems (match/match (s/conform ::find-spec form)
                     [:find-rel find-elems]          find-elems
                     [:find-coll [[find-elem '...]]] [find-elem]
                     [:find-tuple [find-elems]]      find-elems
                     [:find-scalar [find-elem '.]]   [find-elem])]
    (mapcat (fn find-elem-variables [find-elem]
              (match/match find-elem
                [:variable variable]              [variable]
                [:pull-expr {:variable variable}] [variable]
                [:aggregate {:fn-args fn-args}] (->> fn-args
                                                     (filter #(= :variable (first %)))
                                                     (mapv second))))
            find-elems)))

(s/def ::binding
  (s/or :variable variable?
        :bind-tuple (s/coll-of (s/or :variable variable? :underscore #{'_}))
        :bind-coll (s/cat :variable variable? :ellipsis #{'...})
        :bind-rel (s/tuple (s/+ (s/or :variable variable? :underscore #{'_}) ))))

(defn binding?
  [x]
  (s/valid? ::binding x))

(defn input-variables
  [form]
  (if (s/valid? ::binding form)
    (match/match (s/conform ::binding form)
      [:variable variable]              [variable]
      [:bind-tuple _]                   (filter variable? form)
      [:bind-coll {:variable variable}] [variable]
      [:bind-rel _]                     (filter variable? (first form)))
    ;; Otherwise it's a src-var, pattern-name, rules-var.
    form))

(s/def ::expression-clause
  (s/or :data-pattern (s/cat :src-var (s/? src-var?)
                             :components (s/+ (s/or :variable variable?
                                                    :underscore #{'_}
                                                    :constant ::constant)))
        :pred-expr (s/tuple (s/cat :pred symbol? :fn-args (s/+ ::fn-arg)))
        :fn-expr (s/tuple (s/cat :pred symbol? :fn-args (s/+ ::fn-arg))
                          variable?)
        :rule-expr (s/cat :src-var (s/? src-var?)
                          :rule-name (every-pred symbol? (comp nil? namespace))
                          :rule-args (s/+ (s/or :variable variable?
                                                :constant ::constant
                                                :underscore #{'_})))))

(s/conform ::expression-clause '[(even? ?e)])
(s/conform ::expression-clause '[(inc ?x) ?y])
(s/conform ::expression-clause '(row ?x _ ?z))

(defn expression-clause-variables
  [clause]
  (let [[kind {:keys [src-var]}] (s/conform ::expression-clause clause)]
    (case kind
      :data-pattern (concat (when src-var [src-var])
                            (filter variable? clause)))))

(s/def ::where-clause
  (s/or :not-clause (s/cat :src-var (s/? src-var?)
                           :not #{'not}
                           :clauses (s/+ ::where-clause))
        :not-join-clause (s/cat :src-var (s/? src-var?)
                                :not-join #{'not-join}
                                :variables (s/coll-of variable?)
                                :clauses (s/+ ::where-clause))
        :or-clause (s/cat :src-var (s/? src-var?)
                          :or #{'or}
                          :clauses (s/+ (s/or :clause ::where-clause
                                              :and-clause (s/spec (s/cat :and #{'and}
                                                                         :clauses (s/+ ::where-clause))))))
        :or-join-clause (s/cat :src-var (s/? src-var?)
                               :or #{'or-join}
                               :rule-vars (s/coll-of variable?) ; TODO
                               :clauses (s/+ (s/or :clause ::where-clause
                                                   :and-clause (s/spec (s/cat :and #{'and}
                                                                              :clauses (s/+ ::where-clause))))))
        :expression-clause ::expression-clause))

(defn where-variables
  [clause]
  (let [[kind {:keys [src-var clauses variables]}] (s/conform ::where-clause clause)]
    (condp contains? kind
      #{:not-clause :not-join-clause}
      (let [subclause-variables (->> clauses
                                     (map #(s/unform ::where-clause %))
                                     (mapcat where-variables))]
        (concat (when src-var
                  [src-var])
                variables
                subclause-variables))

      #{:expression-clause} (expression-clause-variables clause))
    ;; :or-clause
    ;; :or-join-clause
    ;; :expression-clause
    ))

(where-variables '(not [?e :cat/name "Henry"]))
(where-variables '($ not [?e :cat/name "Henry"]))
(where-variables '(not-join [?x ?y ?z] [?e :cat/name "Henry"]))
(where-variables '($ not-join [?x ?y ?z] [?e :cat/name "Henry"]))

(s/conform ::where-clause '(not [?e :cat/name "Henry"]))
(s/conform ::where-clause '($ not [?e :cat/name "Henry"]))
(s/conform ::where-clause '(not-join [?x ?y ?z] [?e :cat/name "Henry"]))
(s/conform ::where-clause '($ not-join [?x ?y ?z] [?e :cat/name "Henry"]))

(s/conform ::where-clause '(or [?e :cat/name "Henry"]))
;; => [:or-clause {:or or, :clauses [[:clause [:expression-clause [:data-pattern {:components [[:variable ?e] [:constant :cat/name] [:constant "Henry"]]}]]]]}]
(s/conform ::where-clause '(or clause (and clause clause)))
(s/conform ::where-clause '(or-join [?x ?y ?z] [?e :cat/name "Henry"]))
(s/conform ::where-clause '(or-join [?x ?y ?z] clause (and clause clause)))
(s/conform ::where-clause '(or [?e :cat/name "Henry"]
                               [?e :cat/color :orange]))

(def db
  [[0 :cat/name "Henry"]
   [0 :cat/color :orange]
   [0 :cat/mood :hungry]
   [1 :cat/name "Zelda"]
   [1 :cat/color :black]
   [1 :cat/mood :brooding]
   [2 :cat/name "Disco"]
   [2 :cat/color :brown]
   [2 :cat/mood :purring]])

(def query
  '[:find ?name
    :in $ ?color
    :where
    [?e :cat/name ?name]
    [?e :cat/color ?color]])

(defn free-symbols
  [form]
  (->> (tree-seq sequential? seq form)
       (filter symbol?)
       (distinct)))

(defn free-variables
  [form]
  (filter variable? (free-symbols form)))

(defn free-src-vars
  [form]
  (filter src-var? (free-symbols form)))

(defn group-where
  [where-form]
  (map vector where-form))

#_(-> (reduce (fn [{:keys [seen-vars] :as acc} clauses]
                (let [where-vars (filter variable? (free-variables clauses))
                      new-seen-vars (->> (free-variables clauses)
                                         (filter variable?)
                                         (concat seen-vars)
                                         (distinct))
                      next-query `[:find ~@new-seen-vars
                                   :in ~@src-vars [[~@seen-vars]]
                                   :where ~@clauses]]
                  (-> acc
                      (assoc :seen-vars new-seen-vars)
                      (update :plan conj next-query))))
              {:plan `[[:find ~@in-variables
                        :in ~@(in-form query)]]
               :seen-vars in-variables}
              clause-groups)
      (:plan)
      (conj `[:find ~@(find-form query)
              :in [[~@(free-variables (find-form query))]]]))

#_
(defn query-plan
  [query]
  (let [src-vars (free-src-vars (in-form query))
        clause-groups (group-where (where-form query))
        seen-vars (reductions (comp distinct concat)
                              (free-variables (in-form query))
                              (map free-variables clause-groups))]
    (map (fn [seen-vars clauses]
           `[:find ~@(distinct (concat seen-vars (free-variables clauses)))
             :in ~@src-vars [[~@seen-vars]]
             :where ~@clauses])
         seen-vars
         clause-groups)))

#_(query-plan query)

(defn sources
  [query & inputs]
  (->> (zipmap (or (seq (in-form query))
                   ['$]) inputs)
       (filter (comp src-var? key))
       (map val)))

(defn in-relation
  [in-form inputs]
  (let [variables (free-variables in-form)]
    {:variables ~@variables
     :relation (apply d/q `[:find ~@variables
                            :in ~@in-form]
                      inputs)}))

(defn datalog?
  [x]
  true)

(defn not-datalog?
  [x]
  (not (datalog? x)))

(defn filter-keys
  [m f]
  (->> m
       (filter (comp f key))
       (into (empty m))))

(defn partition-when
  "Partitions the items in coll into a sequences of items for which (pred item)
  returns logical true and individual items for which (pred item) does not
  return logical true. Returns a stateful transducer when no collection is
  provided."
  ([pred]
   (comp (partition-by pred)
         (mapcat #(if (pred (first %))
                    (list %)
                    %))))
  ([pred coll]
   (sequence (partition-when pred) coll)))


(set! *print-length* 10)

(partition-when even? [1 1 1 2 2 3 3 3])
(into [] (partition-when even?) [1 1 1 2 2 3 3 3])
(partition-when odd? [1 1 1 2 2 3 3 3])

(defn execute
  [query & inputs]
  (let [in-form (in-form query)
        where-form (where-form query)
        relation-syms (reductions (comp distinct concat)
                                  (vec (free-variables in-form))
                                  (map free-variables where-form))

        {bindings true non-bindings false} (->> (map vector in-form inputs)
                                                (group-by (comp binding? first)))
        binding-forms (map first bindings)
        non-binding-forms (mapv first non-bindings)
        non-binding-inputs (mapv second non-bindings)

        middle-steps (loop [relation-syms (free-variables in-form)
                            form-or-forms (partition-when datalog? where-form)]
                       (if (not-datalog? form-or-forms)
                         (ex-info "Not implemented." {'x x})
                         (when-let [form-or-forms (first items)]
                           (let [relation-syms-after (distinct (concat relation-syms (free-variables form-or-forms)))]
                             (lazy-seq (cons `[:datalog [:find ~@relation-syms-after
                                                         :in ~@non-binding-forms [[~@relation-syms]]
                                                         :where ~@form-or-forms]]
                                             (recur relation-syms-after
                                                    (rest items))))))))
        last-step `[:find ~@(find-form query)
                    :in ~@non-binding-forms [[~@(last relation-syms)]]]
        first-relation (apply d/q
                              `[:find ~@(free-variables binding-forms)
                                :in ~@in-form]
                              inputs)
        last-relation (reduce (fn [relation step]
                                (match/match step
                                  [:datalog query] (let [inputs (conj non-binding-inputs relation)]
                                                     (apply d/q query inputs))))
                              first-relation
                              middle-steps)]
    (apply d/q last-step (conj non-binding-inputs last-relation))))

#_(execute '[:find ?name .
             :in $ [?color ...]
             :where
             [?e :cat/name ?name]
             [?e :cat/color ?color]]
           db
           [:brown])

#_(require 'hashp.core)

#_(query-plan query)
