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
          (update-in [:table-panel :visual-state :headers] es.toggle-label-column/adjust-headers shift-amount)))))

(defn add-row
  [[hot row new-selection sort-state]]
  (let [row-number (:inferenceql.viz.row/row-number__ row)
        values (for [[k v] row]
                 (let [row-index (dec row-number)]
                   [row-index k v]))]

    (.removeHook hot "beforeChange" (:hot/before-change hot/real-hot-hooks))
    (.removeHook hot "afterSelectionEnd" (:hot/after-selection-end hot/real-hot-hooks))
    (.removeHook hot "afterColumnSort" (:hot/after-column-sort hot/real-hot-hooks))

    ;; Remove any sorting the table may have had.
    (when (seq sort-state)
      (let [sorting-plugin (.getPlugin hot "multiColumnSorting")]
        (.clearSort sorting-plugin)))
    ;; Adding data for the new row.
    (.setDataAtRowProp hot (clj->js values) nil nil "add-row-event")
    ;; Jump to and select the first cell in the newly created row.
    (.selectCells hot (clj->js new-selection) true)

    (.addHook hot "beforeChange" (:hot/before-change hot/real-hot-hooks))
    (.addHook hot "afterSelectionEnd" (:hot/after-selection-end hot/real-hot-hooks))
    (.addHook hot "afterColumnSort" (:hot/after-column-sort hot/real-hot-hooks))))

(rf/reg-fx :hot/add-row add-row)

(rf/reg-event-fx
  :table/add-row
  event-interceptors
  (fn [{:keys [db]} [_]]
    (let [new-row-id (data/generate-row-id)
          color (control-db/selection-color db)

          new-row-coord (count (db/physical-row-order-all db))

          ;; Table coordinates of the first cell of the new row we are adding.
          new-selection [[new-row-coord 0 new-row-coord 0]]

          new-row-num (inc new-row-coord)
          new-row {:inferenceql.viz.row/user-added-row__ true
                   :inferenceql.viz.row/id__ new-row-id
                   :inferenceql.viz.row/row-number__ new-row-num}

          hot (get-in db [:table-panel :hot-instance])
          hot-sort-state (get-in db [:table-panel :sort-state])]

      {:hot/add-row [hot new-row new-selection hot-sort-state]
       :db (-> db
               ;; Update staged data with the new row.
               (update-in [:table-panel :physical-data :staged-changes] assoc new-row-id new-row)
               (update-in [:table-panel :physical-data :staged-row-order-for-new-rows] (fnil conj []) new-row-id)

               ;; Sets the table visual state to be the same as the new table physical data
               ;; (including user-added staged rows).
               (assoc-in [:table-panel :visual-state :row-order] (conj (db/physical-row-order-all db) new-row-id))

               (assoc-in [:table-panel :selection-layers color :coords] new-selection)
               (assoc-in [:table-panel :sort-state] []))})))

(defn update-row-numbers [rows-by-id row-number]
  (medley/map-vals (fn [row]
                     (if (> (:inferenceql.viz.row/row-number__ row)
                            row-number)
                       (update row :inferenceql.viz.row/row-number__ dec)
                       row))
                   rows-by-id))

(rf/reg-event-db
  :table/delete-row
  event-interceptors
  (fn [db [_]]
    (let [color (control-db/selection-color db)

          selections-coords (get-in db [:table-panel :selection-layers color :coords])
          [r1 _c1 r2 _c2] (first selections-coords)

          row-id (get-in db [:table-panel :visual-state :row-order r1])
          row (get-in db [:table-panel :physical-data :rows-by-id row-id])
          row-number (get row :inferenceql.viz.row/row-number__)

          user-row-ids (db/user-added-row-ids db)

          staged-changes (-> (get-in db [:table-panel :physical-data :staged-changes])
                             ;; Remove any staged changes related to the row we are removing.
                             (dissoc row-id))]

      ;; Only remove a row when there is only one selection rectangle and
      ;; it is set on a single row. The number of columns spanned does not matter.
      ;; The selected row must also be a user-added row.
      (if (and (= 1 (count selections-coords))
               (= r1 r2)
               (contains? user-row-ids row-id))
        (-> db
            ;; Remove the row from the dataset.
            (update-in [:table-panel :physical-data :rows-by-id] dissoc row-id)
            (update-in [:table-panel :physical-data :row-order] (comp vec (partial remove #{row-id})))

            ;; Update row numbers for subsequent rows.
            (update-in [:table-panel :physical-data :rows-by-id] update-row-numbers row-number)

            ;; Incorporate staged changes.
            (update-in [:table-panel :physical-data :rows-by-id] es.before-change/merge-row-updates staged-changes)
            (update-in [:table-panel :physical-data] dissoc :staged-changes)

            ;; Update the selection to the first cell in the row before the deleted row.
            (assoc-in [:table-panel :selection-layers color :coords] [[(dec r1) 0 (dec r1) 0]]))
        db))))

(rf/reg-event-db
  :table/jump-to-selection-done
  event-interceptors
  (fn [db [_]]
    (assoc-in db [:table-panel :behavior :jump-to-selection] false)))

;;; Events that correspond to hooks in the Handsontable API

(rf/reg-event-db
  :hot/before-change
  event-interceptors
  (fn [db [_ hot id changes source]]
    (let [physical-rows-by-id-all (es.before-change/merge-row-updates (db/physical-rows-by-id db)
                                                                      (db/physical-staged-changes db))
          change-maps (for [[row col _prev-val new-val] changes]
                        (let [row-id (get (db/visual-row-order db) row)
                              row-data (get physical-rows-by-id-all row-id)

                              ;; Our special row attrs are saved as fully qualified keywords in the app-db.
                              ;; However, when they become column names in Handsontable, they are not fully qualified.
                              ;; The label column is the only column that changes that refers to a special row attr,
                              ;; so we have a special case here for referring to the correct fully qualified keyword.
                              col-kw (if (= col (name :inferenceql.viz.row/label__)) ;; this becomes (= col "label__")
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
            ;; Stage the changes in the db. The Handsontable itself already has the updates.
            (update-in [:table-panel :physical-data :staged-changes] es.before-change/merge-row-updates updates))))))

(defn valid-selection?
  "Checks if the selection in the current selection layer is valid."
  ;; TODO: update comments.
  [selections-coords num-rows]
  (let [;; Takes a selection vector and returns true if that selection represents
        ;; the selection of a single column.
        column-selected? (fn [[row-start col-start row-end col-end]]
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
           (not-every? column-selected? selections-coords))
      false

      ;; Deselect all cells in the current selection layer if it is made up of more than
      ;; two selections.
      (> (count selections-coords) 2)
      false

      :else
      true)))

(rf/reg-event-db
 :hot/after-selection-end
 event-interceptors
 (fn [db [_ hot id row-index _col _row2 _col2 _selection-layer-level]]
   (let [color (control-db/selection-color db)
         selection-layers (js->clj (.getSelected hot))
         num-rows (count (db/visual-row-order db))]

     (if (valid-selection? selection-layers num-rows)
       (-> db
           (assoc-in [:table-panel :selection-layers color :coords] selection-layers))
       (-> db
           (update-in [:table-panel :selection-layers] dissoc color))))))

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
 (fn [db [_ hot _id _current-sort-config destination-sort-config]]
   (-> db
       (assoc-in [:table-panel :sort-state] (js->clj destination-sort-config :keywordize-keys true))
       (assoc-visual-row-order hot)
       (assoc-visual-headers hot))))

(rf/reg-event-db
  :hot/after-filter
  event-interceptors
  (fn [db [_ hot _id _conditions-stack]]
    (-> db
        (assoc-visual-row-order hot)
        (assoc-visual-headers hot))))

(rf/reg-event-db
  :hot/after-column-move
  event-interceptors
  (fn [db [_ hot _id _columns _target]]
    (-> db
        (assoc-visual-row-order hot)
        (assoc-visual-headers hot))))

