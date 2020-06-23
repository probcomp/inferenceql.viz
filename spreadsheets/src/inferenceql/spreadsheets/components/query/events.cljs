(ns inferenceql.spreadsheets.components.query.events
  (:require [clojure.string :as str]
            [re-frame.core :as rf]
            [inferenceql.query :as query]
            [inferenceql.spreadsheets.panels.table.db :as table-db]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]
            [inferenceql.spreadsheets.model :as model]
            [inferenceql.inference.gpm :as gpm]
            [medley.core :as medley]))

(rf/reg-event-fx
  :query/parse-query
  event-interceptors
  (fn [{:keys [db]} [_ text]]
    (let [rows (table-db/dataset-as-iql-query-rows db)
          query (str/trim text)
          models {:model (gpm/Multimixture model/spec)}]
      (try
        (let [result (query/q query rows models)
              columns (:iql/columns (meta result))]
          ;; TODO: add flag for virtual data.
          {:dispatch [:table/set result columns {:virtual false}]})
        (catch ExceptionInfo e
          (let [error-messages
                (case (:cognitect.anomalies/category (ex-data e))
                  :cognitect.anomalies/incorrect
                  (if-let [ip-fail-obj (:inferenceql.query.instaparse/failure (ex-data e))]
                    ;; When information on the parsing error is available.
                    (let [ip-fail-msg (with-out-str (print ip-fail-obj))]
                      {:log-msg (str (ex-message e) "\n" ip-fail-msg)
                       :alert-msg (str "Your query could not be parsed."
                                       "\n"
                                       "Open the browser console to see how to fix it.")})
                    ;; When information on the parsing error is not available.
                    {:log-msg "Parse Error: Could not be parse query."
                     :altert-msg (str "Your query could not be parsed. "
                                      "\n"
                                      "Please check your query.")})

                  ;; default case
                  {:log-msg (ex-message e)
                   :alert-msg (ex-message e)})]
            ;; TODO: These could be their own effects!
            (js/console.error (:log-msg error-messages))
            (js/alert (:alert-msg error-messages))
            {}))))))
