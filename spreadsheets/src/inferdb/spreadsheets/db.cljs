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

(s/def ::selection (s/coll-of ::row))
(s/def ::selections (s/coll-of ::selection))
(s/def ::selected-columns (s/coll-of ::header))

(s/def ::score number?)
(s/def ::scores (s/coll-of ::score))

(s/def ::example-status bool?)
(s/def ::example-statuses (s/coll-of ::example-status))

(s/def ::topojson any?)

(s/def ::selected-row-index ::row-index)

(s/def ::sampled-row ::row)
(s/def ::sampled-rows (s/coll-of ::sampled-row))

(s/def ::row-at-selection-start ::row)

(s/def ::db (s/keys :req [::headers ::rows]
                    :opt [::simulator
                          ::simulated-rows
                          ::selected-row-index
                          ::row-at-selection-start
                          ::scores
                          ::example-statuses
                          ::selected-columns
                          ::topojson
                          ::sampled-rows]))

(defn with-row-at-selection-start
  [db row]
  (assoc db ::row-at-selection-start row))

(defn row-at-selection-start
  [db]
  (get db ::row-at-selection-start))

(defn with-selected-row-index
  [db row-index]
  (assoc db ::selected-row-index row-index))

(defn selected-row-index
  [db]
  (get db ::selected-row-index))

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

(defn virtual-rows
  [db]
  (get-in db [::virtual-rows]))

(defn scores
  [db]
  (get-in db [::scores]))

(defn left-scroll-pos
  [db]
  (get-in db [::left-scroll-pos]))

(defn pos-emmitter
  [db]
  (get-in db [::pos-emmitter]))

(defn with-scores
  [db scores]
  (assoc-in db [::scores] scores))

(defn with-virtual-rows
  [db new-v-rows]
  (let [cur-v-rows (virtual-rows db)]
    (assoc-in db [::virtual-rows] (concat new-v-rows cur-v-rows))))

(defn clear-simulations
  [db]
  (assoc-in db [::virtual-rows] []))

(defn with-left-scroll-pos
  [db pos-emmitter left-scroll-pos]
  ;(.log js/console left-scroll-pos)
  ;(.log js/console pos-emmitter)
  (let [first-assoc (assoc-in db [::left-scroll-pos] left-scroll-pos)]
    (assoc-in first-assoc [::pos-emmitter] pos-emmitter)))

(defn example-statuses
  [db]
  (get-in db [::example-statuses]))

(defn default-db
  "When the application starts, this will be the value put in `app-db`."
  []
  {::headers (into [] (keys (first nyt-data)))
   ::rows nyt-data
   ::virtual-rows []})

(defn one-cell-selected?
  [db]
  (and (= 1 (count (selected-columns db)))
       (= 1 (count (selections db)))
       (= 1 (count (first (selections db))))))
