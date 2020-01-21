(ns inferenceql.datalog.sandbox-test
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as string]
            [clojure.test :as test :refer [deftest is are testing]]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [inferenceql.datalog.sandbox :as sandbox]))

(defspec variable?-true
  (prop/for-all [s (gen/such-that #(not (string/includes? % "/"))
                                  gen/string)]
    (let [variable (symbol (str "?" s))]
      (is (sandbox/variable? variable))
      (is (not (sandbox/plain-symbol? variable))))))

(defspec variable?-false
  (prop/for-all [not-variable (gen/fmap symbol
                                    (gen/such-that #(not (or (string/starts-with? % "?")
                                                             (string/includes? % "/")))
                                                   gen/string))]
    (is (not (sandbox/variable? not-variable)))))

(defspec src-var?-true
  (prop/for-all [s (gen/such-that #(not (string/includes? % "/"))
                                  gen/string)]
    (let [variable (symbol (str "$" s))]
      (is (sandbox/src-var? variable))
      (is (not (sandbox/plain-symbol? variable))))))

(defspec src-var?-false
  (prop/for-all [not-variable (gen/fmap symbol
                                        (gen/such-that #(not (or (string/starts-with? % "$")
                                                                 (string/includes? % "/")))
                                                       gen/string))]
    (is (not (sandbox/src-var? not-variable)))))

(deftest form-smoke
  (let [query '[:find ?x
                :in $
                :where
                [?x :cat/name "Henry"]]]
    (are [f expected] (= expected (f query))
      sandbox/find-form  '[?x]
      sandbox/in-form    '[$]
      sandbox/where-form '[[?x :cat/name "Henry"]])))

(deftest find-spec
  (are [x] (s/valid? ::sandbox/find-spec x)
    '[?x]
    '[[?x ...]]
    '[[?x ?y]]
    '[?x .]
    '[(pull ?e [:a :b :c])]
    '[(max ?e)]))

(deftest find-variables
  (are [form variables] (= variables (sandbox/find-variables form))
    '[?x]                   '[?x]
    '[[?x ...]]             '[?x]
    '[[?x ?y]]              '[?x ?y]
    '[?x .]                 '[?x]
    '[(pull ?x [:a :b :c])] '[?x]
    '[(max ?x)]             '[?x]))

(deftest binding-spec
  (are [x] (s/valid? ::sandbox/binding x)
    '?x
    '[?x _ ?z]
    '[?x ...]
    '[[?x ?y ?z]]))

(deftest input-variables
  (are [form variables] (= variables (sandbox/input-variables form))
    '?x           '[?x]
    '[?x _ ?z]    '[?x ?z]
    '[?x ...]     '[?x]
    '[[?x ?y ?z]] '[?x ?y ?z]))
