(ns inferdb.spreadsheets.db
  (:require [clojure.spec.alpha :as s]
            [inferdb.spreadsheets.data :refer [nyt-data]]
            [metaprob.distributions :as dist]))

(s/def ::header string?)
(s/def ::row (s/map-of ::header any?))
(s/def ::rows (s/cat :row (s/* ::row)))
(s/def ::headers (s/cat :header (s/* ::header)))

(s/def ::index nat-int?)
(s/def ::row-index ::index)
(s/def ::column-index ::index)

(s/def ::selection ::row)
(s/def ::selections (s/coll-of ::selection))
(s/def ::selected-columns (s/coll-of (s/coll-of ::header)))

(s/def ::score number?)
(s/def ::scores (s/coll-of ::score))

(s/def ::topojson any?)

(s/def ::selected-row ::row-index)

(s/def ::db (s/keys :req [::headers ::rows]
                    :opt [::selected-row
                          ::scores
                          ::selections
                          ::selected-columns
                          ::topojson]))

(defn with-selected-row-index
  [db row-index]
  (assoc db ::selected-row row-index))

(defn selected-row-index
  [db]
  (get db ::selected-row))

(defn selected-row
  [db]
  (when-let [row-index (get db ::selected-row)]
    (nth (get db ::rows)
         row-index)))

(defn with-selections
  [db selections]
  (assoc db ::selections selections))

(defn with-selected-columns
  [db columns]
  (assoc db ::selected-columns columns))

(defn clear-selections
  [db]
  (dissoc db
          ::selections
          ::selected-row
          ::selected-columns))

(defn selections
  [db]
  (get-in db [::selections]))

(defn selected-columns
  [db]
  (get-in db [::selected-columns]))

(defn table-headers
  [db]
  (get-in db [::headers]))

(defn table-rows
  [db]
  (get-in db [::rows]))

(defn scores
  [db]
  (get-in db [::scores]))

(defn with-scores
  [db scores]
  (assoc-in db [::scores] scores))

(defn default-db
  "When the application starts, this will be the value put in `app-db`."
  []
  {::headers (into [] (keys (first nyt-data)))
   ::rows nyt-data})
