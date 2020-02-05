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
 :set-confidence-threshold
 event-interceptors
 (fn [{:keys [db]} [_ value]]
   (let [conf-mode (get-in db [:control-panel :confidence-options :mode])
         new-query-string (query-for-conf-options conf-mode value)]
     {:db (assoc-in db [:control-panel :confidence-threshold] value)
      :dispatch [:set-query-string new-query-string]})))

(rf/reg-event-fx
 :set-confidence-options
 event-interceptors
 (fn [{:keys [db]} [_ path value]]
   (let [conf-threshold (get-in db [:control-panel :confidence-threshold])
         new-query-string (query-for-conf-options value conf-threshold)

         ;; Determine if a load event needs to take place.
         load-event (when (= path [:mode])
                      (cond
                        (and (= value :row)
                             (nil? (get db ::db/row-likelihoods)))
                        [:compute-row-likelihoods]

                        (and (= value :cells-missing)
                             (nil? (get db ::db/missing-cells)))
                        [:compute-missing-cells]

                        ;; Default case: no event
                        :else
                        nil))
         query-string-event [:set-query-string new-query-string]
         event-list [query-string-event load-event]]
    {:db (assoc-in db (into [:control-panel :confidence-options] path) value)
     :dispatch-n event-list})))

(rf/reg-event-db
 :update-confidence-options
 event-interceptors
 (fn [db [_ f path value]]
   (update-in db (into [:control-panel :confidence-options] path) f value)))

(rf/reg-event-db
 :set-query-string
 event-interceptors
 (fn [db [_ new-val]]
   (assoc-in db [:control-panel :query-string] new-val)))
