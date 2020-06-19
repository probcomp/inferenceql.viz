(ns inferenceql.spreadsheets.panels.table.events
  (:require [re-frame.core :as rf]
            [medley.core :as medley]
            [inferenceql.spreadsheets.panels.table.db :as db]
            [inferenceql.spreadsheets.panels.table.handsontable :as hot]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]
            [inferenceql.spreadsheets.panels.control.db :as control-db]
            [inferenceql.spreadsheets.panels.table.event-support.toggle-label-column :as es.toggle-label-column]
            [inferenceql.spreadsheets.util :as util]))

;;; Events that do not correspond to hooks in the Handsontable api.

(rf/reg-event-db
 :table/set
 event-interceptors
 (fn [db [_ rows headers {:keys [virtual]}]]
   (let [rows-order (mapv :inferenceql.viz.row/id__ rows)
         rows-maps (zipmap rows-order rows)

         ;; Casts a value to a vec if it is not nil.
         vec-maybe #(some-> % vec)
         ;; Remove special-columns from headers as we don't want to display
         ;; them in the table.
         headers (remove #{:inferenceql.viz.row/id__}
                         headers)]
     (-> db
         (assoc-in [:table-panel :physical-data :rows-by-id] rows-maps)
         (assoc-in [:table-panel :physical-data :row-order] rows-order)
         (assoc-in [:table-panel :physical-data :headers] (vec-maybe headers))
         (util/assoc-or-dissoc-in [:table-panel :physical-data :virtual] virtual)

         ;; Clear all selections in all selection layers.
         (assoc-in [:table-panel :selection-layers] {})))))

(rf/reg-event-db
 :table/clear
 event-interceptors
 (fn [db [_]]
   (-> db
       (update-in [:table-panel] dissoc :physical-data)
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

(rf/reg-event-db
  :table/toggle-label-column
  event-interceptors
  (fn [db [_]]
    (let [new-state (not (get-in db [:table-panel :label-column-show]))
          shift-amount (if new-state 1 -1)]
      (-> db
          (assoc-in [:table-panel :label-column-show] new-state)
          ;; :sort-state is not always present, hence the special update function.
          (medley/update-existing-in [:table-panel :sort-state] es.toggle-label-column/shift-sort shift-amount)
          (update-in [:table-panel :selection-layers] es.toggle-label-column/shift-selections shift-amount)
          ;; Hack: This prevents a flicker in visualizations.
          (update-in [:table-panel :visual-data :headers] es.toggle-label-column/adjust-headers shift-amount)))))

;;; Events that correspond to hooks in the Handsontable API

;; Used to detect changes in the :real-data handsontable
(rf/reg-event-db
 :hot/before-change
 event-interceptors
 (fn [db [_ hot id changes source]]
   db))

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
        (assoc-in [:table-panel :visual-data :rows] row-maps)
        (assoc-in [:table-panel :visual-data :headers] headers))))

(rf/reg-event-db
 :hot/after-change
 event-interceptors
 (fn [db [_ hot _id _changes _source]]
   (assoc-visual-table-state db hot)))

(rf/reg-event-db
 :hot/after-column-sort
 event-interceptors
 (fn [db [_ hot _id _current-sort-config destination-sort-config]]
   (assoc-in db [:table-panel :sort-state]
             (js->clj destination-sort-config :keywordize-keys true))))

