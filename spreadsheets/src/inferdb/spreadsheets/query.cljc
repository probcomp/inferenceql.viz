(ns inferdb.spreadsheets.query
  (:require [clojure.core.match :refer [match]]
            [clojure.java.io :as io]
            [instaparse.core :as insta]
            [metaprob.prelude :as mp]
            [inferdb.multimixture :as mmix]
            [inferdb.spreadsheets.model :as model]))

#_(let [parser (insta/parser (io/resource "query.bnf"))]
    (parser "GENERATE"))

#_(set! *print-length* 10)

(def parser (insta/parser (io/resource "query.bnf")))

#_(parser "SELECT * FROM (GENERATE * FROM model) LIMIT 1")
#_(parser "SELECT * FROM (GENERATE * GIVEN java=\"False\" FROM model) LIMIT 1")
#_(parser "SELECT (PROBABILITY OF salary_usd GIVEN * FROM model), *")
