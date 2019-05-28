(ns inferdb.spreadsheets.build-pfcas
  (:require [inferdb.search-by-example.main :as sbe]
            [inferdb.spreadsheets.data :as data]
            [inferdb.spreadsheets.model :as model]))

(defn -main []
  (let [fix-table (fn fix-table [t]
                    (map
                     #(as-> % r
                          (dissoc r "geo_fips" "district_name")
                          (remove (comp nil? second) r))
                     t))]
    (sbe/save-pfcas "pfcas.cljc"
                    'inferdb.spreadsheets.pfcas
                    (:proc model/census-cgpm)
                    model/cluster-data
                    (fix-table data/nyt-data)
                    "percent_white")))
