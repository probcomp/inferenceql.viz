(ns inferdb.spreadsheets.search
  (:require [inferdb.spreadsheets.model :as model]
            [inferdb.spreadsheets.data :as data]
            [metaprob.examples.search-by-example :as sbe]
            [clojure.walk]))

(defn fix-table [t]
  (map
   #(dissoc % :geo_fips :district_name)
   (clojure.walk/keywordize-keys t)))

(defn search-by-example [example emphasis _]
  (sbe/search
   model/census-cgpm
   (fix-table data/nyt-data)
   example
   emphasis))
