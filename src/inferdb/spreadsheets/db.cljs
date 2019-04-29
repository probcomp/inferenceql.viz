(ns inferdb.spreadsheets.db
  (:require [clojure.spec.alpha :as s]
            [metaprob.distributions :as dist]))

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
  {::headers ["x" "y" "z"]
   ::rows (into [] (repeatedly 10000
                               (fn []
                                 (let [x (dist/uniform 0 10)]
                                   {"x" x
                                    "y" (dist/gaussian 0 x)
                                    "z" (dist/gaussian 0 1)}))))})
