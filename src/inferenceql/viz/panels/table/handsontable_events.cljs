(ns inferenceql.viz.panels.table.handsontable-events
  "Re-frame events that correspond to hooks in Handsontable."
  (:require [re-frame.core :as rf]
            [inferenceql.viz.panels.table.selections :as selections]
            [inferenceql.viz.panels.table.db :as table-db]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]
            [inferenceql.viz.panels.control.db :as control-db]
            [medley.core :as medley]))

(rf/reg-event-db
 :hot/after-selection-end
 event-interceptors
 (fn [db [_ hot _id _row-index _col _row2 _col2 _selection-layer-level]]
   (let [selection-coords (selections/normalize (js->clj (.getSelected hot)))
         color (control-db/selection-color db)
         num-rows (count (table-db/visual-row-order db))]
     (if (selections/valid-selection? selection-coords num-rows)
       (assoc-in db [:table-panel :selection-layer-coords color] selection-coords)
       (update-in db [:table-panel :selection-layer-coords] dissoc color)))))

(rf/reg-event-db
  :hot/after-on-cell-mouse-down
  event-interceptors
  (fn [db [_ _hot _id mouse-event _coords _TD]]
    (let [alt-key-pressed (.-altKey mouse-event) ;; User held alt during last click.
          color (control-db/selection-color db)]
      (cond-> db
        alt-key-pressed
        ;; Deselect all cells in selection layer on alt-click.
        (update-in [:table-panel :selection-layer-coords] dissoc color)))))

(defn assoc-visual-headers
  "Associates the column headers as displayed by `hot` into `db`.
  This data changes when the user re-orders columns.
  We use this data to along with selection coordinates to produce the data subset selected.
  This all eventually gets passed onto the visualization code via subscriptions."
  [db hot]
  (let [headers (mapv keyword (js->clj (.getColHeader hot)))]
    (assoc-in db [:table-panel :visual-state :headers] headers)))

(defn assoc-visual-row-order
  "Associates the order of rows as displayed by `hot` into `db`.
  This data changes when the user filters, re-orders columns, or sorts columns.
  We use this data to along with selection coordinates to produce the data subset selected.
  This all eventually gets passed onto the visualization code via subscriptions."
  [db hot]
  (let [num-rows-shown (.countRows hot)
        ;; NOTE: Could I do this using the new row index mapper stuff?
        visual-row-indices (range num-rows-shown)
        physical-row-indices (map #(.toPhysicalRow hot %) visual-row-indices)
        physical-row-order (table-db/physical-row-order db)
        visual-row-order (mapv physical-row-order physical-row-indices)]
    (assoc-in db [:table-panel :visual-state :row-order] visual-row-order)))

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
       (assoc-visual-headers hot)
       (assoc-visual-row-order hot))))

(rf/reg-event-db
 :hot/after-filter
 event-interceptors
 (fn [db [_ hot _id _conditions-stack]]
   (assoc-visual-row-order db hot)))

(defn valid-source?
  [source]
  ;; Changes should only be the result of user edits, copy paste, or drag and autofill.
  ;; This should be enforced by Hansontable settings.
  (let [valid-change-sources #{"edit" "CopyPaste.paste" "Autofill.fill"}]
    (some? (valid-change-sources source))))

(defn merge-row-updates
  "Merges `updates` into `rows`.
  Both `updates` and `rows` are a maps where keys are row-ids and vals are rows
  (or row updates) in the case of `updates`."
  [rows updates]
  (let [merge-op (fnil (partial merge-with merge) {} {})
        merged-rows (merge-op rows updates)

        ;; Updates will sometimes have nil or "" as the new value for a particular attribute
        ;; in a row. This means the user has entered "" in the cell or has deleted the cell's value.
        ;; For these cases we want to remove these values and their corresponding keys
        ;; from the map representing the row.
        empty-cell? #(or (nil? %) (= "" %))]

    (reduce (fn [acc [row-id row]]
              (let [clean-row (medley/remove-vals empty-cell? row)]
                (if (seq clean-row)
                  (assoc acc row-id clean-row)
                  acc)))
            {}
            merged-rows)))

(rf/reg-event-fx
  :hot/before-change
  event-interceptors
  (fn [{:keys [db]} [_ hot id changes source]]
    (assert (valid-source? source))
    (let [updates (reduce (fn [acc change]
                            (let [[row col _prev-val new-val] change
                                  row-id (get (table-db/visual-row-order db) row)
                                  col (keyword col)]
                              ;; Changes should only occur in the label column.
                              (assert (= col :label))
                              (assoc-in acc [row-id col] new-val)))
                          {}
                          changes)
          merged-updates (merge-row-updates (get-in db [:table-panel :changes :existing])
                                            updates)]
      ;; Stage the changes in the db. The Handsontable itself already has the updates.
      {:db (assoc-in db [:table-panel :changes :existing] merged-updates)
       :fx [[:dispatch [:control/update-query-string merged-updates]]]})))
