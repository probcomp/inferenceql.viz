(ns inferenceql.spreadsheets.panels.table.events
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.panels.table.db :as db]
            [inferenceql.spreadsheets.panels.table.handsontable :as hot]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]))

;;; Events that do not correspond to hooks in the Handsontable api.

(rf/reg-event-db
 :table/search-result
 event-interceptors
 (fn [db [_ result]]
   (db/with-scores db result)))

;;; Events that correspond to hooks in the Handsontable API

;; Used to detect changes in the :real-data handsontable
(rf/reg-event-db
 :hot/before-change
 event-interceptors
 (fn [db [_ hot id changes source]]
   (let [label-col hot/label-col-header]

     ;; Checks if a specific change is to a cell in the label column.
     (let [change-to-label-col? (fn [change]
                                  (let [[_row col _prev-val _new-val] change]
                                    (= col label-col)))]
       ;; Changes should only be happening in the label column.
       (assert (every? change-to-label-col? changes))
       ;; Changes should only be the result of user edits.
       (assert (= source "edit")))

     (let [labels (.getDataAtProp hot label-col)]
       (db/with-labels db (js->clj labels))))))

(rf/reg-event-fx
 :hot/after-selection-end
 event-interceptors
 (fn [{:keys [db]} [_ hot id row-index _col _row2 _col2 _selection-layer-level]]
   (let [selection-layers (.getSelected hot)

         ;; Takes a selection vector and returns true if that selection represents
         ;; the selection of a single column.
         column-selected (fn [[row-start col-start row-end col-end]]
                           (let [last-row-index (- (.countRows hot) 1)]
                             (and (= row-start 0)
                                  (= row-end last-row-index)
                                  (= col-start col-end))))]

     (cond
       ;;; These next two cond sections sometimes deselect all cells in order to enforce our constraints
       ;;; on what sorts of multiple selections are allowed.
       ;;; When we do deselect all cells, we fire the the :hot/deselect-all effect to do it which will
       ;;; eventually cause Handsontable to trigger the :hot/after-deselect event.

       ;; Deselect all if the current two selections are not both single column selections.
       (and (= (count selection-layers) 2)
            (not-every? column-selected selection-layers))
       {:hot/deselect-all hot}

       ;; Deselect all there are more than two selections.
       (> (count selection-layers) 2)
       {:hot/deselect-all hot}

       ;; This cond section only executes when our current selection is permissible. We calculate
       ;; new selection-information for our current selection and store it in the db.
       :else
       (let [header-for-selection (fn [[_ col-start _ col-end]]
                                    (map #(.getColHeader hot %)
                                         (range (min col-start col-end) (inc (max col-start col-end)))))

             data-by-layer (for [layer selection-layers]
                             (let [headers (header-for-selection layer)
                                   [r1 c1 r2 c2] layer]
                               (->> (.getData hot r1 c1 r2 c2)
                                    (js->clj)
                                    (map (fn [row] (zipmap headers row))))))
             ;; Merging the row-wise data for each selection layer.
             selected-data (apply mapv merge data-by-layer)

             ;; Column headers from all the selection layers.
             selected-headers (mapcat header-for-selection selection-layers)

             ;; When the user selects two columns in a single selection layer, they can
             ;; do so in any order. (e.g. A higher indexed column first and then a lower indexed one.)
             ;; If they did so, we want to reflect this in the order of the columns saved in the db.
             ;; This only hapens when we have a single selection layer because code earlier in this
             ;; event enforces this.
             select-order-headers (let [[_ col-start _ col-end] (last selection-layers)]
                                    (if (and (= (count selection-layers) 1)
                                             (> col-start col-end))
                                      (reverse selected-headers)
                                      selected-headers))

             ;; This is the row at the start point of the most recent selection.
             row (js->clj (zipmap (.getColHeader hot)
                                  (.getDataAtRow hot row-index)))]
         {:db (-> db
                  (assoc-in [:table-panel :hot-state id :selected-columns] select-order-headers)
                  (assoc-in [:table-panel :hot-state id :selections] selected-data)
                  (assoc-in [:table-panel :hot-state id :row-at-selection-start] row))})))))

(rf/reg-event-db
 :hot/after-on-cell-mouse-down
 event-interceptors
 (fn [db [_ hot id mouse-event coords _TD]]
   (let [;; Stores whether the user clicked on one of the column headers.
         header-clicked-flag (= -1 (.-row coords))

         ;; Stores whether the user held alt during the click.
         alt-key-pressed (.-altKey mouse-event)]

     ; Deselect all cells on alt-click.
     (when alt-key-pressed
       (.deselectCell hot))

     (-> db
         (assoc-in [:table-panel :hot-state id :header-clicked] header-clicked-flag)))))

(rf/reg-event-db
 :hot/after-deselect
 event-interceptors
 (fn [db [_ hot id]]
   ;; clears selections associated with table
   (update-in db [:table-panel :hot-state id] dissoc :selected-columns :selections :row-at-selection-start)))
