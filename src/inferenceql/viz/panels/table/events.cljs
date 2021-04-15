(ns inferenceql.viz.panels.table.events
  "Re-frame events related to data and selections in Handsontable"
  (:refer-clojure :exclude [set])
  (:require [re-frame.core :as rf]
            [medley.core :as medley]
            [inferenceql.viz.panels.table.util :refer [merge-row-updates]]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]))

(defn set
  "Sets data in the table.
  To be used as a re-frame event-fx.

  Args:
    `rows`: A collection of maps to be set as rows in the table.
    `headers`: The attributes of the maps in `rows` to display in the table. :rowid must be among
      these attributes as it will be used as the row ids in Handsontable."
  [{:keys [db]} [_ rows headers]]
  (let [row-ids (mapv :rowid rows)
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
                   (assoc-in [:table-panel :physical-data :row-ids] row-ids)
                   (assoc-in [:table-panel :physical-data :rows-by-id] rows-by-id)
                   (assoc-in [:table-panel :physical-data :headers] headers)
                   ;; Data at these paths change with updates in the table.
                   (assoc-in [:table-panel :row-ids] row-ids)
                   (assoc-in [:table-panel :rows-by-id] rows-by-id)
                   ;; Clear all selections in all selection layers.
                   (assoc-in [:table-panel :selection-layer-coords] {}))]
    {:db new-db}))

(rf/reg-event-fx :table/set
                 event-interceptors
                 set)

(rf/reg-event-fx
 :table/clear
 event-interceptors
 (fn [{:keys [db]} [_]]
   (let [new-db (-> db
                    (update-in [:table-panel] dissoc
                               :physical-data :visual-state :rows-by-id :row-ids)
                    (assoc-in [:table-panel :selection-layer-coords] {}))]
     {:db new-db
      :fx [[:dispatch [:query/clear-details]]
           [:dispatch [:viz/clear-pts-store]]]})))

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

(defn add-row
  "Adds a new empty row in both the Handsontable instance and the app-db.
  To be used as a re-frame event-fx.

  Triggered when the user presses the +row button in the control panel UI."
  [{:keys [db]} [_]]
  (let [new-rowid (inc (count (get-in db [:table-panel :row-ids])))
        new-row {:rowid new-rowid :editable true}
        new-db (-> db
                   (update-in [:table-panel :rows-by-id] assoc new-rowid new-row)
                   (update-in [:table-panel :row-ids] conj new-rowid))

        hot (get-in db [:table-panel :hot-instance])]
    {:db new-db
     :hot/add-row [hot new-row]
     :fx [[:dispatch [:control/add-edits-to-query]]]}))

(rf/reg-event-fx :table/add-row
                 event-interceptors
                 add-row)
