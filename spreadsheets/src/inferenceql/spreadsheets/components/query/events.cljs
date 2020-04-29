(ns inferenceql.spreadsheets.components.query.events
  (:require [clojure.core.match :refer [match]]
            [clojure.string :as str]
            [re-frame.core :as rf]
            [instaparse.core :as insta]
            [metaprob.prelude :as mp]
            [inferenceql.multimixture :as mmix]
            [inferenceql.multimixture.search :as search]
            [inferenceql.spreadsheets.db :as db]
            [inferenceql.spreadsheets.panels.table.db :as table-db]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]
            [inferenceql.spreadsheets.model :as model]
            [inferenceql.spreadsheets.query :as query]
            [inferenceql.spreadsheets.panels.override.helpers :as co]))

(def ^:private search-column "new property")
(def ^:private n-models 10)
(def ^:private beta-params {:alpha 0.001, :beta 0.001})

(rf/reg-event-fx
 :query/parse-query
 event-interceptors
 (fn [{:keys [db]} [_ text label-info]]
   (let [rows (table-db/dataset-rows db)
         command (str/trim text)
         models {:model (search/optimized-row-generator model/spec)}

         result (query/q command rows models)
         columns (:iql/columns (meta result))
         virtual (:iql/is-virtual-data (meta result))]
    (if-not (insta/failure? result)
      {:dispatch [:table/set result columns {:virtual virtual}]}
      (let [logged-msg (str "Invalid query syntax: \n" (with-out-str (print result)))
            alerted-msg "Invalid query syntax."]
        ;; TODO: These could be their own effects!
        (js/console.error logged-msg)
        (js/alert alerted-msg)
        {})))))

(rf/reg-event-fx
 :query/display-dataset
 event-interceptors
 (fn [{:keys [db]} [_]]
   (let [rows (table-db/dataset-rows db)
         headers (table-db/dataset-headers db)]
     {:dispatch [:table/set rows headers]})))

(rf/reg-event-fx
 :query/search-by-example
 event-interceptors
 (fn [{:keys [db]} [_ example-row]]
   (let [rows (table-db/dataset-rows db)
         headers (table-db/dataset-headers db)
         search-row (merge example-row {search-column true})
         result (search/search model/spec search-column [search-row] rows n-models beta-params {})]
     {:dispatch [:table/set rows headers {:scores result}]})))

(rf/reg-event-fx
 :query/anomaly-search
 event-interceptors
 (fn [{:keys [db]} [_ target-col conditional-cols]]
   (let [rows (table-db/dataset-rows db)
         headers (table-db/dataset-headers db)
         result (search/anomaly-search model/spec target-col conditional-cols rows)]
     {:dispatch [:table/set rows headers {:scores result}]})))

(rf/reg-event-fx
 :query/generate-virtual-row
 event-interceptors
 (fn [{:keys [db]} [_ conditions num-rows]]
   (let [constraint-addrs-vals (mmix/with-row-values {} conditions)
         gen-fn #(first (mp/infer-and-score
                         :procedure (search/optimized-row-generator model/spec)
                         :observation-trace constraint-addrs-vals))
         has-negative-vals? #(some (every-pred number? neg?) (vals %))

         overrides-map (get-in db [:override-panel :column-override-fns])
         overrides-insert-fn (co/gen-insert-fn overrides-map)

         ;; TODO: '(remove negative-vals? ...)' is hack for StrangeLoop2019
         new-rows (take num-rows (map overrides-insert-fn (remove has-negative-vals? (repeatedly gen-fn))))
         headers (table-db/dataset-headers db)]
     {:dispatch [:table/set new-rows headers {:virtual true}]})))

(defn- create-search-examples [pos-rows neg-rows]
  (let [remove-nil-key-vals #(into {} (remove (comp nil? second) %))
        pos-rows-examples (->> pos-rows
                               (map #(merge % {search-column true}))
                               (map remove-nil-key-vals))
        neg-rows-examples (->> neg-rows
                               (map #(merge % {search-column false}))
                               (map remove-nil-key-vals))
        examples (concat pos-rows-examples neg-rows-examples)]
    examples))

(defn- create-scores-map-for-labeled-rows [pos-ids neg-ids]
  (let [pos-ids-map (zipmap pos-ids (repeat 1))
        neg-ids-map (zipmap neg-ids (repeat 0))]
    (merge pos-ids-map neg-ids-map)))

(rf/reg-event-fx
 :query/search-by-labeled
 event-interceptors
 (fn [{:keys [db]} [_ pos-ids neg-ids unlabeled-ids]]
   (let [rows (table-db/dataset-rows db)
         headers (table-db/dataset-headers db)
         raw-labels (table-db/labels db)

         pos-rows (map rows pos-ids)
         neg-rows (map rows neg-ids)
         unlabeled-rows (map rows unlabeled-ids)

         example-rows (create-search-examples pos-rows neg-rows)

         scores (search/search model/spec search-column example-rows unlabeled-rows n-models beta-params {})
         scores-ids-map (zipmap unlabeled-ids scores)

         scores-ids-map-lab (create-scores-map-for-labeled-rows pos-ids neg-ids)

         all-scores (->> (merge scores-ids-map scores-ids-map-lab)
                         (sort-by key)
                         (map second))]
     {:dispatch [:table/set rows headers {:scores all-scores :labels raw-labels}]})))
