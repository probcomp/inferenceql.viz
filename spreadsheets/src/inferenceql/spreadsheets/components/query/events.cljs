(ns inferenceql.spreadsheets.components.query.events
  (:require [clojure.string :as str]
            [re-frame.core :as rf]
            [inferenceql.query :as query]
            [inferenceql.spreadsheets.panels.table.db :as table-db]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]
            [inferenceql.spreadsheets.model :as model]
            [inferenceql.inference.gpm :as gpm]))

(rf/reg-event-fx
  :query/parse-query
  event-interceptors
  (fn [{:keys [db]} [_ text label-info]]
    (let [rows (table-db/dataset-rows db)
          command (str/trim text)
          models {:model (gpm/Multimixture model/spec)}]
      (try
        (let [result (query/q command rows models)
              columns (:iql/columns (meta result))]
          ;; TODO: add flag for virtual data.
          {:dispatch [:table/set result columns {:virtual false}]})
        (catch ExceptionInfo e
          (let [error-messages
                (case (:cognitect.anomalies/category (ex-data e))
                  :cognitect.anomalies/incorrect
                  (let [ip-fail-obj (:inferenceql.query.instaparse/failure (ex-data e))
                        ip-fail-msg (with-out-str (print ip-fail-obj))]
                    {:log-msg (str (ex-message e) "\n" ip-fail-msg)
                     :alert-msg (str "Your query could not be parsed."
                                     "\n"
                                     "Open the browser console to see how to fix it.")})

                  ;; default case
                  {:log-msg (ex-message e)
                   :alert-msg (ex-message e)})]
            ;; TODO: These could be their own effects!
            (js/console.error (:log-msg error-messages))
            (js/alert (:alert-msg error-messages))
            {}))))))
