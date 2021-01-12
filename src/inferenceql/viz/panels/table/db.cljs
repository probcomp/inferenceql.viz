(ns inferenceql.viz.panels.table.db
  (:require [clojure.spec.alpha :as s]))

(def default-db
  {:table-panel {:selection-layer-coords {}
                 :show-label-column false}})

(s/def ::table-panel (s/keys :req-un [::selection-layer-coords
                                      ::show-label-column]
                             :opt-un [::physical-data
                                      ::visual-headers
                                      ::visual-rows
                                      ::hot-instance]))

;;; Specs related to table data.

(s/def ::header keyword?)
(s/def ::row (s/map-of ::header any?))
(s/def ::rowid integer?)
(s/def ::row-with-id (s/merge ::row (s/keys :req-un [::rowid])))

(s/def ::rows (s/coll-of ::row-with-id :kind vector?))
(s/def ::headers (s/coll-of ::header :kind vector?))

(s/def ::row-order (s/coll-of ::rowid :kind vector?))
(s/def ::rows-by-id (s/map-of ::rowid ::row-with-id))
(s/def ::physical-data (s/keys :opt-un [::headers ::row-order ::rows-by-id]))

(s/def ::visual-rows (s/coll-of ::row :kind vector?))
(s/def ::visual-headers (s/coll-of ::header :kind vector?))
(s/def ::show-label-column boolean?)

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

(defn physical-headers
  [db]
  (get-in db [:table-panel :physical-data  :headers]))

(defn physical-rows
  [db]
  (let [row-order (get-in db [:table-panel :physical-data :row-order])
        rows-by-id (get-in db [:table-panel :physical-data :rows-by-id])]
    (map rows-by-id row-order)))

(defn visual-headers
  [db]
  (get-in db [:table-panel :visual-headers]))

(defn visual-rows
  [db]
  (get-in db [:table-panel :visual-rows]))

