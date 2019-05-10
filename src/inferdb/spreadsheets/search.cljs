(ns inferdb.spreadsheets.search
  (:require [inferdb.search-by-example.main :as sbe]
            [inferdb.spreadsheets.model :as model]
            [inferdb.spreadsheets.data :as data]
            [inferdb.spreadsheets.pfcas :as pfcas]))

(defn search-by-example [example emphasis _]
  (sbe/cached-search model/census-cgpm
                     model/cluster-data
                     pfcas/pfcas
                     example
                     emphasis))
