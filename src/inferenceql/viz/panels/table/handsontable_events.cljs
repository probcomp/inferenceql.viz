(ns inferenceql.viz.panels.table.handsontable-events
  "Re-frame events that correspond to hooks in Handsontable."
  (:require [re-frame.core :as rf]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]
            [inferenceql.viz.panels.table.selections :as selections]
            [inferenceql.viz.panels.table.db :as table-db]
            [inferenceql.viz.panels.table.util :refer [merge-row-updates]]
            [inferenceql.viz.panels.control.db :as control-db]
            [inferenceql.viz.components.query.db :as query-db]
            [inferenceql.viz.components.store.db :as store-db]
            [goog.string :refer [format]]
            [clojure.edn :as edn]
            [clojure.string :as string]))

(rf/reg-event-fx
 :hot/after-selection-end
 event-interceptors
 (fn [{:keys [db]} [_ selections]]
   (let [selection-coords (selections/normalize selections)

         color (control-db/selection-color db)
         num-rows (count (table-db/visual-row-ids db))
         hot (table-db/hot-instance db)]
     (if (selections/valid-selection? selection-coords num-rows)
       {:db (assoc-in db [:table-panel :selection-layer-coords color] selection-coords)}
       ;; Deselect everything.
       {:hot/select [hot nil]}))))

(rf/reg-event-db
 :hot/after-deselect
 event-interceptors
 (fn [db [_]]
   (let [color (control-db/selection-color db)]
     (update-in db [:table-panel :selection-layer-coords] dissoc color))))

(rf/reg-event-db
  :hot/after-on-cell-mouse-down
  event-interceptors
  (fn [db [_ alt-key-pressed]]
    (let [color (control-db/selection-color db)]
      (cond-> db
        alt-key-pressed
        ;; Deselect all cells in selection layer on alt-click.
        (update-in [:table-panel :selection-layer-coords] dissoc color)))))

(rf/reg-event-db
 :hot/after-column-move
 event-interceptors
 (fn [db [_ headers]]
   ;; Associates the column headers as displayed by `hot` into `db`. This data changes when the user
   ;; re-orders columns. We use this data to along with selection coordinates to produce the data
   ;; subset selected. This all eventually gets passed onto the visualization code via
   ;; subscriptions.
   (assoc-in db [:table-panel :visual-state :headers] (mapv keyword headers))))

(rf/reg-event-db
  :hot/after-column-sort
  event-interceptors
  (fn [db [_ headers physical-row-indices]]
    (let [row-ids (get-in db [:table-panel :row-ids])]
      (-> db
          (assoc-in [:table-panel :visual-state :headers] (mapv keyword headers))
          (assoc-in [:table-panel :visual-state :row-ids] (mapv row-ids physical-row-indices))))))

(rf/reg-event-db
  :hot/after-create-row
  event-interceptors
  (fn [db [_ source physical-row-indices]]
    (assert (= source "add-row-fx"))
    (let [row-ids (get-in db [:table-panel :row-ids])]
      (assoc-in db [:table-panel :visual-state :row-ids] (mapv row-ids physical-row-indices)))))

(rf/reg-event-db
  :hot/after-filter
  event-interceptors
  (fn [db [_ physical-row-indices]]
    (let [row-ids (get-in db [:table-panel :row-ids])]
      (assoc-in db [:table-panel :visual-state :row-ids] (mapv row-ids physical-row-indices)))))

(rf/reg-event-fx
  :hot/before-change
  event-interceptors
  ;; This event mutates the changes argument (which is a js-object producted by Handsontable).
  ;; Certain types of mutations allows this event to cancel changes in Handsontable. This approach
  ;; is not the ideal re-frame way.
  (fn [{:keys [db]} [_ changes source]]
    (let [valid-change-sources #{"edit" "CopyPaste.paste" "Autofill.fill" "UndoRedo.undo"}]
      ;; Changes should only be the result of user edits, copy paste, drag and autofill,
      ;; and undo. This should be enforced by Hansontable settings.
      (assert (some? (valid-change-sources source))))
    (let [models (store-db/models db)
          model (get models (query-db/model-name db))
          categories (->> (for [view (vals (:views model))
                                [col-name col] (:columns view)]
                            [col-name (set (:metadata col))])
                          (into {}))

          {:keys [updates errors change-ids-to-cancel]}
          (reduce (fn [acc [i change]]
                    (let [[row col _prev-val new-val] change
                          row-id (get (table-db/visual-row-ids db) row)
                          col (keyword col)
                          editable (get-in db [:table-panel :rows-by-id row-id :editable])
                          type (get (query-db/schema-base db) col)]

                      ;; Changes should only occur in the label column or in editable row.
                      (assert (or (= col :label) editable))

                      (cond
                        (= type :numerical)
                        ;; Try to cast.
                        (let [new-val (edn/read-string new-val)]
                          (if (or (number? new-val) (nil? new-val))
                            ;; Include the change.
                            (assoc-in acc [:updates row-id col] new-val)
                            (let [error (format
                                         (str "Table-edit cancelled: The value '%s' is not a number. "
                                              "New values for column '%s' must be a number.")
                                         new-val (name col))]
                              ;; Do not include the change. Add the error.
                              (-> acc
                                  (update :errors conj error)
                                  (update :change-ids-to-cancel conj i)))))

                        (= type :nominal)
                        ;; Check if valid category-value
                        (let [categories (get categories col)]
                          (if (or (contains? categories new-val) (string/blank? new-val))
                            ;; Include the change.
                            (assoc-in acc [:updates row-id col] new-val)
                            (let [error (format
                                         (str "Table-edit cancelled: The value '%s' is not a valid "
                                              "category value for the column '%s'. Valid categories "
                                              "are %s.")
                                         new-val (name col) (seq categories))]
                              ;; Do not include the change. Add the error.
                              (-> acc
                                  (update :errors conj error)
                                  (update :change-ids-to-cancel conj i)))))

                        (= col :label)
                        ;; Just include the change.
                        (assoc-in acc [:updates row-id col] new-val)

                        :else
                        ;; Trying to edit a column that is either not in the original
                        ;; dateset's schema or the column has been renamed in the query.
                        (let [error (format
                                     (str "Table-edit cancelled: Column '%s' is not part of the "
                                          "original dataset. You can not edit its values.")
                                     (name col))]
                          (-> acc
                              (update :errors conj error)
                              (update :change-ids-to-cancel conj i))))))

                  {:updates {} :errors [] :change-ids-to-cancel []}
                  (map-indexed vector changes))



          ;; Re-frame :fx vectors for emmiting errors to browser console.
          errors (vec (for [error errors]
                        [:js/console-error error]))
          ;; If we have errors, display a js alert message slightly later,
          ;; so we don't lock up Handsontable from undoing changes.
          errors (cond-> errors
                   (seq errors)
                   (conj [:dispatch-later
                          [{:ms 100 :dispatch [:app/alert (str "Your edits were undone because they "
                                                               "contained invalid values or they edited "
                                                               "special columns. "
                                                               "Check the browser console for more "
                                                               "information.")]}]]))]
      ;; Cancel changes by mutating changes.
      (doseq [id change-ids-to-cancel]
        (aset changes id nil))
      ;; Add the changes to db. Handsontable itself already has the updates.
      {:db (update-in db [:table-panel :rows-by-id] merge-row-updates updates)
       :fx (conj errors [:dispatch [:control/add-edits-to-query]])})))

