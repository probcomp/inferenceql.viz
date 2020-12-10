(ns inferenceql.viz.panels.table.handsontable-events
  "Re-frame events that correspond to hooks in Handsontable."
  (:require [re-frame.core :as rf]
            [inferenceql.viz.panels.table.selections :as selections]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]
            [inferenceql.viz.panels.control.db :as control-db]))

(rf/reg-event-fx
 :hot/after-selection-end
 event-interceptors
 (fn [{:keys [db]} [_ hot _id _row-index _col _row2 _col2 _selection-layer-level]]
   (let [selection-coords (selections/normalize (js->clj (.getSelected hot)))
         color (control-db/selection-color db)]
     {:db (assoc-in db [:table-panel :selection-layer-coords color] selection-coords)
      :dispatch [:table/check-selection]})))

(rf/reg-event-db
  :hot/after-on-cell-mouse-down
  event-interceptors
  (fn [db [_ _hot _id mouse-event _coords _TD]]
    (let [alt-key-pressed (.-altKey mouse-event) ;; User held alt during last click.
          color (control-db/selection-color db)]
      (cond-> db
        alt-key-pressed
        ; Deselect all cells in selection layer on alt-click.
        (update-in [:table-panel :selection-layer-coords] dissoc color)))))

(defn assoc-visual-headers
  "Associates the column headers as displayed by `hot` into `db`.
  This data changes when the user re-orders columns.
  We use this data to along with selection coordinates to produce the data subset selected.
  This all eventually gets passed onto the visualization code via subscriptions."
  [db hot]
  (let [headers (mapv keyword (js->clj (.getColHeader hot)))]
    (assoc-in db [:table-panel :visual-headers] headers)))

(defn assoc-visual-row-data
  "Associates the actual row data as displayed by `hot` into `db`.
  The data changes when the user filters or sorts columns.
  We use this data to along with selection coordinates to produce the data subset selected.
  This all eventually gets passed onto the visualization code via subscriptions."
  [db hot]
  (let [raw-rows (js->clj (.getData hot))
        rows (for [r raw-rows]
               (let [remove-nan (fn [cell] (when-not (js/Number.isNaN cell) cell))]
                 (mapv remove-nan r)))
        headers (mapv keyword (js->clj (.getColHeader hot)))
        row-maps (mapv #(zipmap headers %) rows)]
    (assoc-in db [:table-panel :visual-rows] row-maps)))

(rf/reg-event-db
 :hot/after-column-move
 event-interceptors
 (fn [db [_ hot _id _moved-columns _final-index _drop-index _move-possible _order-changed]]
   (assoc-visual-headers db hot)))

(rf/reg-event-db
 :hot/after-column-sort
 event-interceptors
 (fn [db [_ hot _id _current-sort-config _destination-sort-config]]
   (-> db
       (assoc-visual-row-data hot)
       (assoc-visual-headers hot))))

(rf/reg-event-db
 :hot/after-filter
 event-interceptors
 (fn [db [_ hot _id _conditions-stack]]
   (assoc-visual-row-data db hot)))
