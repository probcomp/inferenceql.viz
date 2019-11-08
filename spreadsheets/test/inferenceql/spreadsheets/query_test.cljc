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
  (let [db {"table" [{"x" 1 "y" 2}
                     {"x" 1 "y" 3}
                     {      "y" 4}]}]
    (are [query result] (= result (query/execute (query/parse query) db))
      "SELECT table.x FROM table"
      '[{"x" 1}
        {"x" 1}
        {"x" nil}]

      "SELECT table.y FROM table"
      '[{"y" 2}
        {"y" 3}
        {"y" 4}]

      "SELECT table.y FROM table WHERE table.y=2"
      '[{"y" 2}]

      "SELECT table.y FROM table WHERE table.x=NULL"
      '[{"y" 4}]

      "SELECT table.y FROM table WHERE table.x!=NULL"
      '[{"y" 2}
        {"y" 3}])))
