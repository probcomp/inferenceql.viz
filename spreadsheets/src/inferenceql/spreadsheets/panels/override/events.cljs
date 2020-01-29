(ns inferenceql.spreadsheets.panels.override.events
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.db :as db]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]))

(rf/reg-event-db
 :set-modal
 event-interceptors
 (fn [db [_ data]]
   (assoc-in db [::db/modal] data)))

(rf/reg-event-db
 :clear-modal
 event-interceptors
 (fn [db [_]]
   (assoc-in db [::db/modal] {:child nil})))

(rf/reg-event-db
 :set-column-function
 event-interceptors
 (fn [db [_ col-name source-text]]
   (try (if-let [evaled-fn (js/eval (str "(" source-text ")"))]
          (-> db
              (assoc-in [::db/column-overrides col-name] source-text)
              (assoc-in [::db/column-override-fns col-name] evaled-fn))
          db)
        (catch :default e
          (js/alert (str "There was an error evaluating your Javascript function.\n"
                         "See the browser console for more information."))
          (.error js/console e)
          db))))

(rf/reg-event-db
 :clear-column-function
 event-interceptors
 (fn [db [_ col-name]]
   (if (and (get-in db [::db/column-overrides col-name])
            (get-in db [::db/column-override-fns col-name]))
     (-> db
         (update-in [::db/column-overrides] dissoc col-name)
         (update-in [::db/column-override-fns] dissoc col-name))
     db)))
