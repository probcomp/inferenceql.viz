(ns inferdb.spreadsheets.search
  (:require [inferdb.spreadsheets.model :as model]
            [inferdb.spreadsheets.data :as data]
            [metaprob.examples.search-by-example :as sbe]
            [clojure.walk]))

(defn search-by-example [example context n]
  (sbe/search
   model/census-cgpm
   (map
    #(dissoc % :geo_fips :district_name)
    (clojure.walk/keywordize-keys data/nyt-data))
   example
   context
   n))
