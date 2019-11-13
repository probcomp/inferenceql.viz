(ns inferenceql.spreadsheets.query-test
  (:require [clojure.test :as test :refer [are deftest is]]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [instaparse.core :as insta]
            [inferenceql.multimixture.search  :as search]
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
  (let [env {"table" [{"x" 1 "y" 2}
                      {"x" 1 "y" 3}
                      {      "y" 4}]}]
    (are [query result] (= result ((query/parse query) env))
      "SELECT * FROM table"
      (get env "table")

      "SELECT table.x FROM table"
      [{"x" 1}
       {"x" 1}
       {"x" nil}]

      "SELECT table.y FROM table"
      [{"y" 2}
       {"y" 3}
       {"y" 4}]

      "SELECT table.y FROM table WHERE table.y=2"
      [{"y" 2}]

      "SELECT table.y FROM table WHERE table.x=NULL"
      [{"y" 4}]

      "SELECT table.y FROM table WHERE table.x!=NULL"
      [{"y" 2}
       {"y" 3}])))

(deftest probability-smoke
  (let [spec {:vars {"x" :categorical
                     "y" :categorical}
              :views [[{:probability 0.5
                        :parameters {"x" {"a" 1.0}
                                     "y" {"a" 1.0}}}
                       {:probability 0.5
                        :parameters {"x" {"b" 1.0}
                                     "y" {"b" 1.0}}}]]}
        env {"table" [{"x" 1 "y" 2}
                      {"x" 1 "y" 3}
                      {"x" 2 "y" 4}]
             "model" (search/optimized-row-generator spec)}]
    (are [query result] (= result (map (comp first vals) ((query/parse query) env)))
      "SELECT PROBABILITY OF model.x=\"a\" GIVEN model.y=\"a\" USING model FROM table"
      [1.0 1.0 1.0]

      "SELECT PROBABILITY OF model.x=\"a\" GIVEN model.y=\"b\" USING model FROM table"
      [0.0 0.0 0.0])))

(deftest generate-smoke
  (let [spec {:vars {"x" :categorical
                     "y" :categorical}
              :views [[{:probability 0.5
                        :parameters {"x" {"a" 1.0}
                                     "y" {"a" 1.0}}}
                       {:probability 0.5
                        :parameters {"x" {"b" 1.0}
                                     "y" {"b" 1.0}}}]]}
        env {"table" [{"x" 1 "y" 2}
                      {"x" 1 "y" 3}
                      {"x" 2 "y" 4}]
             "model" (search/optimized-row-generator spec)}]
    (are [query result] (= result ((query/parse query) env))
      "SELECT model.x FROM GENERATE model.x GIVEN model.y=\"a\" USING model LIMIT 1"
      [{"x" "a"}]

      "SELECT model.x FROM GENERATE model.x GIVEN model.y=\"b\" USING model LIMIT 2"
      [{"x" "b"}
       {"x" "b"}])))
