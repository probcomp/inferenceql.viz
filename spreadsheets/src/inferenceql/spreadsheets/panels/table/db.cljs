(ns inferenceql.spreadsheets.panels.table.db
  (:require [clojure.spec.alpha :as s]
            [inferenceql.spreadsheets.data :refer [nyt-data]]))

(def default-db
  {:table-panel {:headers (into [] (keys (first nyt-data)))
                 :rows nyt-data
                 :virtual-rows []
                 :hot-state {:real-table nil :virtual-table nil}}})

(s/def ::table-panel (s/keys :req-un [::headers
                                      ::rows
                                      ::virtual-rows
                                      ::hot-state]
                             :opt-un [::scores
                                      ::virtual-scores
                                      ::table-last-clicked
                                      ::labels]))

;;; Specs related to scores computed on rows.

(s/def ::score number?)
(s/def ::scores (s/coll-of ::score))
(s/def ::virtual-scores (s/coll-of ::score))

;;; Specs related to user-set labels on rows.

(s/def ::label (s/nilable string?))
(s/def ::labels (s/coll-of ::label))

;;; Specs related to table data.

(s/def ::header string?)
(s/def ::row (s/map-of ::header any?))
(s/def ::rows (s/cat :row (s/* ::row)))
(s/def ::virtual-rows ::rows)
(s/def ::headers (s/cat :header (s/* ::header)))

;;; Specs related to selections within handsontable instances.

(s/def ::index nat-int?)
(s/def ::row-index ::index)
(s/def ::column-index ::index)

(s/def ::selections (s/coll-of ::row))
(s/def ::selected-columns (s/coll-of ::header))

(s/def ::row-at-selection-start ::row)
(s/def ::header-clicked boolean?)

;;; Specs related to storing the selection state of both handsontables

(s/def ::table-id #{:real-table :virtual-table})
(s/def ::table-state (s/nilable (s/keys :opt-un [::row-at-selection-start
                                                 ::selections
                                                 ::selected-columns
                                                 ::header-clicked])))
(s/def ::hot-state (s/map-of ::table-id ::table-state))

(s/def ::table-last-clicked ::table-id)

;;; Accessor functions to portions of the table-panel db.

(defn table-headers
  [db]
  (get-in db [:table-panel :headers]))

(defn table-rows
  [db]
  (get-in db [:table-panel :rows]))

(defn virtual-rows
  [db]
  (get-in db [:table-panel :virtual-rows]))

(defn with-virtual-rows
  [db new-v-rows]
  (let [cur-v-rows (virtual-rows db)]
    (assoc-in db [:table-panel :virtual-rows] (concat new-v-rows cur-v-rows))))

(defn clear-virtual-rows
  [db]
  (assoc-in db [:table-panel :virtual-rows] []))

(defn with-labels
  [db labels]
  (assoc-in db [:table-panel :labels] labels))

(defn labels
  [db]
  (get-in db [:table-panel :labels]))

(defn scores
  [db]
  (get-in db [:table-panel :scores]))

(defn with-scores
  [db scores]
  (assoc-in db [:table-panel :scores] scores))

(defn with-virtual-scores
  [db scores]
  (assoc-in db [:table-panel :virtual-scores] scores))

(defn virtual-scores
  [db]
  (get-in db [:table-panel :virtual-scores]))

(defn clear-virtual-scores
  [db]
  (update-in db [:table-panel] dissoc :virtual-scores))

;;; Helper functions for accessing data related to table selection state.

(defn other-table-id
  "Returns the key corresponding to the `table-id` not given."
  [table-id]
  (condp = table-id
    :real-table :virtual-table
    :virtual-table :real-table))

(defn table-selection-state [db table-id]
  "Returns the table selection state corresponding to the `table-id` given."
  (fn [db table-id]
    (get-in db [:table-panel :hot-state table-id])))
