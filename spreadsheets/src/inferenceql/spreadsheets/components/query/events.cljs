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
            [instaparse.core :as insta]
            [clojure.edn :as edn]))

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

(def num-mi-samples 100)

(defn execute-mi-query
  [db query _rows models]
  (let [conditions
        (condp re-matches query
          #"(?i)MUTUAL INFO OF \* WITH \* GIVEN ([A-Za-z][A-Za-z0-9_-]*)=(?:(?:\"(.+)\")|(.+)), ([A-Za-z][A-Za-z0-9_-]*)=(?:(?:\"(.+)\")|(.+));?" :>>
          (fn [[_ col-1 val-1-quot val-1 col-2 val-2-quot val-2]]
            {(keyword col-1) (or val-1-quot (edn/read-string val-1))
             (keyword col-2) (or val-2-quot (edn/read-string val-2))})

          #"(?i)MUTUAL INFO OF \* WITH \* GIVEN ([A-Za-z][A-Za-z0-9_-]*)=(?:(?:\"(.+)\")|(.+));?" :>>
          (fn [[_ col-1 val-1-quot val-1]]
            {(keyword col-1) (or val-1-quot (edn/read-string val-1))})

          #"(?i)MUTUAL INFO OF \* WITH \*;?" :>>
          (fn [_]
            {})

          :else
          nil)]

    (if (nil? conditions)
      {:js/alert "There was an error parsing you query.\nPlease check you query."}
      (let [{:keys [model]} models
            _ (.log js/console :conditions----- conditions)

            cols (keys (get-in model [:model :vars]))
            col-pairs-to-compute (for [col-1 cols col-2 cols]
                                   #{col-1 col-2})

            mi-vals (->> (for [pair-set col-pairs-to-compute]
                           (let [[col-1 col-2] (seq pair-set)
                                 col-2 (or col-2 col-1)]
                             [pair-set (gpm/mutual-information model [col-1] [col-2] conditions num-mi-samples)]))
                         (into {}))

            rows (for [col-1 cols col-2 cols]
                   (let [mi (get mi-vals #{col-1 col-2})]
                     {:column-1 col-1 :column-2 col-2 :mi mi}))

            columns [:column-1 :column-2 :mi]]
        {:dispatch [:table/set rows columns {:virtual false :mi true}]}))))

(defn execute-predictive-cols-query
  [query rows models]
  (let [{:keys [model]} models
        matches (re-matches #"(?i)SELECT \(([0-9]+) PREDICTIVE COLUMNS FOR ([A-Za-z][A-Za-z0-9_-]*) UNDER model\).*" query)
        [_whole-match num-cols specified-col] matches]

    (if (not (and num-cols specified-col))
      {:js/alert "There was an error parsing you query.\nPlease check you query."}

      (let [specified-col (keyword specified-col)
            num-cols (edn/read-string num-cols)
            other-cols (remove #{specified-col} (keys (get-in model [:model :vars])))
            mi-vals (for [col other-cols]
                      (let [mi (gpm/mutual-information model [specified-col] [col] {} num-mi-samples)]
                        {:col col :mi mi}))
            col-order (->> mi-vals
                           (sort-by :mi >)
                           (map :col)
                           (take (dec num-cols))
                           (concat [specified-col]))]
        {:dispatch [:table/set rows col-order {:virtual false}]}))))

(defn predictive-cols-query?
  [query]
  (some? (re-matches #"(?i).*PREDICTIVE COLUMNS.*" query)))
(defn mi-query?
  [query]
  (some? (re-matches #"(?i)MUTUAL INFO OF(.*)" query)))

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
  [{:keys [db]} [_ text]]
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
      (let [rows (->> (table-db/dataset-rows db)
                      (map #(medley/remove-vals nil? %)))
            models {:model (gpm/Multimixture model/spec)}]
        (cond
          (mi-query? query)
          (execute-mi-query db query rows models)

          (predictive-cols-query? query)
          (execute-predictive-cols-query query rows models)

          :else
          (execute-query-locally query rows models))))))

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
