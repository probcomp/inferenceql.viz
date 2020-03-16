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
      (is (= n (query/parse-and-ransform s :start :nat))))))

(defspec int-parsing
  (prop/for-all [n gen/int]
    (let [s (pr-str n)]
      (is (= n (query/parse-and-transform s :start :int))))))

(defspec float-parsing
  (prop/for-all [n (gen/double* {:infinite? false :NaN? false})]
    (let [s (pr-str n)]
      (is (== n (query/parse-and-transform s :start :float))))))

(deftest parsing-success
  (are [start query] (nil? (insta/get-failure (query/parser query :start start)))
    :query "SELECT * FROM data"))

(deftest parsing-failure
  (are [start query] (some? (insta/get-failure (query/parser query :start start)))
    :query "123abc"))
