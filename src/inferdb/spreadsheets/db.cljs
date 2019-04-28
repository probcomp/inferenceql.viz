(ns inferdb.spreadsheets.db
  (:require [clojure.spec.alpha :as s]))

(s/def ::header string?)
(s/def ::row (s/map-of ::header any?))
(s/def ::rows (s/cat :row (s/* ::row)))
(s/def ::headers (s/cat :header (s/* ::header)))

(s/def ::index nat-int?)
(s/def ::row-index ::index)
(s/def ::column-index ::index)
(s/def ::selection (s/cat :row ::row-index
                          :col ::column-index
                          :row2 ::row-index
                          :col2 ::column-index))

(s/def ::db (s/keys :req [::headers ::rows]
                    :opt [::selection]))

(defn add-selection
  [db row col row2 col2]
  (assoc-in db [::selection] [row col row2 col2]))

(defn clear-selection
  [db]
  (dissoc db ::selection))

(defn default-db
  "When the application starts, this will be the value put in `app-db`."
  []
  {::headers ["" "Ford" "Tesla" "Toyota" "Honda"]
   ::rows    [{""       "2017"
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
