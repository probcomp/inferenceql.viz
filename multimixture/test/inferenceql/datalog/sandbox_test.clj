(ns inferenceql.datalog.sandbox-test
  (:require [clojure.test :as test :refer [deftest is are testing]]
            [clojure.spec.alpha :as s]
            [datahike.api :as d]
            [inferenceql.datalog.sandbox :as sandbox]))

(deftest smoke-valid
  (are [spec x] (s/valid? spec x)
    ::sandbox/data-pattern '[?e :cat/name "Henry"]
    ::sandbox/pred-expr '[(even? ?lives)]))

(deftest smoke-invalid
  (are [spec x] (not (s/valid? spec x))
    ::sandbox/data-pattern '[(even? ?x)]))
