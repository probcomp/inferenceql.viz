(ns inferdb.spreadsheets.search
  (:require [inferdb.spreadsheets.model :as model]
            [inferdb.spreadsheets.data :as data]
            [inferdb.search-by-example.main :as sbe]
            [clojure.walk]))

(defn search-by-example [example emphasis _]
  (sbe/cached-search model/census-cgpm
                     model/cluster-count
                     example
                     emphasis))
