(ns inferenceql.spreadsheets.components.query.events
  (:require [clojure.string :as str]
            [re-frame.core :as rf]
            [inferenceql.query :as query]
            [inferenceql.spreadsheets.panels.table.db :as table-db]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]
            [inferenceql.spreadsheets.model :as model]
            [inferenceql.inference.gpm :as gpm]
            [medley.core :as medley]
            [instaparse.core :as insta]))

(defn errors-for-iql-query
  "Produces errors strings given a `error-map` from inferenceql.query.

  `error-map` may have come from an inferenceql.query.server response or it may have been the
  ex-data on an exception when inferenceql.query was run locally.

  Returns a map with two keys.
    :error-log-msg (string) -- a message to be printed on the browser error log.
    :alert-msg (string) -- a message to be displayed in a js alert popup."
  [error-map]
  (let [{error-category :cognitect.anomalies/category
         instaparse-error :instaparse/failure} error-map
        ;; The instaparse failure will be an instaparse failure object if inferenceql.query ran
        ;; locally and it will be a string if inferenceql.query ran remotely.
        instaparse-fail-msg (cond-> instaparse-error
                              (insta/failure? instaparse-error) (prn-str))]
    (case error-category
      :cognitect.anomalies/incorrect
      (if instaparse-fail-msg
        ;; When information on the parsing error is available.
        {:error-log-msg instaparse-fail-msg
         :alert-msg (str "Your query could not be parsed."
                         "\n"
                         "Open the browser console to see how to fix it.")}
        ;; When information on the parsing error is not available.
        {:error-log-msg "Parse Error: your query could not be parsed."
         :alert-msg (str "Your query could not be parsed. "
                         "\n"
                         "Please check your query.")})
      ;; default case
      {:error-log-msg "There was an unknown error with your query."
       :alert-msg "There was an unknown error with your query."})))

(defn execute-query-locally
  "Executes a query specified by the string `command` locally on the client using inferenceql.query.
  `rows` and `models` are the dataset and models used to run the query.

  Returns an effect map to be used as the return value of a re-frame fx event.

  Effects returned:
    :dispatch [:table/set] -- If the query ran successfully the table will be set with the result
      set.
    :js/console-error -- If the query ran locally and failed, an error message will be output.
    :js/alert -- If the query ran locally and failed, an error message will be output."
  [query rows models]
  (try
    (let [result (query/q query rows models)
          columns (:iql/columns (meta result))]
      ;; TODO: add flag for virtual data.
      ;; This is a map of a re-frame effect to be executed by re-frame.
      {:dispatch [:table/set result columns {:virtual false}]})
    (catch ExceptionInfo e
      (let [error-messages (if (:cognitect.anomalies/category (ex-data e))
                             ;; This case is for a proper iql.query exception.
                             (errors-for-iql-query (ex-data e))

                             ;; Default case.
                             {:error-log-msg (ex-message e)
                              :alert-msg (ex-message e)})
            {:keys [error-log-msg alert-msg]} error-messages]
        ;; This is a map of re-frame effects to be executed by re-frame.
        {:js/console-error error-log-msg
         :js/alert alert-msg}))))

(defn ^:event-fx parse-query
  "Executes the query represented by `text` on the default dataset and model.

  Query execution may happen locally or remotely depending on whether the :query-server-url key is
  present in the app config.

  Triggered when:
    The user presses enter in the query text box or when the `Run InferenceQL` button is pressed.

  Params:
    `text` (string) -- The raw string from the query text box.

  Effects returned:
    :dispatch [:table/set] -- If the query ran locally and successfully the table will be set with
      the result set.
    :js/console-error -- If the query ran locally and failed, an error message will be output.
    :js/alert -- If the query ran locally and failed, an error message will be output."
  [{:keys [db]} [_ text]]
  (let [query (str/trim text)]
    (if false
      ;; Currently a no-op when remote query webserver is specified.
      {}
      ;; Perform query execution locally.
      (let [rows (->> (table-db/dataset-rows db)
                      (map #(medley/remove-vals nil? %)))
            models {:model (gpm/Multimixture model/spec)}]
        (execute-query-locally query rows models)))))

(rf/reg-event-fx :query/parse-query event-interceptors parse-query)
