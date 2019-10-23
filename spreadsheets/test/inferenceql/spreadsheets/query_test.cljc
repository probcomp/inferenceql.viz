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

(deftest valid-select
  (are [start query] (nil? (insta/get-failure (query/parser query :start start)))
    :select "SELECT * FROM (GENERATE * FROM model) LIMIT 1"
    :select "SELECT * FROM (GENERATE * FROM model) LIMIT 2"
    :select "SELECT * FROM (GENERATE * GIVEN java=\"False\" FROM model) LIMIT 1"
    :select "SELECT * FROM (GENERATE * GIVEN java=\"False\" AND linux=\"True\" FROM model) LIMIT 1"
    :select "SELECT (PROBABILITY OF salary_usd FROM model), *"
    :select "SELECT (PROBABILITY OF salary_usd GIVEN * FROM model), *"
    :select "SELECT (PROBABILITY OF label=\"True\" GIVEN * FROM model), *"
    :select "SELECT * FROM (GENERATE * FROM model) LIMIT 100"
    :select "SELECT (PROBABILITY OF doctors_visits GIVEN * FROM model), *"
    :select "SELECT (PROBABILITY OF label=\"True\" GIVEN * FROM model), *"

    :generate "GENERATE * FROM model"
    :generate "GENERATE * GIVEN java=\"False\" FROM model"
    :generate "GENERATE * GIVEN java=\"False\" AND linux=\"True\" FROM model"

    :column "java"
    :column "linux"

    :limit "LIMIT 0"
    :limit "LIMIT 7"
    :limit "LIMIT 24"

    :bindings "java=\"False\""
    :bindings "java=\"False\" AND linux=\"True\""

    :value "\"True\""
    :value "\"False\""

    :ws " "
    :ws "  "
    :ws "\t"))
