(ns inferenceql.spreadsheets.panels.table.db
  (:require [clojure.spec.alpha :as s]
            [inferenceql.spreadsheets.data :as data]))

(def default-db
  {:table-panel {:dataset {:headers (into [] (keys (first data/app-dataset)))
                           :rows-by-id data/app-dataset-indexed
                           :row-order data/app-dataset-order}
                 :selection-layers {}}})

(s/def ::table-panel (s/keys :req-un [::dataset
                                      ::selection-layers]
                             :opt-un [::physical-data
                                      ::visual-state]))

;;; Specs related to table data.

(s/def ::header keyword?)
(s/def ::headers (s/coll-of ::header :kind vector?))
(s/def ::row-id string?)
(s/def ::row (s/map-of ::header any?))
(s/def ::rows (s/coll-of ::row :kind vector?))

(s/def :iql.viz.row/id__ ::row-id)
(s/def :iql.viz.row/row-number__ number?)
(s/def :iql.viz.row/label__ string?)
(s/def :iql.viz.row/user-added-row__ boolean?)

;; ::row-special specs out special attributes that get added onto rows.
(s/def ::row-special (s/keys :req [:iql.viz.row/id__
                                   :iql.viz.row/row-number__]
                             :opt [:iql.viz.row/label__
                                   :iql.viz.row/user-added-row__]))
(s/def ::row-with-special (s/merge ::row ::row-special))

(s/def ::rows-by-id (s/map-of ::row-id ::row-with-special))
(s/def ::row-order (s/coll-of ::row-id))
(s/def ::rows-by-id-with-changes (s/map-of ::row-id ::row))
(s/def ::row-order-for-new-rows (s/coll-of ::row-id))
(s/def ::virtual boolean?)

;; ::dataset holds the original dataset that launched with the app
(s/def ::dataset (s/keys :req-un [::headers
                                  ::rows-by-id
                                  ::row-order]))

;; ::physical-data is mostly concerned with data that will be used to populate
;; our Handsontable component via subscriptions.
(s/def ::physical-data (s/keys :req-un [
                                        ;; We populate Handsontable based on
                                        ;; the following three keys.
                                        ::headers
                                        ::rows-by-id
                                        ::row-order
                                        ::virtual

                                        ;; This key holds the same data as ::rows-by-id but it is
                                        ;; also augmented by changes the user has made via
                                        ;; editing cells in the table. These changes do not
                                        ;; propagate back to the table via subscriptions.
                                        ::rows-by-id-with-changes]

                                        ;; This key hold the row-id's which in
                                        ;; ::rows-by-id-with-changes map to newly added rows
                                        ;; (added via the add row button)
                               :opt-un [::row-order-for-new-rows]))

(s/def ::visual-state (s/keys :req-un [
                                       ;; The data attributes that correspond the columns
                                       ;; currently being displayed in Handsontable.
                                       ;; Takes into account column reorderings done in the
                                       ;; Handsontable GUI.
                                       ::headers
                                       ;; Holds the ordering of rows that corresponds to what
                                       ;; the user sees in Handsontable. Takes into account
                                       ;; various operations like sort and filtering done in
                                       ;; the Handsontable GUI.
                                       ::row-order]))

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

(defn physical-rows-by-id-with-changes
  [db]
  (get-in db [:table-panel :physical-data :rows-by-id-with-changes] []))

(defn physical-row-order-for-new-rows
  [db]
  (get-in db [:table-panel :physical-data :row-order-for-new-rows] []))

(defn physical-row-order-all
  [db]
  (vec (concat (physical-row-order db)
               (physical-row-order-for-new-rows db))))

;;; Accessor functions to :visual-data related paths.

(defn visual-headers
  [db]
  (get-in db [:table-panel :visual-state :headers]))

(defn visual-row-order
  [db]
  (get-in db [:table-panel :visual-state :row-order]))
