(ns inferenceql.viz.panels.table.db
  (:require [clojure.spec.alpha :as s]
            [medley.core :as medley]
            [goog.string :refer [format]]
            [inferenceql.viz.util :refer [coerce-bool]]))

(def default-db
  {:table-panel {:selection-layer-coords {}
                 :show-label-column false}})

(s/def ::table-panel (s/keys :req-un [::selection-layer-coords
                                      ::show-label-column]
                             :opt-un [::physical-data
                                      ::visual-state
                                      ::row-ids
                                      ::rows-by-id
                                      ::hot-instance]))

;;; Specs related to table data.

(s/def ::header keyword?)
(s/def ::row (s/map-of ::header any?))
(s/def ::rowid integer?)
(s/def ::row-with-id (s/merge ::row (s/keys :req-un [::rowid])))

(s/def ::rows (s/coll-of ::row-with-id :kind vector?))
(s/def ::headers (s/coll-of ::header :kind vector?))

(s/def ::row-ids (s/coll-of ::rowid :kind vector?))
(s/def ::rows-by-id (s/map-of ::rowid ::row-with-id))
(s/def ::physical-data (s/keys :opt-un [::headers ::row-ids ::rows-by-id]))

(s/def ::visual-state (s/keys :opt-un [::headers ::row-ids]))
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
  (let [row-ids (get-in db [:table-panel :physical-data :row-ids])
        rows-by-id (get-in db [:table-panel :physical-data :rows-by-id])]
    (map rows-by-id row-ids)))

(defn visual-row-ids
  [db]
  (get-in db [:table-panel :visual-state :row-ids]))

(defn selection-layer-coords
  [db]
  (get-in db [:table-panel :selection-layer-coords]))

(defn hot-instance
  [db]
  (get-in db [:table-panel :hot-instance]))

;;; Functions for extracting specialized info from :rows-by-id.

(defn editable-rows
  "Returns a sequence of user-edited rows to be used with auto query-editing.

  Args:
    db - Re-frame app-db.
    schema - The schema for the original dataset (not including new columns and column renames)."
  [db schema]
  (let [rows-by-id (get-in db [:table-panel :rows-by-id])
        row-ids (get-in db [:table-panel :row-ids])
        rows (filter :editable (mapv rows-by-id row-ids))
        quote-strings #(if (string? %) (format "\"%s\"" %) %)
        ;; Not keeping :rowid key.
        ;; Keeping columns in the original datasets plus :editable and :label
        keys-to-keep (conj (keys schema) :editable :label)]
    (vec (for [r rows]
           (as-> r $
             (select-keys $ keys-to-keep)
             (medley/update-existing $ :label coerce-bool)
             (medley/remove-vals nil? $)
             (medley/remove-vals #(= "" %) $)
             (medley/map-keys name $)
             (medley/map-vals quote-strings $))))))

(defn editable-rows-for-incorp
  "Returns sequence of user-edited rows to be used in incorporate clauses for auto query-editing.

  Args:
    db - Re-frame app-db.
    schema - The schema for the original dataset (not including new columns and column renames)."
  [db schema]
  (->> (for [r (editable-rows db schema)]
         (dissoc r "editable"))
       ;; Remove empty rows;
       (filter seq)
       (vec)))

(defn label-values
  "Returns a map of row-id to boolean label value for all original rows (not user-edited). "
  [db]
  (->> (get-in db [:table-panel :rows-by-id])
       (medley/remove-vals :editable)
       (medley/map-vals :label)
       (medley/map-vals coerce-bool)
       (medley/filter-vals some?)))
