(ns inferdb.spreadsheets.query-test
  (:require [clojure.java.io :as io]
            [clojure.test :as test :refer [are deftest is]]
            [instaparse.core :as insta]))

(def parser (insta/parser (io/resource "query.bnf")))

#_(parser "SELECT (PROBABILITY OF label=\"True\" GIVEN * FROM model), *"
          :start :select)

(deftest valid-select
  (are [query] (nil? (insta/get-failure (parser query :start :select)))
    "SELECT * FROM (GENERATE * FROM model) LIMIT 1"
    "SELECT * FROM (GENERATE * FROM model) LIMIT 2"
    "SELECT * FROM (GENERATE * GIVEN java=\"False\" FROM model)"
    "SELECT * FROM (GENERATE * GIVEN java=\"False\" AND linux=\"True\" FROM model)"
    "SELECT (PROBABILITY OF salary_usd FROM model), *"
    "SELECT (PROBABILITY OF salary_usd GIVEN * FROM model), *"
    "SELECT (PROBABILITY OF label=\"True\" GIVEN * FROM model), *"
    "SELECT * FROM (GENERATE * FROM model) LIMIT 100"
    "SELECT * FROM (GENERATE * FROM model) LIMIT 100"
    "SELECT (PROBABILITY OF doctors_visits GIVEN * FROM model), *"
    "SELECT (PROBABILITY OF label=\"True\" GIVEN * FROM model), *"))

(deftest valid-generate
  (are [query] (nil? (insta/get-failure (parser query :start :generate)))
    "GENERATE * FROM model"
    "GENERATE * GIVEN java=\"False\" FROM model"
    "GENERATE * GIVEN java=\"False\" AND linux=\"True\" FROM model"))

(deftest valid-given
  (are [query] (nil? (insta/get-failure (parser query :start :given)))
    "GIVEN java=\"False\""
    "GIVEN java=\"False\" AND linux=\"True\""))

(deftest valid-bindings
  (are [query] (nil? (insta/get-failure (parser query :start :bindings)))
    "java=\"False\""
    "java=\"False\" AND linux=\"True\""))

(deftest valid-column
  (are [query] (nil? (insta/get-failure (parser query :start :column)))
    "java"
    "linux"))

(deftest valid-value
  (are [query] (nil? (insta/get-failure (parser query :start :value)))
    "\"True\""
    "\"False\""))

(deftest valid-limits
  (are [query] (nil? (insta/get-failure (parser query :start :limit)))
    "LIMIT 0"
    "LIMIT 7"
    "LIMIT 24"))

(deftest invalid-limits
  (are [query] (some? (insta/get-failure (parser query :start :limit)))
    "LIMIT"))

(deftest valid-whitespace
  (are [query] (nil? (insta/get-failure (parser query :start :ws)))
    " "
    "  "
    "\t"))

(comment
  "SELECT * FROM (GENERATE * FROM model) LIMIT 1"
  "SELECT * FROM (GENERATE * FROM model) LIMIT 2"
  "SELECT * FROM (GENERATE * GIVEN java=\"False\" FROM model)"
  "SELECT * FROM (GENERATE * GIVEN java=\"False\" AND linux=\"True\" FROM model)"
  "SELECT (PROBABILITY OF salary_usd FROM model), *"
  "SELECT (PROBABILITY OF salary_usd GIVEN * FROM model), *"
  "SELECT (PROBABILITY OF label=\"True\" GIVEN * FROM model), *"
  "SELECT * FROM (GENERATE * FROM model) LIMIT 100"
  "SELECT * FROM (GENERATE * FROM model) LIMIT 100"
  "SELECT (PROBABILITY OF doctors_visits GIVEN * FROM model), *"
  "SELECT (PROBABILITY OF label=\"True\" GIVEN * FROM model), *")
