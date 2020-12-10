(ns inferenceql.viz.panels.table.selections
  "Items related to selections in Handsontable"
  (:require [clojure.spec.alpha :as s]
            [medley.core :as medley]
            [inferenceql.viz.panels.table.db :as db]))

;;;; Misc defs for computing the value of the :table/selection-layers sub.

;;; Specs for validating the output of selection-layers fn.

(s/def ::selections (s/coll-of ::db/row))
(s/def ::selected-columns (s/coll-of ::db/header))
(s/def ::row-at-selection-start ::db/row)
(s/def ::selection-state-augments (s/keys :opt-un [::row-at-selection-start
                                                   ::selections
                                                   ::selected-columns]))
(s/def ::selection-state (s/merge ::db/selection-state ::selection-state-augments))
(s/def ::selection-layers (s/map-of ::db/selection-color ::selection-state))

(defn header-for-selection
  "Return the headers in `visual-headers` as indexed by `selection-rectangle`.

  When the user selects two columns in a single selection rectangle, they can
  do so in any order. (e.g. A higher indexed column first and then a lower indexed one.)
  If they did so, we want to reflect this in the order of the column headers returned
  here unless :ascending true in which case the headers are always returned in
  ascending order."
  [visual-headers selection-rectangle & {:keys [ascending] :or {ascending false}}]
  (let [[_ col-start _ col-end] selection-rectangle
        headers (subvec visual-headers (min col-start col-end) (inc (max col-start col-end)))]
    (if (or ascending (< col-start col-end))
      headers
      (reverse headers))))

(defn get-selected-columns
  "Returns the column names selected in a sequence of selection rectangles, `coords`."
  [coords headers]
  (mapcat #(header-for-selection headers %) coords))

(defn get-selections
  "Returns the data in `rows` corresponding to the selection rectangles in `coords`.

  Data returned is a sequence of maps representing a subset of the data in `rows`.
  If the selection rectangles in `coords` are of different heights or have different starting rows,
  the data rows returned may have mismatched data from different rows in `rows`."
  [coords headers rows]
  (let [data-by-layer (for [layer coords]
                        (let [[r1 _c1 r2 _c2] layer]
                          ;; NOTE: This returns full rows corresponding to the rows in the
                          ;; selection rectangle, but does not subset to the columns selected.
                          ;; This is done intentionally so that full rows are returns as selections.
                          (subvec rows (min r1 r2) (inc (max r1 r2)))))]
    ;; Merging the row-wise data for each selection layer.
    (apply mapv merge data-by-layer)))

(defn get-row-at-selection-start
  "Returns the row in `rows` indexed by the start of the last selection rectangle in `coords`."
  [coords rows]
  (let [[r1 _c1 _r2 _c2] (last coords)]
    (nth rows r1)))

(defn valid-coords?
  "Checks whether the bounds of the selection rectangles in `coords` fit the data table size."
  [coords table-width table-height]
  (if (seq coords)
    (let [check-fn (fn [[r1 c1 r2 c2 :as coords]]
                     (and (every? nat-int? coords)
                          (< r1 table-height)
                          (< r2 table-height)
                          (< c1 table-width)
                          (< c2 table-width)))]
      (every? check-fn coords))
    false))

(defn add-selection-data
  "Returns a map of derived data computed off of `coords`."
  [coords headers rows]
  (when (valid-coords? coords (count headers) (count rows))
    {:coords coords
     :selected-columns (get-selected-columns coords headers)
     :selections (get-selections coords headers rows)
     :row-at-selection-start (get-row-at-selection-start coords rows)}))

(defn selection-layers
  "Merges in data pertaining to the selection-layer-coords

  To be used as re-frame sub."
  [[selection-layer-coords visual-headers visual-display-rows]]
  (medley/map-vals #(add-selection-data % visual-headers visual-display-rows)
                   selection-layer-coords))

(s/fdef selection-layers :ret ::selection-layers)

;;;; Misc defs for working with selections and coordinates.

(defn normalize
  "Removes negative indicies from coords.

  When the row header or column header is clicked in Handsontable, the selection coordinates
  will start at -1 for the start-row or start-header respectively. This normalizes these negative
  indices to 0.

  Args:
    coords - A sequence of selection rectangles.

  Returns:
    nil or a non-empty sequence of normalized selection rectangles."
  [coords]
  (let [fix-selection-rect (fn [[r1 c1 r2 c2]]
                             [(max r1 0) (max c1 0) r2 c2])]
    (not-empty (mapv fix-selection-rect coords))))
