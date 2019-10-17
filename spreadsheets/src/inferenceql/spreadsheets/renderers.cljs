(ns inferenceql.spreadsheets.renderers
  "A cell renderer functions for use in handsontable properties")

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
  "Color missing-cells that have likelihood greater than or equal to `conf-thresh`"
  [renderer-args missing-cells-likelihoods computed-headers conf-thresh]
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

    ;; Color cells when we have likelihoods for the cells loaded
    (when (seq missing-cells-likelihoods)
      (let [likelihoods-for-row (nth missing-cells-likelihoods row)
            likelihood-for-cell (get likelihoods-for-row prop-name-of-cell)]
        ;; When this was a missing cell that we computed a value for.
        (when likelihood-for-cell
          (let [cell-above-thresh (>= likelihood-for-cell conf-thresh)]
            (if cell-above-thresh
              (set! (.-background td-style) color-above-thresh)
              (set! (.-background td-style) color-below-thresh))))))))
