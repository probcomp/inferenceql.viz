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
 ;; `rows` and `headers` are required arguments essentially, and
 ;; `scores`, 'labels`, and 'virtual' are optional, and they are meant
 ;; to be passed in a map.
 (fn [db [_ rows headers {:keys [scores labels virtual]}]]
   (-> db
       (assoc-in [:table-panel :rows] rows)
       (assoc-in [:table-panel :headers] headers)
       (util/assoc-or-dissoc-in [:table-panel :scores] scores)
       (util/assoc-or-dissoc-in [:table-panel :labels] labels)
       (util/assoc-or-dissoc-in [:table-panel :virtual] virtual)

       ;; Clear all selections in all selection layers.
       (assoc-in [:table-panel :selection-layers] {}))))

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

         num-rows (count (db/table-rows db))
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

;; Used to detect changes in the :real-data handsontable
(rf/reg-event-db
 :hot/before-change
 event-interceptors
 (fn [db [_ hot id changes source]]
   (let [change-maps (for [change changes]
                       (let [[row col prev-val new-val] change
                             p-row (.toPhysicalRow hot row)]
                         {:row p-row :col col :prev-val prev-val :new-val new-val}))

         label-col hot/label-col-header
         ;; Changes should only be the result of user edits, copy paste, or drag and autofill.
         valid-change-sources #{"edit" "CopyPaste.paste" "Autofill.fill"}]
      ;; Changes should only be happening in the label column.
      (assert (every? #{label-col} (map :col change-maps)))
      (assert (valid-change-sources source))

      (let [num-rows (count (db/table-rows db))
            ;; Get the current vector of lables in the db or make new vector of nils.
            default-labels (vec (repeat num-rows nil))
            labels (or (db/labels db) default-labels)

            row-new-vals (mapcat (juxt :row :new-val) change-maps)
            ;; Apply the changes to the labels.
            labels-changed (apply assoc labels row-new-vals)]
        (db/with-labels db labels-changed)))))

;; Gets selection information from Handsontable, transforms it, and saves it to the db.
(rf/reg-event-fx
 :hot/after-selection-end
 event-interceptors
 (fn [{:keys [db]} [_ hot id row-index _col _row2 _col2 _selection-layer-level]]
   (let [selection-layers (.getSelected hot)
         header-for-selection (fn [[_ col-start _ col-end]]
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
         select-order-headers (let [[_ col-start _ col-end] (last selection-layers)]
                                (if (and (= (count selection-layers) 1)
                                         (> col-start col-end))
                                  (reverse selected-headers)
                                  selected-headers))

         ;; This is the row at the start point of the most recent selection.
         row (js->clj (zipmap (.getColHeader hot)
                              (.getDataAtRow hot row-index)))

         color (control-db/selection-color db)]
     {:db (-> db
              (assoc-in [:table-panel :selection-layers color :selected-columns] select-order-headers)
              (assoc-in [:table-panel :selection-layers color :selections] selected-data)
              (assoc-in [:table-panel :selection-layers color :row-at-selection-start] row)
              (assoc-in [:table-panel :selection-layers color :coords] (js->clj selection-layers)))
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
