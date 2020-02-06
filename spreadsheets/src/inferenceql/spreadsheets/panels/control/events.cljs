(ns inferenceql.spreadsheets.panels.control.events
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.panels.control.db :as db]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]
            [inferenceql.spreadsheets.components.highlight.db :as highlight-db]))

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
       [:selection-color]
       {:dispatch [:control/set-selection-color full-path value]}

       [:confidence-mode]
       {:dispatch [:control/set-confidence-mode full-path value]}

       ;; Default case is to write the value into the db.
       {:db (assoc-in db full-path value)}))))

(rf/reg-event-db
 :control/set-selection-color
 event-interceptors
 (fn [db [_ path value]]
   ;; Here we only allow setting :selection-color to a non-nil value. The UI selection-color
   ;; selector tries to set a nil value when the same option is clicked twice.
   ;; TODO: fix the above issue.
   (if (some? value)
     (assoc-in db path value)
     db)))

(rf/reg-event-fx
 :control/set-confidence-mode
 event-interceptors
 (fn [{:keys [db]} [_ path value]]
   (let [conf-threshold (get-in db [:control-panel :confidence-threshold])
         new-query-string (query-for-conf-options value conf-threshold)
         query-string-event [:control/set-query-string new-query-string]

         ;; Determine if a load event needs to take place.
         load-event (cond
                      (and (= value :row)
                           (nil? (highlight-db/row-likelihoods db)))
                      [:highlight/compute-row-likelihoods]

                      (and (= value :cells-missing)
                           (nil? (highlight-db/missing-cells db)))
                      [:highlight/compute-missing-cells]

                      ;; Default case: no event
                      :else
                      nil)
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
