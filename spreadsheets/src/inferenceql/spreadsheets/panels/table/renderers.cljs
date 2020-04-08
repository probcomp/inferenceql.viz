(ns inferenceql.spreadsheets.panels.table.renderers
  "A cell renderer functions for use in handsontable properties")

;; TODO: Add a new renderer for rendering data at this attribute. into a vega spec.
;; Attach a div on the td for the cell, and call vega-embed on it with a spec.

(defn row-wise-likelihood-threshold-renderer
  "Colors rows that have likelihood greater than or equal to `conf-thresh`"
  [renderer-args row-likelihoods conf-thresh]
  (let [renderer-args-js (clj->js renderer-args)
        [hot td row _col _prop _value _cell-properties] renderer-args

        ;; Using physical coords makes rendering resilient to sorting the table.
        row (.toPhysicalRow hot row)

        td-style (.-style td)
        text-render-fn js/Handsontable.renderers.TextRenderer]

    ;; Performs standard rendering of text in cell
    (this-as this
      (.apply text-render-fn this renderer-args-js))

    ;; Colors rows when we have likelihoods loaded
    (when (seq row-likelihoods)
      (let [likelihood-for-row (nth row-likelihoods row)
            row-above-thresh (>= likelihood-for-row conf-thresh)]
        (when row-above-thresh
          (set! (.-background td-style) "#CEC"))))))

(defn missing-cell-wise-likelihood-threshold-renderer
  "Color missing-cells based on whether they meet the set confidence threshold or not.
  `missing-vals-and-scores` is a row-wise collection maps. The map for each row has keys of column
  names and vals of maps of this form. {:score _ :value _ :meets-threshold _ }."
  [renderer-args missing-vals-and-scores computed-headers]
  (let [renderer-args-js (clj->js renderer-args)
        [hot td row col _prop _value _cell-properties] renderer-args

        ;; Using physical coords makes rendering resilient to sorting the table.
        row (.toPhysicalRow hot row)
        col (.toPhysicalColumn hot col)

        td-style (.-style td)
        text-render-fn js/Handsontable.renderers.TextRenderer

        prop-name-of-cell (nth computed-headers col)

        color-above-thresh "#CEC"
        color-below-thresh "#DDD"]
    ;; Performs standard rendering of text in cell
    (this-as this
      (.apply text-render-fn this renderer-args-js))

    ;; Perform coloring when we have missing-cells information loaded.
    (when-let [mvs (not-empty missing-vals-and-scores)]
      ;; When current cell being colored has missing cell info.
      (when-let [cell-info (get-in mvs [row prop-name-of-cell])]
        (let [color (if (:meets-threshold cell-info)
                        color-above-thresh
                        color-below-thresh)]
          (set! (.-background td-style) color))))))
