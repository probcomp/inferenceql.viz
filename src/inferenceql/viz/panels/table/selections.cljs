(ns inferenceql.viz.panels.table.selections
  "Items related to selections in Handsontable")

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
