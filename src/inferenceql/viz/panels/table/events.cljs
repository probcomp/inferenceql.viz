(ns inferenceql.viz.panels.table.events
  "Re-frame events related to data and selections in Handsontable"
  (:refer-clojure :exclude [set])
  (:require [re-frame.core :as rf]
            [inferenceql.viz.panels.table.db :as db]
            [inferenceql.viz.panels.table.subs :as table-subs]
            [inferenceql.viz.panels.table.util :refer [merge-row-updates]]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]
            [inferenceql.viz.panels.control.db :as control-db]
            [inferenceql.viz.util :as util]
            [medley.core :as medley]))

(defn set
  "Sets data in the table.
  To be used as a re-frame event-fx.

  Args:
    `rows`: A collection of maps to be set as rows in the table.
    `headers`: The attributes of the maps in `rows` to display in the table. :rowid must be among
      these attributes as it will be used as the row ids in Handsontable."
  [{:keys [db]} [_ rows headers]]
  (let [row-order (mapv :rowid rows)
        rows-by-id (medley/index-by :rowid rows)

        headers (->> headers
                     ;; headers should always look like [:rowid :editable :label ...] because
                     ;; :rowid, :editable, and :label are automatically added to the
                     ;; user's query by iql.viz.

                     ;; :rowid should not be displayed in the table. Instead it extracted
                     ;; from the rows using the Handsontable rowHeaders function.
                     ;; :editable should also not be displayed.

                     (drop 2) ; drop :rowid and :editable
                     (vec))

        new-db (-> db
                   (assoc-in [:table-panel :physical-data :row-order] row-order)
                   (assoc-in [:table-panel :physical-data :rows-by-id] rows-by-id)
                   (assoc-in [:table-panel :physical-data :headers] headers)
                   ;; Clear all selections in all selection layers.
                   (assoc-in [:table-panel :selection-layer-coords] {})
                   ;; Clear changes.
                   (update-in [:table-panel] dissoc :changes))]

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
                    (update-in [:table-panel] dissoc :physical-data :visual-state :changes)
                    (assoc-in [:table-panel :selection-layer-coords] {}))]
     {:db new-db
      :dispatch [:viz/clear-pts-store]})))

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

(rf/reg-event-db :table/toggle-label-column
                 event-interceptors
                 (fn [db [_]]
                   (update-in db [:table-panel :show-label-column] not)))

;------------------------------------------

(defn ^:effect add-row
  [[hot row]]
  (let [row-index (dec (:rowid row))
        values (for [[k v] row]
                 [row-index k v])
        ;; Table coordinates of the first cell of the new row we are adding.
        new-selection [[row-index 1 row-index 1]]]

    ;; Remove any sorting the table may have had.
    (let [sorting-plugin (.getPlugin hot "multiColumnSorting")]
      (.clearSort sorting-plugin))

    ;; Create the new row.
    (.alter hot "insert_row" row-index 1 "add-row-fx")
    ;; Adding data for the new row.
    (.setSourceDataAtCell hot (clj->js values) nil nil "add-row-fx")

    ;; Jump to and select the first cell in the newly created row.
    (.selectCells hot (clj->js new-selection) true)))

(rf/reg-fx :hot/add-row add-row)

(rf/reg-event-fx
 :table/add-row
 event-interceptors
 (fn [{:keys [db]} [_ new-rowid query-displayed rows-by-id row-order schema]]
   (let [new-row {:editable true
                  :rowid new-rowid}
         hot (get-in db [:table-panel :hot-instance])

         new-db (-> db
                    ;; Update changes with the new row.
                    (update-in [:table-panel :changes :existing] assoc new-rowid new-row)
                    (update-in [:table-panel :changes :new-row-order] (fnil conj []) new-rowid))

         changes (get-in new-db [:table-panel :changes :existing])
         row-order-all (vec (concat row-order
                                    (get-in new-db [:table-panel :changes :new-row-order])))


         rows-by-id (merge-row-updates rows-by-id changes)
         rows-all (mapv rows-by-id row-order-all)]

     {:db new-db
      :hot/add-row [hot new-row]
      :fx [[:dispatch [:control/update-query-string
                        query-displayed
                        (table-subs/label-values rows-by-id)
                        (table-subs/editable-rows [rows-all schema])]]]})))

