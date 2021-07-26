(ns inferenceql.viz.panels.control.events
  (:require [re-frame.core :as rf]
            [inferenceql.viz.events.interceptors :refer [event-interceptors]]
            [inferenceql.viz.components.query.editing :refer [add-edit-exprs add-incorp-expr]]
            [inferenceql.viz.panels.table.db :as table-db]
            [inferenceql.viz.components.query.db :as query-db]
            [inferenceql.viz.components.store.db :as store-db]
            [clojure.string :as str]
            [goog.string :refer [format]]))

(defn query-for-conf-options [type threshold]
  (case type
    :none ""
    :row (str "COLOR ROWS WITH CONFIDENCE OVER " threshold)
    :cells-existing (str "COLOR CELLS EXISTING WITH CONFIDENCE OVER " threshold)
    :cells-missing (str "IMPUTE CELLS MISSING WITH CONFIDENCE OVER " threshold)))

(rf/reg-event-fx
 :control/set-confidence-threshold
 event-interceptors
 (fn [{:keys [db]} [_ value]]
   (let [conf-mode (get-in db [:control-panel :reagent-forms :confidence-mode])
         new-query-string (query-for-conf-options conf-mode value)]
     {:db (assoc-in db [:control-panel :confidence-threshold] value)
      :dispatch [:control/set-query-string new-query-string]})))

(rf/reg-event-fx
 :control/set-reagent-forms
 event-interceptors
 (fn [{:keys [db]} [_ path value]]
   (let [full-path (into [:control-panel :reagent-forms] path)]
     (case path
       [:confidence-mode]
       {:dispatch [:control/set-confidence-mode full-path value]}

       ;; Default case is to write the value into the db.
       {:db (assoc-in db full-path value)}))))

(rf/reg-event-fx
 :control/set-confidence-mode
 event-interceptors
 (fn [{:keys [db]} [_ path value]]
   (let [conf-threshold (get-in db [:control-panel :confidence-threshold])
         new-query-string (query-for-conf-options value conf-threshold)
         query-string-event [:control/set-query-string new-query-string]

         ;; TODO: emit events for computing row-wise probabilities or for imputing missing cells.
         load-event (cond
                      (= value :row) nil
                      (= value :cells-missing) nil
                      :else nil)
         event-list [query-string-event load-event]]
     {:db (assoc-in db path value)
      :dispatch-n event-list})))

(rf/reg-event-db
 :control/update-reagent-forms
 event-interceptors
 (fn [db [_ f path value]]
   (update-in db (into [:control-panel :reagent-forms] path) f value)))

(rf/reg-event-db
 :control/set-query-string
 event-interceptors
 (fn [db [_ new-val]]
   (assoc-in db [:control-panel :query-string] new-val)))

(rf/reg-event-db
  :control/set-query-string-to-select-all
  event-interceptors
  (fn [db _]
    (let [schema (-> (store-db/datasets db)
                     (get-in [:data :schema]))
          columns (->> (map name (keys schema))
                       (str/join ", "))
          query (format "SELECT %s FROM data;" columns)]
      (assoc-in db [:control-panel :query-string] query))))

(rf/reg-event-fx
 :control/set-selection-color
 event-interceptors
 (fn [{:keys [db]} [_ color]]
   (let [hot (table-db/hot-instance db)
         selection (get (table-db/selection-layer-coords db) color)]
     {:db (assoc-in db [:control-panel :selection-color] color)
      :hot/select [hot selection]})))

(rf/reg-event-fx
  :control/add-edits-to-query
  event-interceptors
  (fn [{:keys [db]} _]
    (let [query-last-ran (query-db/query db)
          current-query (get-in db [:control-panel :query-string])

          schema (query-db/schema-base db)
          edited-query (add-edit-exprs current-query
                                       query-last-ran
                                       (table-db/label-values db)
                                       (table-db/editable-rows db schema))]
      (if edited-query
        {:db (assoc-in db [:control-panel :query-string] edited-query)}
        {:js/console-warn "Auto query-editing: Query string could not be parsed and edited."}))))

(rf/reg-event-fx
  :control/incorp-new-vals-in-query
  event-interceptors
  (fn [{:keys [db]} _]
    (let [current-query (get-in db [:control-panel :query-string])
          schema (query-db/schema-base db)
          edited-query (add-incorp-expr current-query
                                        (table-db/label-values db)
                                        (table-db/editable-rows-for-incorp db schema))]
      (if edited-query
        {:db (assoc-in db [:control-panel :query-string] edited-query)}
        {:js/console-warn "Auto query-editing: Query string could not be parsed and edited."}))))
