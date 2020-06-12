(ns inferenceql.spreadsheets.components.query.events
  (:require [clojure.string :as str]
            [re-frame.core :as rf]
            [inferenceql.query :as query]
            [inferenceql.spreadsheets.panels.table.db :as table-db]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]
            [inferenceql.spreadsheets.model :as model]
            [inferenceql.inference.gpm.multimixture.search :as search]
            [inferenceql.inference.gpm :as gpm]
            [medley.core :as medley]))

(def search-by-label-query
  "SELECT (PROBABILITY OF label=\"True\" GIVEN * UNDER model AS prob-label-true), *")
(def ^:private search-column "new-property")
(def ^:private n-models 10)
(def ^:private beta-params {:alpha 0.001, :beta 0.001})

(defn label-set?
  "Converts a label string into a boolean indicating whether the label sets the row as an example."
  [val]
  (when val
    (let [clean-label (str/lower-case (str/trim val))]
      (cond
        ;; Positive example.
        (#{"true" "1" "t"} clean-label)
        true

        ;; Negative example.
        (#{"false" "0" "f"} clean-label)
        false

        ;; Not set.
        :else
        nil))))

(defn make-example
  "Returns an example row built from `row` to be used in the search-by-label search implementation."
  [row]
  (let [label-state (:inferenceql.viz.row/label__ row)
        clean-row (medley/remove-vals nil? (dissoc row
                                                   :inferenceql.viz.row/id__
                                                   :inferenceql.viz.row/user-added-row__
                                                   :inferenceql.viz.row/label__))]
    (merge clean-row {search-column label-state})))

(defn perform-search-by-label
  "Performs a search by label query over `rows`.
  Adds a new column, prob-label-true, to result rows to show resulting scores.
  Returns a re-frame event map to be returned by a re-frame fx-event."
  [rows-by-id row-order rows headers]
  (let [rows-clean-label (map #(update % :inferenceql.viz.row/label__ label-set?) rows)

        labeled-rows (filter (comp some? :inferenceql.viz.row/label__) rows-clean-label)
        labeled-scores (map (comp {true 1 false 0} :inferenceql.viz.row/label__) labeled-rows)
        example-rows (map make-example labeled-rows)

        unlabeled-rows (filter (comp nil? :inferenceql.viz.row/label__) rows-clean-label)
        unlabeled-scores (search/search model/spec search-column example-rows unlabeled-rows n-models beta-params)

        scores-map (zipmap (map :inferenceql.viz.row/id__ (concat unlabeled-rows labeled-rows))
                           (map #(hash-map :prob-label-true %) (concat unlabeled-scores labeled-scores)))

        display-headers (concat [:prob-label-true] headers)
        display-rows-map (merge-with merge rows-by-id scores-map)
        display-rows (map display-rows-map row-order)]
    {:dispatch [:table/set display-rows display-headers {:virtual false}]}))

(defn process-with-iql-query
  "Processes `query` on `rows` using inferenceql.query.
  Returns a re-frame event map to be returned by a re-frame fx-event."
  [query rows]
  ;; Use inferenceql.query to process query.
  (let [models {:model (gpm/Multimixture model/spec)}]
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
          {})))))

(rf/reg-event-fx
  :query/parse-query
  event-interceptors
  (fn [{:keys [db]} [_ text]]
    (let [rows-by-id (table-db/dataset-rows-by-id db)
          row-order (table-db/dataset-row-order db)
          rows (->> (map rows-by-id row-order)
                    (map #(medley/remove-vals nil? %)))
          headers (table-db/dataset-headers db)

          query (str/trim text)]
      (if (= (str/lower-case query) (str/lower-case search-by-label-query))
        (perform-search-by-label rows-by-id row-order rows headers)
        (process-with-iql-query query rows)))))


