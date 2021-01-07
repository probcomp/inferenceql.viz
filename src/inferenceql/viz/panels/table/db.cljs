(ns inferenceql.viz.panels.table.db
  (:require [clojure.spec.alpha :as s]))

(def default-db
  {:table-panel {:selection-layer-coords {}}})

(s/def ::table-panel (s/keys :req-un [::selection-layer-coords]
                             :opt-un [::headers
                                      ::rows
                                      ::visual-headers
                                      ::visual-rows
                                      ::hot-instance]))

;;; Specs related to table data.

(s/def ::header keyword?)
(s/def ::row (s/map-of ::header any?))
(s/def ::rows (s/coll-of ::row :kind vector?))
(s/def ::headers (s/coll-of ::header :kind vector?))
(s/def ::visual-rows (s/coll-of ::row :kind vector?))
(s/def ::visual-headers (s/coll-of ::header :kind vector?))

;;; Specs related to selections within handsontable instances.

(s/def ::index nat-int?)
(s/def ::row-index ::index)
(s/def ::column-index ::index)

(s/def ::coords-seq (s/coll-of ::coords))
(s/def ::coords (s/coll-of number? :kind vector? :count 4))

;;; Specs related to storing the selection state of both handsontables

(s/def ::selection-color #{:blue :red :green})
(s/def ::selection-layer-coords (s/map-of ::selection-color ::coords-seq))

;;; Spec related to the handsontable instance itself.

(s/def ::hot-instance some?)

;;; Accessor functions to portions of the table-panel db.

(defn table-headers
  [db]
  (get-in db [:table-panel :headers]))

(defn table-rows
  [db]
  (get-in db [:table-panel :rows] []))

(defn visual-headers
  [db]
  (get-in db [:table-panel :visual-headers]))

(defn visual-rows
  [db]
  (get-in db [:table-panel :visual-rows]))

