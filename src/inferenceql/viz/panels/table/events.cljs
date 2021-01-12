(ns inferenceql.viz.panels.table.events
  "Re-frame events related to data and selections in Handsontable"
  (:refer-clojure :exclude [set])
  (:require [re-frame.core :as rf]
            [inferenceql.viz.panels.table.db :as db]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]
            [inferenceql.viz.panels.control.db :as control-db]
            [inferenceql.viz.util :as util]))

(defn set
  "Sets data in the table.
  To be used as a re-frame event-fx.

  Args:
    `rows`: A collection of maps to be set as rows in the table.
    `headers`: The attributes of the maps in `rows` to display in the table. :rowid must be among
      these attributes as it will be used as the row ids in Handsontable."
  [{:keys [db]} [_ rows headers]]
  (let [new-db (-> db
                   (assoc-in [:table-panel :rows] (vec rows))
                   ;; :rowid should not be displayed in the table. Instead it extracted
                   ;; from the rows using the Handsontable rowHeaders function.
                   (assoc-in [:table-panel :headers] (vec (remove #{:rowid} headers)))
                   ;; Clear all selections in all selection layers.
                   (assoc-in [:table-panel :selection-layer-coords] {}))]
    {:db new-db
     ;; Clear previous selections made in vega-lite plots.
     :dispatch [:viz/clear-pts-store]}))

(rf/reg-event-fx :table/set
                 event-interceptors
                 set)

(rf/reg-event-fx
 :table/clear
 event-interceptors
 (fn [{:keys [db]} [_]]
   (let [new-db (-> db
                    (update-in [:table-panel] dissoc :rows :headers)
                    (assoc-in [:table-panel :selection-layer-coords] {}))]
     {:db new-db
      :dispatch [:viz/clear-pts-store]})))

;; Checks if the selection in the current selection layer is valid.
;; If it is not valid, the current selection layer is cleared.
(rf/reg-event-db
 :table/check-selection
 event-interceptors
 (fn [db _]
   (let [color (control-db/selection-color db)
         selections-coords (get-in db [:table-panel :selection-layer-coords color])

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
       (update-in db [:table-panel :selection-layer-coords] dissoc color)

       ;; Deselect all cells in the current selection layer if it is made up of more than
       ;; two selections.
       (> (count selections-coords) 2)
       (update-in db [:table-panel :selection-layer-coords] dissoc color)

       :else
       db))))

(defn set-hot-instance
  "To be used as re-frame event-db."
  [db [_ hot-instance]]
  (assoc-in db [:table-panel :hot-instance] hot-instance))

(rf/reg-event-db :table/set-hot-instance
                 event-interceptors
                 set-hot-instance)

(defn unset-hot-instance
  "To be used as re-frame event-db."
  [db _]
  (update-in db [:table-panel] dissoc :hot-instance))

(rf/reg-event-db :table/unset-hot-instance
                 event-interceptors
                 unset-hot-instance)
