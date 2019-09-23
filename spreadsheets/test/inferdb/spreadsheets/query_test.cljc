(ns inferdb.spreadsheets.query-test
  (:require [clojure.test :as test :refer [are deftest]]
            [instaparse.core :as insta]
            [inferdb.spreadsheets.query :as query]))

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
