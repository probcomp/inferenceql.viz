(ns inferdb.spreadsheets.build-pfcas
  (:require
   [inferdb.spreadsheets.data :as data]
   [inferdb.search-by-example.main :as sbe]
   [inferdb.spreadsheets.model :as model]
   ))

(defn fix-table [t]
  (map
   #(dissoc % "geo_fips" "district_name")
   t))

(defn -main []
  (let [fix-table (fn fix-table [t]
                    (map
                     #(dissoc % "geo_fips" "district_name")
                     t))]
    ;; [filename ns model clusters rows emphasis]
    (sbe/save-pfcas "pfcas.cljc"
                    'inferdb.spreadsheets.pfcas
                    (:proc model/census-cgpm)
                    model/cluster-data
                    (fix-table data/nyt-data)
                    "percent_black")))
