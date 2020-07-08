(ns inferenceql.spreadsheets.panels.table.events
  (:require [re-frame.core :as rf]
            [medley.core :as medley]
            [inferenceql.spreadsheets.panels.table.db :as db]
            [inferenceql.spreadsheets.panels.table.handsontable :as hot]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]
            [inferenceql.spreadsheets.panels.control.db :as control-db]
            [inferenceql.spreadsheets.util :as util]
            [inferenceql.spreadsheets.data :as data]
            [inferenceql.spreadsheets.panels.table.eventsupport.before-change :as es.before-change]
            [inferenceql.spreadsheets.panels.table.eventsupport.toggle-label-column :as es.toggle-label-column]
            [clojure.walk]))

(rf/reg-event-db
 :table/set-hot-instance
 event-interceptors
 (fn [db [_ hot-instance]]
   (assoc-in db [:table-panel :hot-instance] hot-instance)))

(rf/reg-event-db
 :table/unset-hot-instance
 event-interceptors
 (fn [db _]
   (update-in db [:table-panel] dissoc :hot-instance)))

;;; Events that do not correspond to hooks in the Handsontable api.

(rf/reg-event-fx
 :table/set
 event-interceptors
 ;; `rows` and `headers` are required arguments essentially, and
 ;; `scores`, 'labels`, and 'virtual' are optional, and they are meant
 ;; to be passed in a map.
 (fn [{:keys [db]} [_ rows headers {:keys [virtual]}]]
   (let [rows-order (mapv :inferenceql.viz.row/id__ rows)
         rows-maps (zipmap rows-order rows)

         ;; Casts a value to a vec if it is not nil.
         vec-maybe #(some-> % vec)
         ;; Remove special-columns from headers
         headers (remove #{:inferenceql.viz.row/user-added-row__
                           :inferenceql.viz.row/id__
                           :inferenceql.viz.row/label__}
                         headers)
         new-db (-> db
                    (assoc-in [:table-panel :physical-data :rows-by-id] rows-maps)
                    (assoc-in [:table-panel :physical-data :row-order] rows-order)
                    (assoc-in [:table-panel :physical-data :headers] (vec-maybe headers))
                    (util/assoc-or-dissoc-in [:table-panel :physical-data :virtual] virtual)

                    ;; Sets the table visual state to be the same as the new table physical data.
                    (assoc-in [:table-panel :visual-state :row-order] rows-order)
                    (assoc-in [:table-panel :visual-state :headers] (vec-maybe headers))

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

(rf/reg-event-db
  :table/add-row
  event-interceptors
  (fn [db [_]]
    (let [new-row-id (data/generate-row-id)
          color (control-db/selection-color db)

          new-row-num (count (get-in db [:table-panel :physical-data :row-order]))
          ;; Selects the first cell of the new row we are adding.
          new-selection [[new-row-num 0 new-row-num 0]]

          new-row {:inferenceql.viz.row/user-added-row__ true
                   :inferenceql.viz.row/id__             new-row-id}]
      (-> db
          ;; Update the currently displayed data in the table.
          (update-in [:table-panel :physical-data :rows-by-id] assoc new-row-id new-row)
          (update-in [:table-panel :physical-data :row-order] conj new-row-id)
          ;; Update the original dataset.
          (update-in [:table-panel :dataset :rows-by-id] assoc new-row-id new-row)
          (update-in [:table-panel :dataset :row-order] conj new-row-id)

          ;; TODO: scroll the viewport to this cell.
          (assoc-in [:table-panel :selection-layers color :coords] new-selection)))))

(rf/reg-event-db
  :table/delete-row
  event-interceptors
  (fn [db [_]]
    (let [color (control-db/selection-color db)
          selections-coords (get-in db [:table-panel :selection-layers color :coords-physical])
          [r1 _c1 r2 _c2] (first selections-coords)

          row-id (get-in db [:table-panel :physical-data :row-order r1])
          user-row-ids (db/user-added-row-ids db)]
      ;; Only remove a row when there is only one selection rectangle and
      ;; it is set on a single row. The number of columns spanned does not matter.
      ;; The selected row must also be a user-added row.
      (if (and (= 1 (count selections-coords))
               (= r1 r2)
               (contains? user-row-ids row-id))
        (-> db
            ;; Update the currently displayed data in the table.
            (update-in [:table-panel :physical-data :rows-by-id] dissoc row-id)
            (update-in [:table-panel :physical-data :row-order] (comp vec (partial remove #{row-id})))
            ;; Update the original dataset.
            (update-in [:table-panel :dataset :rows-by-id] dissoc row-id)
            (update-in [:table-panel :dataset :row-order] (comp vec (partial remove #{row-id})))

            ;; Update the selection to the first cell in the row before the deleted row.
            (assoc-in [:table-panel :selection-layers color :coords] [[(dec r1) 0 (dec r1) 0]]))
        db))))

;;; Events that correspond to hooks in the Handsontable API

(rf/reg-event-db
  :hot/before-change
  event-interceptors
  (fn [db [_ hot id changes source]]
    (let [change-maps (for [[row col _prev-val new-val] changes]
                        (let [p-row (.toPhysicalRow hot row)
                              row-id (get-in db [:table-panel :physical-data :row-order p-row])
                              row-data (get-in db [:table-panel :physical-data :rows-by-id row-id])

                              ;; Handsontable does not save fully qualified names as column names.
                              ;; The label column is the only column that we might change that we save as a
                              ;; fully qualified keyword, so this is a special case for it.
                              col-kw (if (= col (name :label__))
                                         :inferenceql.viz.row/label__
                                         (keyword col))]
                          {:row-id row-id :row-data row-data :col col-kw :new-val new-val}))]

      (es.before-change/assert-permitted-changes change-maps source)

      (let [change-maps-checked (es.before-change/validate-and-cast-changes change-maps)
            valid-change-maps (filter :valid change-maps-checked)
            invalid-change-maps (remove :valid change-maps-checked)

            reduce-changes-for-id (fn [changes]
                                    (let [simple-change-maps (for [change changes]
                                                               {(:col change) (:new-val change)})]
                                      (apply merge simple-change-maps)))
            updates (->> valid-change-maps
                      (group-by :row-id)
                      (medley/map-vals reduce-changes-for-id))]

        (doseq [c-map invalid-change-maps]
          ;; Cancel invalid changes in Handsontable by mutating js-object `changes`.
          (aset changes (:change-index c-map) nil)
          ;; Print reasons why changes are invalid in error log.
          (.error js/console (:error c-map)))

        (-> db
            ;; Update the currently displayed data in the table.
            (update-in [:table-panel :physical-data :rows-by-id] es.before-change/merge-row-updates updates)
            ;; Update the original dataset.
            (update-in [:table-panel :dataset :rows-by-id] es.before-change/merge-row-updates updates))))))

(rf/reg-event-fx
 :hot/after-selection-end
 event-interceptors
 (fn [{:keys [db]} [_ hot id row-index _col _row2 _col2 _selection-layer-level]]
   (let [selection-layers (js->clj (.getSelected hot))
         physical-selection-layers (vec (for [[r1 c1 r2 c2] selection-layers]
                                          (let [rp1 (.toPhysicalRow hot r1)
                                                cp1 (.toPhysicalColumn hot c1)
                                                rp2 (.toPhysicalRow hot r2)
                                                cp2 (.toPhysicalColumn hot c2)]
                                              [rp1 cp1 rp2 cp2])))
         color (control-db/selection-color db)]
     {:db (-> db
            (assoc-in [:table-panel :selection-layers color :coords] selection-layers)
            (assoc-in [:table-panel :selection-layers color :coords-physical] physical-selection-layers))
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
  "Associates data related to the displayed stated of `hot` into `db`.
  The visual table state changes when the user filters, re-orders columns, or sorts columns.
  We use this visual state to along with selection coordinates to produce the data subset selected.
  This all eventually gets passed onto the visualization code via subscriptions."
  [db hot]
  (let [headers (mapv keyword (js->clj (.getColHeader hot)))

        num-rows-shown (.countRows hot)
        physical-row-order-indices (map #(.toPhysicalRow hot %) (range num-rows-shown))
        physical-row-order (db/physical-row-order db)
        visual-row-order (mapv physical-row-order physical-row-order-indices)]
    (-> db
        (assoc-in [:table-panel :visual-state :row-order] visual-row-order)
        (assoc-in [:table-panel :visual-state :headers] headers))))

(rf/reg-event-db
 :hot/after-column-sort
 event-interceptors
 (fn [db [_ hot _id _current-sort-config destination-sort-config]]
   (-> db
       (assoc-in [:table-panel :sort-config] (js->clj destination-sort-config :keywordize-keys true))
       (assoc-visual-table-state hot))))

(rf/reg-event-db
  :hot/after-filter
  event-interceptors
  (fn [db [_ hot _id _conditions-stack]]
    (-> db
        (assoc-visual-table-state hot))))

(rf/reg-event-db
  :hot/after-column-move
  event-interceptors
  (fn [db [_ hot _id _columns _target]]
    (-> db
        (assoc-visual-table-state hot))))

