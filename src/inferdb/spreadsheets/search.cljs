(ns inferdb.spreadsheets.search
  (:require [inferdb.spreadsheets.model :as model]
            [inferdb.spreadsheets.data :as data]
            [metaprob.examples.search-by-example :as sbe]
            [clojure.walk]))

(defn search-by-example [example emphasis _]
  (sbe/cached-search model/census-cgpm
                     example
                     emphasis))
