(ns inferenceql.datalog.table-test
  (:require [clojure.test :as test :refer [deftest is]]
            [datahike.api :as d]
            [inferenceql.datalog.table :as table]
            [inferenceql.datalog.test :refer [with-empty-conn]]))

(deftest table-schema
  (is (some? (with-empty-conn conn
               (d/transact conn table/schema)))
      "Table schema can be transacted"))
