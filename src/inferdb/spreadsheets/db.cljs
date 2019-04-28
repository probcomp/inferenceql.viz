(ns inferdb.spreadsheets.db)

(defn default-db
  "When the application starts, this will be the value put in `app-db`."
  []
  {:headers ["" "Ford" "Tesla" "Toyota" "Honda"]
   :rows    [{""       "2017"
              "Ford"   10
              "Tesla"  11
              "Toyota" 12
              "Honda"  13}
             {""       "2018"
              "Ford"   20
              "Tesla"  11
              "Toyota" 14
              "Honda"  13}
             {""       "2019"
              "Ford"   30
              "Tesla"  15
              "Toyota" 12
              "Honda"  13}]})
