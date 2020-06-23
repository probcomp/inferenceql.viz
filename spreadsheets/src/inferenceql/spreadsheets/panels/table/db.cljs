(ns inferenceql.spreadsheets.panels.table.db
  (:require [clojure.spec.alpha :as s]
            [inferenceql.spreadsheets.data :as data]
            [medley.core :as medley]))

(def default-db
  {:table-panel {:dataset {:headers (into [] (keys (first data/app-dataset)))
                           :rows-by-id data/app-dataset-indexed
                           :row-order data/app-dataset-order}
                 :selection-layers {}}})

(s/def ::table-panel (s/keys :req-un [::dataset
                                      ::selection-layers]
                             :opt-un [::physical-data
                                      ::visual-data]))

;;; Specs related to table data.

(s/def ::header keyword?)
(s/def ::headers (s/coll-of ::header :kind vector?))
(s/def ::row-id string?)
(s/def ::row (s/map-of ::header any?))
(s/def ::rows (s/coll-of ::row :kind vector?))

(s/def :inferenceql.viz.row/id__ ::row-id)
;; ::row-special specs out special attributes that get added onto rows.
(s/def ::row-special (s/keys :req [:inferenceql.viz.row/id__]))

(s/def ::rows-by-id (s/map-of ::row-id (s/merge ::row ::row-special)))
(s/def ::row-order (s/coll-of ::row-id))
(s/def ::virtual boolean?)

(s/def ::dataset (s/keys :req-un [::headers
                                  ::rows-by-id
                                  ::row-order]))

(s/def ::physical-data (s/keys :req-un [::headers
                                        ::rows-by-id
                                        ::row-order
                                        ::virtual]))

(s/def ::visual-data (s/keys :req-un [::headers
                                      ::rows]))

;;; Specs related to selections within handsontable instances.

(s/def ::index nat-int?)
(s/def ::row-index ::index)
(s/def ::column-index ::index)

(s/def ::header-clicked boolean?)
(s/def ::coords (s/coll-of ::selection-layer-coords))
(s/def ::selection-layer-coords (s/coll-of number? :kind vector? :count 4))

;;; Specs related to storing the selection state of both handsontables

(s/def ::selection-color #{:blue :red :green})
(s/def ::selection-state (s/keys :opt-un [::header-clicked
                                          ::coords]))
(s/def ::selection-layers (s/map-of ::selection-color ::selection-state))

;;; Accessor functions to :dataset related paths.

(defn dataset-headers
  [db]
  (get-in db [:table-panel :dataset  :headers]))

(defn dataset-rows-by-id
  [db]
  (get-in db [:table-panel :dataset :rows-by-id]))

(defn dataset-row-order
  [db]
  (get-in db [:table-panel :dataset :row-order]))

;;; Accessor functions to :physical-data related paths.

(defn physical-headers
  [db]
  (get-in db [:table-panel :physical-data :headers]))

(defn physical-rows-by-id
  [db]
  (get-in db [:table-panel :physical-data :rows-by-id] []))

(defn physical-row-order
  [db]
  (get-in db [:table-panel :physical-data :row-order] []))

;;; Accessor functions to :visual-data related paths.

(defn visual-headers
  [db]
  (get-in db [:table-panel :visual-data :headers]))

(defn visual-rows
  [db]
  (get-in db [:table-panel :visual-data :rows]))

;;; Special accessor functions

(defn dataset-as-iql-query-rows
  "Returns dataset as rows usable by inferenceql.query
  This returns a collection of maps, where each map represents a row.
  Each map is also filtered for nil values as inferenceql.query cannot
  handle these nil values."
  [db]
  (let [rows-by-id (dataset-rows-by-id db)
        row-order (dataset-row-order db)
        rows (map rows-by-id row-order)]
    (map #(medley/remove-vals nil? %) rows)))
