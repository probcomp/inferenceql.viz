(ns inferenceql.spreadsheets.panels.table.events
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.panels.table.db :as db]
            [inferenceql.spreadsheets.panels.table.handsontable :as hot]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]
            [inferenceql.spreadsheets.panels.control.db :as control-db]
            [inferenceql.spreadsheets.util :as util]))

;;; Events that do not correspond to hooks in the Handsontable api.

(rf/reg-event-db
 :table/set
 event-interceptors
 (fn [db [_ rows headers {:keys [virtual]}]]
   (let [vec-maybe #(some-> % vec)] ; Casts a value to a vec if it is not nil.
     (-> db
         (assoc-in [:table-panel :rows] (vec-maybe rows))
         (assoc-in [:table-panel :headers] (vec-maybe headers))
         (util/assoc-or-dissoc-in [:table-panel :virtual] virtual)

         ;; Clear all selections in all selection layers.
         (assoc-in [:table-panel :selection-layers] {})))))

(rf/reg-event-db
 :table/clear
 event-interceptors
 (fn [db [_]]
   (-> db
       (update-in [:table-panel] dissoc :rows :headers :labels :scores)
       (assoc-in [:table-panel :selection-layers] {}))))

;; Checks if the selection in the current selection layer is valid.
;; If it is not valid, the current selection layer is cleared.
(rf/reg-event-db
 :table/check-selection
 event-interceptors
 (fn [db _]
   (let [color (control-db/selection-color db)
         selections-coords (get-in db [:table-panel :selection-layers color :coords])

         num-rows (count (db/visual-rows db))
         ;; Takes a selection vector and returns true if that selection represents
         ;; the selection of a single column.
         column-selected (fn [[row-start col-start row-end col-end]]
                           (let [last-row-index (- num-rows 1)]
                             (and (= row-start 0)
                                  (= row-end last-row-index)
                                  (= col-start col-end))))]
     (cond
       ;; These next two cond sections sometimes deselect all cells in the selection layer in order
       ;; to enforce our constraints on what sorts of selections are allowed in each selection layer.

       ;; Deselect all cells in the current selection layer if it is made up of two selections that
       ;; are not both single column selections.
       (and (= (count selections-coords) 2)
            (not-every? column-selected selections-coords))
       (update-in db [:table-panel :selection-layers] dissoc color)

       ;; Deselect all cells in the current selection layer if it is made up of more than
       ;; two selections.
       (> (count selections-coords) 2)
       (update-in db [:table-panel :selection-layers] dissoc color)

       :else
       db))))

;;; Events that correspond to hooks in the Handsontable API

(rf/reg-event-fx
 :hot/after-selection-end
 event-interceptors
 (fn [{:keys [db]} [_ hot id row-index _col _row2 _col2 _selection-layer-level]]
   (let [selection-layers (.getSelected hot)
         color (control-db/selection-color db)]
     {:db (assoc-in db [:table-panel :selection-layers color :coords] (js->clj selection-layers))
      :dispatch [:table/check-selection]})))

(rf/reg-event-db
 :hot/after-on-cell-mouse-down
 event-interceptors
 (fn [db [_ hot id mouse-event coords _TD]]
   (let [;; Stores whether the user clicked on one of the column headers.
         header-clicked-flag (= -1 (.-row coords))

         ;; Stores whether the user held alt during the click.
         alt-key-pressed (.-altKey mouse-event)
         color (control-db/selection-color db)]

     (if alt-key-pressed
       ; Deselect all cells in selection layer on alt-click.
       (update-in db [:table-panel :selection-layers] dissoc color)
       ;; Otherwise just save whether a header was clicked or not.
       (assoc-in db [:table-panel :selection-layers color :header-clicked] header-clicked-flag)))))

(defn assoc-visual-table-state
  "Associates the displayed stated of `hot` into `db`.
  The visual table state includes data changes caused by filtering, re-ordering columns, sorting columns, etc.
  We use this visual state to along with selection coordinates to produce the data subset selected.
  This gets passed onto the visualization code--all via subscriptions."
  [db hot]
  (let [rows (js->clj (.getData hot))
        headers (mapv keyword (js->clj (.getColHeader hot)))
        row-maps (mapv #(zipmap headers %) rows)]
    (-> db
        (assoc-in [:table-panel :visual-rows] row-maps)
        (assoc-in [:table-panel :visual-headers] headers))))

(rf/reg-event-db
 :hot/after-column-move
 event-interceptors
 (fn [db [_ hot _id _columns _target]]
   (assoc-visual-table-state db hot)))

(rf/reg-event-db
 :hot/after-column-sort
 event-interceptors
 (fn [db [_ hot _id _current-sort-config _destination-sort-configs]]
   (assoc-visual-table-state db hot)))

(rf/reg-event-db
 :hot/after-filter
 event-interceptors
 (fn [db [_ hot _id _conditions-stack]]
   (assoc-visual-table-state db hot)))
