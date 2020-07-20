(ns inferenceql.spreadsheets.panels.table.db
  (:require [clojure.spec.alpha :as s]
            [inferenceql.spreadsheets.data :as data]))

(def default-db
  {:table-panel {:dataset {:headers (into [] (keys (first data/app-dataset)))
                           :rows-by-id data/app-dataset-indexed
                           :row-order data/app-dataset-order}
                 :selection-layers {}
                 :label-column-show false}})

(s/def ::table-panel (s/keys :req-un [::dataset
                                      ::selection-layers
                                      ::label-column-show]
                             :opt-un [::physical-data
                                      ::visual-state
                                      ::sort-state
                                      ::hot-instance]))

;;; Specs related to user-set labels on rows.

(s/def ::label-column-show boolean?)

;;; Specs related to table data.

(s/def ::header keyword?)
(s/def ::headers (s/coll-of ::header :kind vector?))
(s/def ::row-id string?)
(s/def ::row (s/map-of ::header any?))
(s/def ::rows (s/coll-of ::row :kind vector?))

;; ::row-special specs out special attributes that get added onto rows.
(s/def :inferenceql.viz.row/id__ ::row-id)
(s/def :inferenceql.viz.row/label__ string?)
(s/def :inferenceql.viz.row/user-added-row__ boolean?)
(s/def :inferenceql.viz.row/row-number__ number?)
(s/def ::row-special (s/keys :req [:inferenceql.viz.row/id__]
                             :opt [:inferenceql.viz.row/label__
                                   :inferenceql.viz.row/user-added-row__
                                   :inferenceql.viz.row/row-number__]))
(s/def ::row-with-special (s/merge ::row ::row-special))

(s/def ::rows-by-id (s/map-of ::row-id ::row-with-special))
(s/def ::row-order (s/coll-of ::row-id))
(s/def ::staged-changes (s/map-of ::row-id ::row))
(s/def ::staged-row-order-for-new-rows (s/coll-of ::row-id))
(s/def ::virtual boolean?)

(s/def ::dataset (s/keys :req-un [::headers
                                  ::rows-by-id
                                  ::row-order]))
(s/def ::physical-data (s/keys :req-un [::headers
                                        ::rows-by-id
                                        ::row-order
                                        ::virtual]
                               :opt-un [::staged-changes
                                        ::staged-row-order-for-new-rows]))
(s/def ::visual-state (s/keys :req-un [::headers
                                       ::row-order]))

;;; Specs related to table sort state.

(s/def ::column number?)
(s/def ::sortOrder #{"asc" "desc"})
(s/def ::column-sort-state (s/keys :req-un [::column
                                            ::sortOrder]))
;; The format of ::sort-state is determined by Handsontable.
(s/def ::sort-state (s/coll-of ::column-sort-state :kind vector?))

;;; Spec related to the handsontable instance itself.

(s/def ::hot-instance some?)

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

(defn physical-staged-changes
  [db]
  (get-in db [:table-panel :physical-data :staged-changes] []))

(defn physical-staged-row-order-for-new-rows
  [db]
  (get-in db [:table-panel :physical-data :staged-row-order-for-new-rows] []))

(defn physical-row-order-all
  [db]
  (vec (concat (physical-row-order db)
               (physical-staged-row-order-for-new-rows db))))

(defn user-added-row-ids
  [db]
  (->> (physical-rows-by-id db)
       (vals)
       (filter (comp true? :inferenceql.viz.row/user-added-row__))
       (map :inferenceql.viz.row/id__)
       (set)))

;;; Accessor functions to :visual-data related paths.

(defn visual-headers
  [db]
  (get-in db [:table-panel :visual-state :headers]))

(defn visual-row-order
  [db]
  (get-in db [:table-panel :visual-state :row-order]))

