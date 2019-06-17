(ns inferdb.spreadsheets.search
  (:require [inferdb.search-by-example.main :as sbe]
            [inferdb.spreadsheets.model :as model]
            [inferdb.spreadsheets.pfcas :as pfcas]))

(defn search-by-example [example emphasis _]
  (sbe/cached-search model/model-cgpm
                     model/cluster-data
                     pfcas/pfcas
                     example
                     emphasis))
