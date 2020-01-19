(ns inferenceql.datalog.sandbox
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.string :as string]
            [clojure.test.check.generators :as generators]
            [meander.epsilon :as m]))

;; https://docs.datomic.com/on-prem/query.html

(defn lis
  "Create a new list with the contents of coll."
  [coll]
  (into (list) coll))

(s/def ::vector (s/and vector? (s/conformer vec vec)))
(s/def ::list   (s/and seq?   (s/conformer lis lis)))

(s/def ::variable
  (let [sym "?"]
    (s/with-gen (s/and symbol? #(string/starts-with? (name %) sym))
      #(gen/fmap (fn [s]
                   (symbol (str sym s)))
                 (s/gen string?)))))

(s/def ::constant (s/or :string string? :number number? :keyword keyword?))

(s/def ::src-var
  (let [sym "$"]
    (s/with-gen (s/and symbol? #(string/starts-with? (name %) sym))
      #(gen/fmap (fn [s]
                   (symbol (str sym s)))
                 (s/gen string?)))))

(defn src-var?
  [x]
  (s/valid? ::src-var x))

(s/def ::data-pattern
  (s/and ::vector
         (s/+ (s/or :variable ::variable
                    :constant ::constant
                    :ignore #{'_}))))

(s/def ::fn (s/and symbol? #(not (s/valid? ::variable %))))
(s/def ::fn-arg (s/or :variable ::variable
                      :constant ::constant
                      :src-var  ::src-var))

(s/def ::pred-expr
  (s/tuple (s/spec (s/cat :predicate ::fn
                          :args (s/+ (s/or :variable ::variable
                                           :constant ::constant
                                           :src-var  ::src-var))))))

#_(s/exercise ::pred-expr)
#_(s/exercise (s/tuple (s/cat :predicate ::fn
                              :args (s/+ ::fn-arg))))

#_(s/conform ::pred-expr '[(even? ?lives)])

(s/def ::fn-expr
  (s/cat :fn-call (s/spec (s/cat :fn ::fn
                                 :args (s/+ ::fn-arg)))
         :binding ::variable))

(s/exercise ::fn-expr)

(s/def ::special
  (s/tuple #{'special}))

(s/def ::where-clause
  (s/or :data-pattern ::data-pattern
        :pred-expr    ::pred-expr
        :fn-expr      ::fn-expr
        ;; :rule-expr    ::rule-expr
        ))

(s/def ::query
  (s/and (s/cat :find-clause (s/cat :symbol #{:find}
                                    :clause (s/+ (complement keyword?)))
                :in-clause (s/? (s/cat :symbol #{:in}
                                       :clause (s/+ symbol?)))
                :where-clause (s/cat :symbol #{:where}
                                     :clauses (s/+ ::where-clause)))
         vector?))

(defn binding-variables
  [binding]
  (let [var? (some-fn variable? src-var?)]
    (m/search binding
              (m/pred var? ?v) ?v ; scalar
              (m/scan (m/pred var? ?v)) ?v ; collection
              [(m/pred var? ?v) '...] ?v ; tuple
              [(m/scan (m/pred var? ?v))] ?v)))

(defn clause-variables
  [clause]
  (m/match (s/conform ::where-clause clause)
    #_
    [:data-pattern [(m/or [:variable !variables] _) ...]]
    #_
    !variables

    #_
    [:pred-expr [{:args [(m/or [:variable !variables] _) ...]}]]
    #_
    !variables

    [:fn-expr {:fn-call _}]
    :ok))

(m/match [:empty {}]
  [:empty {}]
  :ok)

(m/match [:d {}]
  [:b [!c ...]]
  !c

  [:d {}]
  :e)

;; (data-pattern | pred-expr | fn-expr | rule-expr)
;; (not-clause | not-join-clause | or-clause | or-join-clause | expression-clause)

(s/def ::input
  (s/or :src-var ::src-var
        :binding ::binding
        :pattern-name ::pattern-name
        :rules-var ::rules-var))

(defn variable?
  [s]
  (and (symbol? s)
       (-> (name s) (string/starts-with? "?"))))

(defn src-var?
  [s]
  (and (symbol? s)
       (-> (name s) (string/starts-with? "$"))))

(defn in-clause
  [query]
  (->> query
       (drop-while (complement #{:in}))
       (take-while (complement #{:where}))
       (rest)))

(defn where-clause
  [query]
  (->> query
       (drop-while (complement #{:where}))
       (rest)))

(comment

  (def test-query
    '[:find ?e ?c
      :in $
      :where
      [?e :cat/name "Henry"]
      [?e :cat/hungriness 9]
      [?e :cat/color ?c]])

  (in-clause test-query)
  (where-clause test-query)

  (->> test-query
       (drop-while (complement #{:where}))
       (rest)
       (partition-by #(s/valid? ::where-clause %)))

  (s/explain ::data-pattern '[?e :cat/name "Henry"])

  (let [query '[:find [(pull ?e [:satellite/period :satellite/apogee :satellite/perigee]) ...]
                :in $ $models %
                :where
                (row ?e)

                [$models _ :iql.model/generative-function ?gfn]

                [(d/pull $ [:satellite/period] ?e) ?target]
                [(d/pull $ [:satellite/apogee :satellite/perigee] ?e) ?constraints]
                [(ground {}) ?no-constraints]

                [(iqldl.examples/marginal-logpdf ?gfn ?target ?no-constraints) ?nc-pdf]
                [(iqldl.examples/conditional-logpdf ?gfn ?target ?constraints) ?c-pdf]

                [(> ?nc-pdf ?c-pdf)]]]
    (s/explain ::query query)
    #_(s/unform ::query (s/conform ::query query))
    #_(s/conform ::query query))


  (->> '[:find [(pull ?e [:satellite/period :satellite/apogee :satellite/perigee]) ...]
         :in $ $models %
         :where
         (row ?e)

         [$models _ :iql.model/generative-function ?gfn]

         [(d/pull $ [:satellite/period] ?e) ?target]
         [(d/pull $ [:satellite/apogee :satellite/perigee] ?e) ?constraints]
         [(ground {}) ?no-constraints]

         [(iqldl.examples/marginal-logpdf ?gfn ?target ?no-constraints) ?nc-pdf]
         [(iqldl.examples/conditional-logpdf ?gfn ?target ?constraints) ?c-pdf]

         [(> ?nc-pdf ?c-pdf)]]
       (partition-by (complement #{:find :in :where}))
       (partition 2))

  (let [query '[:find ?e
                :where
                [$ _ _ _]]]
    (s/unform ::query (s/conform ::query query))
    (s/conform ::query query))

  (defn anomalous-periods
    "Returns rows with anomalous periods given apogee and perigee."
    []
    (d/q '[:find [(pull ?e [:satellite/period :satellite/apogee :satellite/perigee]) ...]
           :in $ $models %
           :where
           (row ?e)

           [$models _ :iql.model/generative-function ?gfn]

           [(d/pull $ [:satellite/period] ?e) ?target]
           [(d/pull $ [:satellite/apogee :satellite/perigee] ?e) ?constraints]
           [(ground {}) ?no-constraints]

           [(iqldl.examples/marginal-logpdf ?gfn ?target ?no-constraints) ?nc-pdf]
           [(iqldl.examples/conditional-logpdf ?gfn ?target ?constraints) ?c-pdf]

           [(> ?nc-pdf ?c-pdf)]]
         satellites/table-db
         satellites/model-db
         core/rules))

  )
