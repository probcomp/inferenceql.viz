(ns inferenceql.spreadsheets.query-test
  (:require [clojure.test :as test :refer [are deftest is]]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [instaparse.core :as insta]
            [inferenceql.spreadsheets.query :as query]))

(defspec nat-parsing
  (prop/for-all [n gen/nat]
    (let [s (pr-str n)]
      (is (= n (query/parse s :start :nat))))))

(defspec int-parsing
  (prop/for-all [n gen/int]
    (let [s (pr-str n)]
      (is (= n (query/parse s :start :int))))))

(defspec float-parsing
  (prop/for-all [n (gen/double* {:infinite? false :NaN? false})]
    (let [s (pr-str n)]
      (is (== n (query/parse s :start :float))))))

(deftest parsing-success
  (are [start query] (nil? (insta/get-failure (query/parser query :start start)))
    :select "SELECT test.x FROM table"))

(deftest parsing-failure
  (are [start query] (some? (insta/get-failure (query/parser query :start start)))
    :symbol "123abc"))

(deftest execute-smoke
  (let [db '{table [{table/x 1 table/y 2}
                    {table/x 1 table/y 3}
                    {          table/y 4}]}]
    (are [query result] (= result (query/execute (query/parse query) db))
      "SELECT table.x FROM table"
      '[{table/x 1}
        {table/x 1}
        {table/x nil}]

      "SELECT table.y FROM table"
      '[{table/y 2}
        {table/y 3}
        {table/y 4}]

      "SELECT table.y FROM table WHERE table.y=2"
      '[{table/y 2}]

      "SELECT table.y FROM table WHERE table.x=NULL"
      '[{table/y 4}]

      "SELECT table.y FROM table WHERE table.x!=NULL"
      '[{table/y 2}
        {table/y 3}])))
