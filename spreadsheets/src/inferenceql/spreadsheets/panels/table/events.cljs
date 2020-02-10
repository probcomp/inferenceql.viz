(ns inferenceql.spreadsheets.panels.table.events
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.panels.table.db :as db]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]))

;;; Events that do not correspond to hooks in the Handsontable api.

(rf/reg-event-db
 :table/search-result
 event-interceptors
 (fn [db [_ result]]
   (db/with-scores db result)))

(rf/reg-event-db
 :table/virtual-search-result
 event-interceptors
 (fn [db [_ result]]
   (db/with-virtual-scores db result)))

(rf/reg-event-db
 :table/clear-virtual-data
 event-interceptors
 (fn [db [event-name]]
   (-> (db/clear-virtual-rows db)
       (db/clear-virtual-scores))))

(rf/reg-event-db
 :table/clear-virtual-scores
 event-interceptors
 (fn [db [event-name]]
   (db/clear-virtual-scores db)))

;;; Events that correspond to hooks in the Handsontable API

;; Used to detect changes in the :real-data handsontable
(rf/reg-event-db
 :hot/before-change
 event-interceptors
 (fn [db [_ hot id changes source]]
   ;; Checks if a specific change is to a cell in column 0.
   (let [change-to-col-0? (fn [change]
                            (let [[_row col _prev-val _new-val] change]
                              (= col 0)))]
     ;; Changes should only be happening in the first column (the label column).
     (assert (every? change-to-col-0? changes))
     ;; Changes should only be the result of user edits.
     (assert (= source "edit")))

   (let [labels-col (.getSourceDataAtCol hot 0)]
     (db/with-labels db (js->clj labels-col)))))

;; Used to detect changes in the :virtual-data handsontable
(rf/reg-event-fx
 :hot/after-change
 event-interceptors
 (fn [{:keys [db]} [_ hot id changes source]]

   ;; `changes` should be null when source of changes is loadData (see docs for why).
   (assert (= nil changes))
   ;; Setting the table's data via the `virtual-hot-props` sub should be the only way it is changing.
   (assert (= source "loadData"))

   (let [table-state (db/table-selection-state db id)]
     (if-let [header-clicked (:header-clicked table-state)]
       (let [current-selection (.getSelectedLast hot)
             [_row1 col1 _row2 col2] (js->clj current-selection)]
         ;; Take the current selection and expand it so the whole columns
         ;; are selected.
         (.selectColumns hot col1 col2))))
   {}))

(rf/reg-event-db
 :hot/after-selection-end
 event-interceptors
 (fn [db [_ hot id row-index col _row2 col2 _prevent-scrolling _selection-layer-level]]
   (let [selected-headers (map #(.getColHeader hot %)
                               (range (min col col2) (inc (max col col2))))
         row (js->clj (zipmap (.getColHeader hot)
                              (.getDataAtRow hot row-index)))
         selected-maps (into []
                             (comp (map (fn [[row col row2 col2]]
                                          (.getData hot row col row2 col2)))
                                   (map js->clj)
                                   (map (fn [rows]
                                          (into []
                                                (map (fn [row]
                                                       (zipmap selected-headers row)))
                                                rows))))
                             (.getSelected hot))
         selected-columns (if (<= col col2) selected-headers (reverse selected-headers))]
     (-> db
         (assoc-in [:table-panel :hot-state id :selected-columns] selected-columns)
         (assoc-in [:table-panel :hot-state id :selections] selected-maps)
         (assoc-in [:table-panel :hot-state id :selected-row-index] row-index)
         (assoc-in [:table-panel :hot-state id :row-at-selection-start] row)))))

(rf/reg-event-db
 :hot/after-on-cell-mouse-down
 event-interceptors
 (fn [db [_ hot id mouse-event coords _TD]]
   (let [other-table-id (db/other-table-id id)

         ;; Stores whether the user clicked on one of the column headers.
         header-clicked-flag (= -1 (.-row coords))

         ;; Stores whether the user held alt during the click.
         alt-key-pressed (.-altKey mouse-event)
         ; Switch the last clicked on table-id to the other table on alt-click.
         new-table-clicked-id (if alt-key-pressed other-table-id id)]

     ; Deselect all cells on alt-click.
     (when alt-key-pressed
       (.deselectCell hot))

     (-> db
         (assoc-in [:table-panel :hot-state id :header-clicked] header-clicked-flag)
         (assoc-in [:table-panel :table-last-clicked] new-table-clicked-id)))))

(rf/reg-event-db
 :hot/after-deselect
 event-interceptors
 (fn [db [_ hot id]]
   ;; clears selections associated with table
   (update-in db [:table-panel :hot-state id] dissoc :selected-columns :selections :selected-row-index :row-at-selection-start)))
