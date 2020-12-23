(ns inferenceql.viz.panels.table.events
  "Re-frame events related to data and selections in Handsontable"
  (:refer-clojure :exclude [set])
  (:require [re-frame.core :as rf]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]
            [inferenceql.viz.panels.control.db :as control-db]
            [inferenceql.viz.util :as util]))

(defn set
  "Sets data in the table.
  To be used as a re-frame event-fx.

  Args:
    `rows`: A collection of maps to be set as rows in the table.
    `headers`: The attributes of the maps in `rows` to display in the table."
  [{:keys [db]} [_ rows headers]]
  (let [new-db (-> db
                   (assoc-in [:table-panel :rows] (vec rows))
                   (assoc-in [:table-panel :headers] (vec headers))
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
