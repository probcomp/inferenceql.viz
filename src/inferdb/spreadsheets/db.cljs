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
(s/def ::selections (s/coll-of ::selection))

(s/def ::db (s/keys :req [::headers ::rows]
                    :opt [::selections]))

(defn selection
  [db row col row2 col2 selection-layer-level]
  (update-in db [::selections] (fnil #(assoc % selection-layer-level [row col row2 col2])
                                     [])))

(defn clear-selections
  [db]
  (dissoc db ::selections))

(defn table-selections
  [db]
  (get-in db [::selections]))

(defn table-headers
  [db]
  (get-in db [::headers]))

(defn table-rows
  [db]
  (get-in db [::rows]))

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
