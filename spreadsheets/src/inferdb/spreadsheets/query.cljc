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

(def row-generator (mmix/row-generator model/spec))

(defn parse-unsigned-int
  [s]
  (Integer/parseUnsignedInt s))

(defn- transform-from
  ([limit]
   (transform-from {} limit))
  ([obs limit]
   (let [trace (mmix/with-row-values {} obs)
         sample #(first (mp/infer-and-score :procedure row-generator
                                            :observation-trace trace))]
     (repeatedly limit sample))))

(defn transform-select
  [& args]
  args
  #_
  (match (vec args)
    [[:star] s]
    [[:probability ]]
    s))

(defn issue
  [q]
  (let [ast (parser q)]
    (insta/transform
     {:select transform-select

      :from transform-from
      :gen-given hash-map
      :nat parse-unsigned-int}
     ast)))

#_(issue "SELECT * FROM (GENERATE * FROM model) LIMIT 1")
#_(issue "SELECT * FROM (GENERATE * GIVEN java=\"False\" AND linux=\"True\" FROM model) LIMIT 3")

#_(issue "SELECT (PROBABILITY OF salary_usd FROM model), *")
#_(issue "SELECT (PROBABILITY OF salary_usd GIVEN * FROM model), *")

#_(parser "SELECT * FROM (GENERATE * FROM model) LIMIT 1")
#_(parser "SELECT * FROM (GENERATE * GIVEN java=\"False\" FROM model) LIMIT 1")
#_(parser "SELECT (PROBABILITY OF salary_usd GIVEN * FROM model), *")
