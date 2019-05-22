(ns inferdb.spreadsheets.search
  (:require [inferdb.search-by-example.main :as sbe]
            [inferdb.spreadsheets.model :as model]
            [inferdb.spreadsheets.data :as data]
            [inferdb.spreadsheets.pfcas :as pfcas]))

(defn search-by-example [example emphasis _]
  (sbe/search model/census-cgpm
              model/cluster-data
              data/nyt-data
              example
              emphasis))

#_
(metaprob.prelude/infer-and-score :procedure model/census-cgpm
                                  :inputs [])

#_
(search-by-example {"percent_white" 0.3} "percent_white" nil)
