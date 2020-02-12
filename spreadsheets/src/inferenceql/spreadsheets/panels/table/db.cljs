(ns inferenceql.spreadsheets.panels.table.db
  (:require [clojure.spec.alpha :as s]
            [inferenceql.spreadsheets.data :refer [nyt-data]]))

(def default-db
  {:table-panel {:headers (into [] (keys (first nyt-data)))
                 :rows nyt-data
                 :hot-state {:real-table nil}}})

(s/def ::table-panel (s/keys :req-un [::headers
                                      ::rows
                                      ::hot-state]
                             :opt-un [::scores
                                      ::labels]))

;;; Specs related to scores computed on rows.

(s/def ::score number?)
(s/def ::scores (s/coll-of ::score))

;;; Specs related to user-set labels on rows.

(s/def ::label (s/nilable string?))
(s/def ::labels (s/coll-of ::label))

;;; Specs related to table data.

(s/def ::header string?)
(s/def ::row (s/map-of ::header any?))
(s/def ::rows (s/cat :row (s/* ::row)))
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

(s/def ::table-id #{:real-table})
(s/def ::table-state (s/nilable (s/keys :opt-un [::row-at-selection-start
                                                 ::selections
                                                 ::selected-columns
                                                 ::header-clicked])))
(s/def ::hot-state (s/map-of ::table-id ::table-state))

;;; Accessor functions to portions of the table-panel db.

(defn table-headers
  [db]
  (get-in db [:table-panel :headers]))

(defn table-rows
  [db]
  (get-in db [:table-panel :rows]))

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
