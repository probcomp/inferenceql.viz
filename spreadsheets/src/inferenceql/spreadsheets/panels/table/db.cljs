(ns inferenceql.spreadsheets.panels.table.db
  (:require [clojure.spec.alpha :as s]
            [inferenceql.spreadsheets.data :refer [nyt-data]]))

(def default-db
  {:table-panel {:dataset-headers (into [] (keys (first nyt-data)))
                 :dataset-rows nyt-data
                 :selection-layers {}}})

(s/def ::table-panel (s/keys :req-un [::dataset-headers
                                      ::dataset-rows
                                      ::selection-layers]
                             :opt-un [::headers
                                      ::rows
                                      ::visual-headers
                                      ::visual-rows
                                      ::virtual]))

;;; Specs related to table data.

(s/def ::header keyword?)
(s/def ::row (s/map-of ::header any?))
(s/def ::rows (s/coll-of ::row :kind vector?))
(s/def ::headers (s/coll-of ::header :kind vector?))
(s/def ::visual-rows (s/coll-of ::row :kind vector?))
(s/def ::visual-headers (s/coll-of ::header :kind vector?))
(s/def ::dataset-rows (s/coll-of ::row :kind vector?))
(s/def ::dataset-headers (s/coll-of ::header :kind vector?))
(s/def ::virtual boolean?)

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

;;; Accessor functions to portions of the table-panel db.

(defn table-headers
  [db]
  (get-in db [:table-panel :headers]))

(defn table-rows
  [db]
  (get-in db [:table-panel :rows] []))

(defn dataset-headers
  [db]
  (get-in db [:table-panel :dataset-headers]))

(defn dataset-rows
  [db]
  (get-in db [:table-panel :dataset-rows]))

(defn visual-headers
  [db]
  (get-in db [:table-panel :visual-headers]))

(defn visual-rows
  [db]
  (get-in db [:table-panel :visual-rows]))

