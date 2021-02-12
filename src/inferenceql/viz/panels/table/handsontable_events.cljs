(ns inferenceql.viz.panels.table.handsontable-events
  "Re-frame events that correspond to hooks in Handsontable."
  (:require [re-frame.core :as rf]
            [inferenceql.viz.panels.table.selections :as selections]
            [inferenceql.viz.panels.table.db :as table-db]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]
            [inferenceql.viz.panels.control.db :as control-db]
            [inferenceql.viz.panels.table.util :refer [merge-row-updates]]
            [inferenceql.viz.components.query.db :refer [query-displayed]]
            [goog.string :refer [format]]
            [clojure.edn :as edn]))

(rf/reg-event-db
 :hot/after-selection-end
 event-interceptors
 (fn [db [_ hot _sub-bundle _row-index _col _row2 _col2 _selection-layer-level]]
   (let [selection-coords (selections/normalize (js->clj (.getSelected hot)))
         color (control-db/selection-color db)
         num-rows (count (table-db/visual-row-order db))]
     (if (selections/valid-selection? selection-coords num-rows)
       (assoc-in db [:table-panel :selection-layer-coords color] selection-coords)
       (update-in db [:table-panel :selection-layer-coords] dissoc color)))))

(rf/reg-event-db
  :hot/after-on-cell-mouse-down
  event-interceptors
  (fn [db [_ _hot _sub-bundle mouse-event _coords _TD]]
    (let [alt-key-pressed (.-altKey mouse-event) ;; User held alt during last click.
          color (control-db/selection-color db)]
      (cond-> db
        alt-key-pressed
        ;; Deselect all cells in selection layer on alt-click.
        (update-in [:table-panel :selection-layer-coords] dissoc color)))))

(defn assoc-visual-headers
  "Associates the column headers as displayed by `hot` into `db`.
  This data changes when the user re-orders columns.
  We use this data to along with selection coordinates to produce the data subset selected.
  This all eventually gets passed onto the visualization code via subscriptions."
  [db hot]
  (let [headers (mapv keyword (js->clj (.getColHeader hot)))]
    (assoc-in db [:table-panel :visual-state :headers] headers)))

(defn assoc-visual-row-order
  "Associates the order of rows as displayed by `hot` into `db`.
  This data changes when the user filters, re-orders columns, or sorts columns.
  We use this data to along with selection coordinates to produce the data subset selected.
  This all eventually gets passed onto the visualization code via subscriptions."
  [db hot physical-row-order]
  (let [num-rows-shown (.countRows hot)
        ;; NOTE: Could I do this using the new row index mapper stuff?
        visual-row-indices (range num-rows-shown)
        physical-row-indices (map #(.toPhysicalRow hot %) visual-row-indices)
        visual-row-order (mapv physical-row-order physical-row-indices)]
    (assoc-in db [:table-panel :visual-state :row-order] visual-row-order)))

(rf/reg-event-db
 :hot/after-column-move
 event-interceptors
 (fn [db [_ hot _sub-bundle _moved-columns _final-index _drop-index _move-possible _order-changed]]
   (assoc-visual-headers db hot)))

;; NOTE: This is also being used to capture the visual row order whenever data is
;; changed in the table or when new rows are added. This is because we always unsort the table
;; first before change it. We may want to use a different Handsontable hook to fulfill this need
;; in the future.
(rf/reg-event-db
 :hot/after-column-sort
 event-interceptors
 (fn [db [_ hot sub-bundle _current-sort-config _destination-sort-config]]
   (-> db
       (assoc-visual-headers hot)
       (assoc-visual-row-order hot (:table/row-order-all sub-bundle)))))

(rf/reg-event-db
 :hot/after-filter
 event-interceptors
 (fn [db [_ hot sub-bundle _conditions-stack]]
   (assoc-visual-row-order db hot (:table/row-order-all sub-bundle))))

(defn valid-source?
  [source]
  ;; Changes should only be the result of user edits, copy paste, drag and autofill,
  ;; and undo. This should be enforced by Hansontable settings.
  (let [valid-change-sources #{"edit" "CopyPaste.paste" "Autofill.fill" "UndoRedo.undo"}]
    (some? (valid-change-sources source))))

(rf/reg-event-fx
  :hot/before-change
  event-interceptors
  (fn [{:keys [db]} [_ hot sub-bundle changes source]]
    (assert (valid-source? source))
    (let [updates (reduce (fn [acc [i change]]
                            (let [[row col _prev-val new-val] change
                                  row-id (get (table-db/visual-row-order db) row)
                                  col (keyword col)
                                  type (get-in sub-bundle [:query/schema col])]

                              ;; Changes should only occur in the label column or in editable row.
                              ;; TODO: check this row is editable instead of true below.
                              (assert (or (= col :label) true))

                              (if (= type :gaussian)
                                ;; Try to cast.
                                (let [new-val (edn/read-string new-val)]
                                  (if (or (number? new-val) (nil? new-val))
                                    ;; Include the change.
                                    (assoc-in acc [row-id col] new-val)
                                    (do
                                      ;; Cancel this change.
                                      (aset changes i nil)
                                      (.error js/console (format "The value '%s' is not a number. New values for column '%s' must be a number." new-val col))
                                      ;; Do not include the change.
                                      acc)))

                                ;; Not gaussian, just include the change.
                                (assoc-in acc [row-id col] new-val))))
                          {}
                          (map-indexed vector changes))

          new-db (update-in db [:table-panel :changes :existing] merge-row-updates updates)]

      ;; Stage the changes in the db. The Handsontable itself already has the updates.
      {:db new-db
       :fx [[:dispatch [:control/update-query-string
                        (query-displayed new-db)
                        (table-db/label-values new-db)
                        (table-db/editable-rows new-db)]]]})))
