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
    :select "SELECT * FROM (GENERATE * USING model) LIMIT 2"
    :select "SELECT * FROM (GENERATE * GIVEN java=\"False\" USING model) LIMIT 1"
    :select "SELECT * FROM (GENERATE * GIVEN java=\"False\" AND linux=\"True\" USING model) LIMIT 1"
    :select "SELECT (PROBABILITY OF salary_usd USING model), *"
    :select "SELECT (PROBABILITY OF salary_usd GIVEN * USING model), *"
    :select "SELECT (PROBABILITY OF label=\"True\" GIVEN * USING model), *"
    :select "SELECT * FROM (GENERATE * USING model) LIMIT 100"
    :select "SELECT (PROBABILITY OF doctors_visits GIVEN * USING model), *"
    :select "SELECT (PROBABILITY OF label=\"True\" GIVEN * USING model), *"

    :generate "GENERATE * USING model"
    :generate "GENERATE * GIVEN java=\"False\" USING model"
    :generate "GENERATE * GIVEN java=\"False\" AND linux=\"True\" USING model"

    :column "java"
    :column "linux"

    :limit "LIMIT 0"
    :limit "LIMIT 7"
    :limit "LIMIT 24"

    :bindings "java=\"False\""
    :bindings "java=\"False\" AND linux=\"True\""

    :symbol "abc"
    :symbol "abc123"

    :value "\"True\""
    :value "\"False\""

    :ws " "
    :ws "  "
    :ws "\t"))

(deftest parsing-failure
  (are [start query] (some? (insta/get-failure (query/parser query :start start)))
    :symbol "123abc"))
