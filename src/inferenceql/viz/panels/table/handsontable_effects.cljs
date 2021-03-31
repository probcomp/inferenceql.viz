(ns inferenceql.viz.panels.table.handsontable-effects
  (:require [re-frame.core :as rf]))

(defn add-row
  "Adds a new row to the Handsontable instance `hot`.
  To be used as a re-frame fx.
  Adds the new row, and selects the first cell in that new row (excluding the label column)."
  [[hot row]]
  (let [row-index (dec (:rowid row))
        values (for [[k v] row]
                 [row-index k v])
        ;; Table coordinates of the first cell of the new row we are adding.
        new-selection [[row-index 1 row-index 1]]]

    ;; Remove any sorting the table may have had.
    (let [sorting-plugin (.getPlugin hot "multiColumnSorting")]
      (.clearSort sorting-plugin))

    ;; Create the new row.
    (.alter hot "insert_row" row-index 1 "add-row-fx")
    ;; Adding data for the new row.
    (.setSourceDataAtCell hot (clj->js values) nil nil "add-row-fx")
    ;; Jump to and select the first cell in the newly created row.
    (.selectCells hot (clj->js new-selection) true)))

(rf/reg-fx :hot/add-row
           add-row)

(defn select
  "Selects the cells specified by `selection` in the Handsontable instance `hot`.
  To be used as a re-frame fx."
  [[hot selection]]
  ;; Update selections.
  (if selection
    (.selectCells hot (clj->js selection) false)
    ;; When coords is nil it means nothing should be selected in the table.
    (.deselectCell hot)))

(rf/reg-fx :hot/select
           select)
