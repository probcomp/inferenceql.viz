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

#_(s/conform ::expression-clause '[?e :cat/name "Henry"])
;; => [:data-pattern {:components [[:variable ?e] [:constant :cat/name] [:constant "Henry"]]}]
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

;; (not-clause | not-join-clause | or-clause | or-join-clause | expression-clause)

(comment

  (def test-query
    '[:find ?e ?c
      :in $
      :where
      [?e :cat/name "Henry"]
      [?e :cat/hungriness 9]
      [?e :cat/color ?c]])

  (def big-query
    '[:find [(pull ?e [:satellite/period :satellite/apogee :satellite/perigee]) ...]
      :in $ $models %
      :where
      (row ?e)

      [$models _ :iql.model/generative-function ?gfn]

      [(d/pull $ [:satellite/period] ?e) ?target]
      [(d/pull $ [:satellite/apogee :satellite/perigee] ?e) ?constraints]
      [(ground {}) ?no-constraints]

      [(iqldl.examples/marginal-logpdf ?gfn ?target ?no-constraints) ?nc-pdf]
      [(iqldl.examples/conditional-logpdf ?gfn ?target ?constraints) ?c-pdf]

      [(> ?nc-pdf ?c-pdf)]])

  (find-clause big-query)
  (in-clause big-query)
  (where-form big-query)

  (require '[clojure.walk :as walk])

  (defn special?
    [form]
    (and (list? form)
         (some? (namespace (first form)))))

  (special? '(not [?e :cat/name "Henry"]))
  (special? '(my/not '[?e :cat/name "Henry"]))

  #_(def query '[:find [(pull ?e [:satellite/period :satellite/apogee :satellite/perigee]) ...]
                 :in $ $models %
                 :where
                 (row ?e)

                 [$models _ :iql.model/generative-function ?gfn]

                 [(d/pull $ [:satellite/period] ?e) ?target]
                 [(d/pull $ [:satellite/apogee :satellite/perigee] ?e) ?constraints]
                 [(ground {}) ?no-constraints]

                 [(iqldl.examples/marginal-logpdf ?gfn ?target ?no-constraints) ?nc-pdf]
                 [(iqldl.examples/conditional-logpdf ?gfn ?target ?constraints) ?c-pdf]

                 [(> ?nc-pdf ?c-pdf)]])

  (let [query query]
    (->> (concat [[]] (map vector (where-form query)) [nil])
         (map (juxt identity free-variables))
         (map #(zipmap [:clauses :variables] %))
         (partition-all 3 1)
         (map #(zipmap [:prev-step :current-step :next-step] %))
         (fn [{:keys [prev-step current-step next-step]}]
           `[:find ~@(or (free-variables current-step))
             :in (free-variables )])))

  (let [query query
        clause-groups (map vector (where-form query))
        free-variables (map free-variables clause-groups)
        providable (reductions into #{} free-variables)]
    (map #(apply zipmap [:clauses ] %)
         clause-groups))

  )


#_(let [query  query
        where  (map vector (where-form query))
        free   (mapv free-variables where)
        seen   (rest (reductions into (set (in-clause query)) free))
        in     (mapv (comp #(conj % '$) set/intersection)
                     seen
                     free
                     (repeat (free-variables (in-clause query))))
        needed (into [#{}]
                     (vec (reverse (reductions into
                                               (free-variables (find-clause query))
                                               free))))
        find   (mapv set/intersection needed free)]
    (map (fn [where free seen in needed find]
           {:where where
            :free free
            :seen seen
            :in in
            :needed needed
            :find find}
           `[:find ~@find
             :in ~@in
             :where ~@where])
         where free seen in needed find))

#_(let [query  query
        where  (map vector (where-form query))
        free   (mapv (comp  free-variables) where)
        seen   (rest (reductions into
                                 (set (in-clause query))
                                 free))
        in     (mapv set/intersection
                     seen
                     free)
        needed (reverse (reductions into
                                    (free-variables (find-clause query))
                                    (reverse free)))
        find   (mapv (comp set
                           (partial filter variable?)
                           set/intersection)
                     needed free)]
    (map (fn [where free seen in needed find]
           {:where  where
            :free   free
            ;; :seen   seen
            ;; :in     in
            :needed needed
            :find   find
            }
           {:w where :n needed}
           `[:find ~@find
             :in ~@in
             :where ~@where])
         where free seen in needed find))

(defn free-variables
  [form]
  (->> (tree-seq sequential? seq form)
       (filter (some-fn src-var? rules-var? variable?))
       (set)))

#_(free-variables query)

(defn available-variables
  "Variables that are available from previous clause groups in the query."
  [query]
  (butlast
   (reductions into
               (free-variables (in-form query))
               (map free-variables (where-form query)))))

#_(available-variables '[:find ?e ?color
                         :in ?waffles
                         :where
                         [?e :cat/name ?name]
                         [?e :cat/color ?color]
                         [?e :cat/mood ?mood]
                         [?e :cat/name "Henry"]])

(defn needed-variables
  "Variables that are needed in later clause groups."
  [query]
  (let [find-variables (free-variables (find-form query))]
    (->> (where-form query)
         (map free-variables)
         (reverse)
         (concat [find-variables])
         (butlast)
         (reductions into #{})
         (reverse))))

#_(needed-variables query)

#_(where-form query)

#_(map vector
       (where-form query)
       (needed-variables query))

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
  '[:find (pull ?e [*])
    :in $
    :where
    [?e :cat/name ?name]
    [?e :cat/color ?color]
    [?e :cat/mood ?mood]
    [?e :cat/name "Henry"]])

(defn query-plan
  [query]
  (let [clauses (map vector (where-form query))]
    (-> (map (fn [clauses available-variables needed-variables]
               (let [clause-variables (into #{}
                                            (filter variable?)
                                            (free-variables clauses))
                     find-variables (set/intersection clause-variables needed-variables)
                     in-variables (set/intersection available-variables clause-variables)
                     in-clause (when (seq in-variables) `[:in [[~@in-variables]]])]
                 `[:find ~@find-variables
                   ~@in-clause
                   :where
                   ~@clauses]))
             clauses
             (available-variables query)
             (needed-variables query))
        (vec)
        (conj `[:find ~@(find-form query)
                :in [[~@(free-variables (find-form query))]]]))))

#_(query-plan query)

'([:find ?e ?name :where [?e :cat/name ?name]]
  [:find ?color ?e :in [[?e]] :where [?e :cat/color ?color]]
  [:find ?mood ?e :in [[?e]] :where [?e :cat/mood ?mood]]
  [:find ?e :in [[?e]] :where [?e :cat/name "Henry"]])
