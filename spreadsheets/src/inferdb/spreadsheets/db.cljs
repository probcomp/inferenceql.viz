(ns inferdb.spreadsheets.db
  (:require [clojure.spec.alpha :as s]
            [inferdb.spreadsheets.data :refer [nyt-data]]
            [metaprob.distributions :as dist]))

(s/def ::header string?)
(s/def ::row (s/map-of ::header any?))
(s/def ::rows (s/cat :row (s/* ::row)))
(s/def ::virtual-rows ::rows)
(s/def ::headers (s/cat :header (s/* ::header)))

(s/def ::index nat-int?)
(s/def ::row-index ::index)
(s/def ::column-index ::index)

(s/def ::selection (s/coll-of ::row))
(s/def ::selections (s/coll-of ::selection))
(s/def ::selected-columns (s/coll-of ::header))

(s/def ::score number?)
(s/def ::scores (s/coll-of ::score))
(s/def ::virtual-scores (s/coll-of ::score))

(s/def ::label (s/nilable string?))
(s/def ::labels (s/coll-of ::label))

(s/def ::topojson any?)

(s/def ::selected-row-index ::row-index)
(s/def ::row-at-selection-start ::row)
(s/def ::header-clicked boolean?)

(s/def ::table-id #{:real-table :virtual-table})
(s/def ::table-state (s/nilable (s/keys :opt-un [::row-at-selection-start
                                                 ::selected-row-index
                                                 ::selections
                                                 ::selected-columns
                                                 ::header-clicked])))
(s/def ::hot-state (s/map-of ::table-id ::table-state))

(s/def ::table-last-clicked ::table-id)

(s/def ::db (s/keys :req [::headers
                          ::rows
                          ::virtual-rows
                          ::hot-state
                          ::confidence-threshold
                          ::confidence-options]
                    :opt [::scores
                          ::virtual-scores
                          ::labels
                          ::topojson
                          ::table-last-clicked]))

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

(defn with-virtual-scores
  [db scores]
  (assoc-in db [::virtual-scores] scores))

(defn virtual-scores
  [db]
  (get-in db [::virtual-scores]))

(defn clear-virtual-scores
  [db]
  (dissoc db ::virtual-scores))

(defn virtual-rows
  [db]
  (get-in db [::virtual-rows]))

(defn with-virtual-rows
  [db new-v-rows]
  (let [cur-v-rows (virtual-rows db)]
    (assoc-in db [::virtual-rows] (concat new-v-rows cur-v-rows))))

(defn clear-virtual-rows
  [db]
  (assoc-in db [::virtual-rows] []))

(defn with-labels
  [db labels]
  (assoc-in db [::labels] labels))

(defn labels
  [db]
  (get-in db [::labels]))

(defn default-db
  "When the application starts, this will be the value put in `app-db`."
  []
  {::headers (into [] (keys (first nyt-data)))
   ::rows nyt-data
   ::virtual-rows []
   ::hot-state {:real-table nil :virtual-table nil}
   ::confidence-threshold 0.9
   ::confidence-options {:mode :none}})
