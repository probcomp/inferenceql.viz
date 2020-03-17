(ns inferenceql.spreadsheets.panels.control.events
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.panels.control.db :as db]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]
            [inferenceql.spreadsheets.components.highlight.db :as highlight-db]
            [clojure.string :as string]))

(defn make-query-string [conditions parts old-query]
  (let [limit-cond (or (re-find #"LIMIT\s\d+" old-query)
                       "LIMIT 1")
        conditions (select-keys conditions [:arabinose :iptg :timepoint])
        experiment-conds (for [[c v] conditions] (str (name c) "=\"" (name v) "\""))
        part-conds (for [[p v] parts] (str (name p) "=" v))
        all-conditions (string/join " AND " (concat experiment-conds part-conds))]
    (str "SELECT ycbj, bdca, ydis, rluc, rsmh FROM \n"
         "(GENERATE * GIVEN " all-conditions " USING model) "
         limit-cond)))

(rf/reg-event-fx
 :control/set-reagent-forms
 event-interceptors
 (fn [{:keys [db]} [_ path value]]
   (let [full-path (into [:control-panel :reagent-forms] path)
         new-db (assoc-in db full-path value)

         parts (get-in new-db [:control-panel :parts])
         conditions (get-in new-db [:control-panel :reagent-forms])
         old-query (get-in new-db [:control-panel :query-string])
         new-query (make-query-string conditions parts old-query)]
     ;; Doing this for all set-reagent-forms events for now.
     {:db new-db
      :dispatch [:control/set-query-string new-query]})))

(rf/reg-event-fx
 :control/set-part
 event-interceptors
 (fn [{:keys [db]} [_ part-key value]]
   (let [new-db (assoc-in db [:control-panel :parts part-key] value)
         parts (get-in new-db [:control-panel :parts])
         conditions (get-in new-db [:control-panel :reagent-forms])
         old-query (get-in new-db [:control-panel :query-string])
         new-query (make-query-string conditions parts old-query)]
     {:db new-db
      :dispatch [:control/set-query-string new-query]})))

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
 :control/set-selection-color
 event-interceptors
 (fn [db [_ value]]
   (assoc-in db [:control-panel :selection-color] value)))
