(ns inferenceql.spreadsheets.panels.control.events
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.panels.control.db :as db]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]))

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
   (let [event-list (case path
                      [:confidence-mode]
                      (let [conf-threshold (get-in db [:control-panel :confidence-threshold])
                            new-query-string (query-for-conf-options value conf-threshold)

                            ;; Determine if a load event needs to take place.
                            load-event (cond
                                         (and (= value :row)
                                              (nil? (get db ::db/row-likelihoods)))
                                         [:highlight/compute-row-likelihoods]

                                         (and (= value :cells-missing)
                                              (nil? (get db ::db/missing-cells)))
                                         [:highlight/compute-missing-cells]

                                         ;; Default case: no event
                                         :else
                                         nil)
                            query-string-event [:control/set-query-string new-query-string]]
                        [query-string-event load-event])

                      ;; Default case is empty event list.
                      [])]
     {:db (assoc-in db (into [:control-panel :reagent-forms] path) value)
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
