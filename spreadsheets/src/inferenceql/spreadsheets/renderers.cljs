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
  "Color missing-cells that have a likelihood greater than or equal to `conf-thresh`.
  `missing-vals-and-scores` is a row-wise collection of missing values and scores. It has already
  been filtered for missing cells with scores that are >= our `_conf-thresh`."
  [renderer-args missing-vals-and-scores computed-headers _conf-thresh]
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

    ;; Color cells when we have missing-cells information loaded.
    (when (seq missing-vals-and-scores)
      (let [vals-and-scores-for-row (nth missing-vals-and-scores row)
            val-and-score-map-for-cell (get vals-and-scores-for-row prop-name-of-cell)]
        ;; When we are coloring a missing cell that beat our confidence threshold.
        (when val-and-score-map-for-cell
          (if (:meets-threshold val-and-score-map-for-cell)
            (set! (.-background td-style) color-above-thresh)
            (set! (.-background td-style) color-below-thresh)))))))
