(ns inferenceql.spreadsheets.panels.table.effects
  (:require [re-frame.core :as rf]))

(rf/reg-fx
  :hot/deselect-all
  ;; Deselects all cells in a Handsontable instance.
  (fn [hot]
    (.deselectCell hot)))
