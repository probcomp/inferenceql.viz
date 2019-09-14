(ns inferdb.spreadsheets.query-test
  (:require [clojure.test :as test :refer [are deftest is]]
            [instaparse.core :as insta]
            [inferdb.spreadsheets.query :as query :refer [parser]]))

#_(parser "GENERATE * FROM model" :start :generate)
#_(insta/transform query/transform-map
                   (parser "GIVEN java=\"False\" AND linux=\"True\""
                           :start :given))

(deftest select
  (are [query] (seq (query/issue query))
    "SELECT * FROM (GENERATE * FROM model) LIMIT 1"
    "SELECT * FROM (GENERATE * FROM model) LIMIT 2"
    "SELECT * FROM (GENERATE * GIVEN java=\"False\" FROM model) LIMIT 1"))

(deftest valid-select
  (are [query] (nil? (insta/get-failure (parser query :start :select)))
    "SELECT * FROM (GENERATE * FROM model) LIMIT 1"
    "SELECT * FROM (GENERATE * FROM model) LIMIT 2"
    "SELECT * FROM (GENERATE * GIVEN java=\"False\" FROM model) LIMIT 1"
    "SELECT * FROM (GENERATE * GIVEN java=\"False\" AND linux=\"True\" FROM model) LIMIT 1"
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

(deftest valid-prob-given
  (are [query] (nil? (insta/get-failure (parser query :start :prob-given)))
    "GIVEN *"))

(deftest valid-gen-given
  (are [query] (nil? (insta/get-failure (parser query :start :gen-given)))
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
