(ns inferenceql.spreadsheets.components.query.events
  (:require [clojure.string :as str]
            [re-frame.core :as rf]
            [inferenceql.query :as query]
            [inferenceql.spreadsheets.panels.table.db :as table-db]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]
            [inferenceql.spreadsheets.model :as model]
            [inferenceql.inference.gpm :as gpm]
            [inferenceql.spreadsheets.config :as config]
            [medley.core :as medley]
            [day8.re-frame.http-fx]
            [ajax.core]
            [ajax.edn]
            [goog.string :refer [format]]
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
    :http-xhrio -- If the query is to be run remotely, a post request will be effected.
    :js/console-error -- If the query ran locally and failed, an error message will be output.
    :js/alert -- If the query ran locally and failed, an error message will be output."
  [{:keys [db]} [_ text datasets models]]
  (let [{:keys [query-server-url]} config/config
        query (str/trim text)]
    (if query-server-url
      ;; Use the query server as a remote query execution engine.
      {:http-xhrio {:method          :post
                    :uri             query-server-url
                    :params          query
                    :timeout         5000
                    :format          (ajax.core/text-request-format)
                    :response-format (ajax.edn/edn-response-format)
                    :on-success      [:query/post-success]
                    :on-failure      [:query/post-failure]}}
      ;; Perform query execution locally.
      ;; NOTE: We currently assume the ':data' dataset is always being
      ;; referenced in the current query.
      (let [rows (->> (get-in datasets [:data :rows])
                      (map #(medley/remove-vals nil? %)))]
        (execute-query-locally query rows models)))))

(rf/reg-event-fx :query/parse-query event-interceptors parse-query)

(defn ^:event-fx post-success
  "Uses the successful response from the query webserver to display query results.

  Triggered when:
    When the http-xhrio request in :query/parse-query returns successfully.

  Params:
    `result` (map) -- The result of the http request as returned by cljs-ajax.

  Effects returned:
    :dispatch [:table/set] -- Used to display the query resultset in the table."
  [_ [_ result]]
  (let [{result-rows :result metadata :metadata} result
        {columns :iql/columns} metadata]
    ;; TODO: add flag for virtual data.
    {:dispatch [:table/set result-rows columns {:virtual false}]}))

(rf/reg-event-fx :query/post-success event-interceptors post-success)

(defn ^:event-fx post-failure
  "Uses the failure response from the query webserver or cljs-ajax to display error messages.

  Triggered when:
    When the http-xhrio request in :query/parse-query fails.

  Params:
    `result` (map) -- The result of the http request as returned by cljs-ajax.

  Effects returned:
    :js/alert -- Alerts the user of an error.
    :js/console-error -- Logs a more detailed error to the browser console."
  [_ [_ result]]
  (let [{:keys [status response]} result
        default-errors {:alert-msg (str "Your query did not succeed. \n"
                                        "Please see the browser console for more information.")
                        :error-log-msg (str "Your query did not succeed. \n"
                                            "Sorry, no further information is available.")}
        errors (case status
                 0 {:error-log-msg (str "Your request to query server failed. "
                                        "Perhaps the server is not running.")}
                 -1 {:error-log-msg "Your request to query server timed out."}
                 500 {:error-log-msg (str "Your there was an error internal to the query "
                                          "server while processing your query.")}
                 400 (errors-for-iql-query response)
                 {})
        {:keys [error-log-msg alert-msg]} (merge default-errors errors)]
    {:js/console-error error-log-msg
     :js/alert alert-msg}))

(rf/reg-event-fx :query/post-failure event-interceptors post-failure)
