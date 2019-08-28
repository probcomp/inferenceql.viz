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
(s/def ::virtual-scores (s/coll-of ::score))

(s/def ::label #(or (string? %) (nil? %)))
(s/def ::labels (s/coll-of ::label))

(s/def ::topojson any?)

(s/def ::selected-row-index ::row-index)

(s/def ::sampled-row ::row)
(s/def ::sampled-rows (s/coll-of ::sampled-row))

(s/def ::row-at-selection-start ::row)

(s/def ::table-id #{:real-table :virtual-table})
(s/def ::table-state any?)
(s/def ::hot-state (s/map-of ::table-id ::table-state))

(s/def ::db (s/keys :req [::headers ::rows ::hot-state]
                    :opt [::scores
                          ::virtual-scores
                          ::labels
                          ::topojson
                          ::sampled-rows]))

(defn with-row-at-selection-start
  [db table-id row]
  (assoc-in db [::hot-state table-id ::row-at-selection-start] row))

(defn row-at-selection-start
  [db table-id]
  (get-in db [::hot-state table-id ::row-at-selection-start]))

(defn with-selected-row-index
  [db table-id row-index]
  (assoc-in db [::hot-state table-id ::selected-row-index] row-index))

(defn selected-row-index
  [db table-id]
  (get-in db [::hot-state table-id ::selected-row-index]))

(defn with-selections
  [db table-id selections]
  (assoc-in db [::hot-state table-id ::selections] selections))

(defn selections
  [db table-id]
  (get-in db [::hot-state table-id ::selections]))

(defn with-selected-columns
  [db table-id columns]
  (assoc-in db [::hot-state table-id ::selected-columns] columns))

(defn selected-columns
  [db table-id]
  (get-in db [::hot-state table-id ::selected-columns]))

(defn clear-selections
  [db table-id]
  (update-in db [::hot-state table-id] dissoc ::selected-columns ::selections ::selected-row-index ::row-at-selection-start))

(defn with-table-last-selected
  [db table-id]
  (assoc db ::table-last-selected table-id))

(defn table-last-selected
  [db]
  (get db ::table-last-selected))

(defn table-not-last-selected
  [db]
  (when-let [table-last-selected (get db ::table-last-selected)]
    (let [table-ids (keys (get db ::hot-state))
          rem-ids (remove #{table-last-selected} table-ids)
          other-id (first rem-ids)]

      ; Enforcing that there are only two tables whose state we are tracking
      ; This is also enforced by the db spec.
      (assert (= 1 (count rem-ids)))
      other-id)))

(defn table-headers
  [db]
  (get-in db [::headers]))

(defn table-rows
  [db]
  (get-in db [::rows]))

(defn with-scores
  [db scores]
  (assoc-in db [::scores] scores))

(defn scores
  [db]
  (get-in db [::scores]))

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
   ::hot-state {:real-table nil :virtual-table nil}})
