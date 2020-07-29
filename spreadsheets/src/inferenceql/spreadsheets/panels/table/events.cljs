(ns inferenceql.spreadsheets.panels.table.events
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.panels.table.db :as db]
            [inferenceql.spreadsheets.panels.table.handsontable :as hot]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]
            [inferenceql.spreadsheets.panels.control.db :as control-db]
            [inferenceql.spreadsheets.util :as util]))

;;; Events that do not correspond to hooks in the Handsontable api.

(rf/reg-event-fx
 :table/set
 event-interceptors
 ;; `rows` and `headers` are required arguments essentially, and
 ;; 'virtual' is optional.
 (fn [{:keys [db]} [_ rows headers {:keys [virtual] :or {:virtual false}}]]
   (let [rows-order (mapv :iql.viz.row/id__ rows)
         rows-maps (zipmap rows-order rows)

         headers (some->> headers
                          ;; Remove special-columns from headers.
                          (remove #{:iql.viz.row/user-added-row__
                                    :iql.viz.row/id__
                                    :iql.viz.row/label__
                                    :iql.viz.row/row-number__})
                          ;; Place the label col and row-number col at the start of the table.
                          (concat [:iql.viz.row/label__
                                   :iql.viz.row/row-number__])
                          (vec))
         new-db (-> db
                    (assoc-in [:table-panel :physical-data :rows-by-id] rows-maps)
                    (assoc-in [:table-panel :physical-data :row-order] rows-order)
                    (assoc-in [:table-panel :physical-data :headers] headers)
                    (assoc-in [:table-panel :physical-data :virtual] virtual)

                    ;; Sets the table visual state to be the same as the new table physical data.
                    (assoc-in [:table-panel :visual-state :row-order] rows-order)
                    (assoc-in [:table-panel :visual-state :headers] headers)

                    ;; We don't have any changes to the data yet, so :rows-by-id-with-changes
                    ;; should just be the same as :rows-by-id.
                    (assoc-in [:table-panel :physical-data :rows-by-id-with-changes] rows-maps)

                    ;; Clear all selections in all selection layers.
                    (assoc-in [:table-panel :selection-layers] {}))]
     {:db new-db
      :dispatch [:viz/clear-pts-store]})))

(rf/reg-event-fx
 :table/clear
 event-interceptors
 (fn [{:keys [db]} [_]]
   (let [new-db (-> db
                    (update-in [:table-panel] dissoc :physical-data)
                    (update-in [:table-panel] dissoc :visual-state)
                    (assoc-in [:table-panel :selection-layers] {}))]
     {:db new-db
      :dispatch [:viz/clear-pts-store]})))

;; Checks if the selection in the current selection layer is valid.
;; If it is not valid, the current selection layer is cleared.
(rf/reg-event-db
 :table/check-selection
 event-interceptors
 (fn [db _]
   (let [color (control-db/selection-color db)
         selections-coords (get-in db [:table-panel :selection-layers color :coords])

         num-rows (count (db/visual-row-order db))
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

(defn assoc-visual-headers
  "Associates the column headers as displayed by `hot` into `db`.
  This data changes when the user re-orders columns.
  We use this data to along with selection coordinates to produce the data subset selected.
  This all eventually gets passed onto the visualization code via subscriptions."
  [db hot]
  (let [headers (mapv keyword (js->clj (.getColHeader hot)))]
    (-> db
        (assoc-in [:table-panel :visual-state :headers] headers))))

(defn assoc-visual-row-order
  "Associates the order of rows as displayed by `hot` into `db`.
  The data changes when the user filters, re-orders columns, or sorts columns.
  We use this data to along with selection coordinates to produce the data subset selected.
  This all eventually gets passed onto the visualization code via subscriptions."
  [db hot]
  (let [num-rows-shown (.countRows hot)
        physical-row-order-indices (map #(.toPhysicalRow hot %) (range num-rows-shown))
        physical-row-order (db/physical-row-order-all db)
        visual-row-order (mapv physical-row-order physical-row-order-indices)]
    (-> db
        (assoc-in [:table-panel :visual-state :row-order] visual-row-order))))

(rf/reg-event-db
 :hot/after-column-sort
 event-interceptors
 (fn [db [_ hot _id _current-sort-config _destination-sort-config]]
   (-> db
       (assoc-visual-row-order hot))))

(rf/reg-event-db
  :hot/after-filter
  event-interceptors
  (fn [db [_ hot _id _conditions-stack]]
    (-> db
        (assoc-visual-row-order hot))))

(rf/reg-event-db
  :hot/after-column-move
  event-interceptors
  (fn [db [_ hot _id _columns _target]]
    (-> db
        (assoc-visual-headers hot))))

