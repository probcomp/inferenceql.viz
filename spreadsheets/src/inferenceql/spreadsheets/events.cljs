(ns inferenceql.spreadsheets.events
  (:require [clojure.core.match :refer [match]]
            [clojure.string :as str]
            [re-frame.core :as rf]
            [metaprob.prelude :as mp]
            [inferenceql.multimixture :as mmix]
            [inferenceql.multimixture.search :as search]
            [inferenceql.spreadsheets.db :as db]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]
            [inferenceql.spreadsheets.model :as model]
            [inferenceql.spreadsheets.query :as query]
            [inferenceql.spreadsheets.column-overrides :as co]
            [inferenceql.spreadsheets.score :as score]))

(def ^:private search-column "new property")
(def ^:private n-models 10)
(def ^:private beta-params {:alpha 0.001, :beta 0.001})

(rf/reg-event-db
 :initialize-db
 event-interceptors
 (fn [_ _]
   (db/default-db)))

(rf/reg-event-db
 :clear-virtual-data
 event-interceptors
 (fn [db [event-name]]
   (-> (db/clear-virtual-rows db)
       (db/clear-virtual-scores))))

(rf/reg-event-db
 :clear-virtual-scores
 event-interceptors
 (fn [db [event-name]]
   (db/clear-virtual-scores db)))

(rf/reg-event-fx
 :parse-query
 event-interceptors
 (fn [{:keys [db]} [_ text]]
   (let [command (->> (str/trim text)
                      (query/parse))]
     (match command
       {:type :generate-virtual-row, :conditions c, :num-rows num-rows}
       {:dispatch [:generate-virtual-row c num-rows]}

       {:type :anomaly-search :column column :given :row}
       {:dispatch [:anomaly-search column ["ROW"]]}

       {:type :anomaly-search :column column :given given-col}
       {:dispatch [:anomaly-search column [given-col]]}

       {:type :anomaly-search :column column}
       {:dispatch [:anomaly-search column []]}

       {:type :search-by-labeled :binding {"label" "True"} :given true}
       (let [pos-ids @(rf/subscribe [:row-ids-labeled-pos])
             neg-ids @(rf/subscribe [:row-ids-labeled-neg])
             unlabeled-ids @(rf/subscribe [:row-ids-unlabeled])]
         {:dispatch [:search-by-labeled pos-ids neg-ids unlabeled-ids]})

       :else
       (let [logged-msg (str "Unimplemented command: " (pr-str command))
             alerted-msg "Invalid query syntax."]
         ;; TODO: These could be their own effects!
         (js/console.error logged-msg)
         (js/alert alerted-msg)
         {})))))

(rf/reg-event-db
 :search-by-example
 event-interceptors
 (fn [db [_ example-row]]
   (let [table-rows @(rf/subscribe [:table-rows])
         search-row (merge example-row {search-column true})
         result (search/search model/spec search-column [search-row] table-rows n-models beta-params)]
     (rf/dispatch [:search-result result]))
   db))

(rf/reg-event-db
 :anomaly-search
 event-interceptors
 (fn [db [_ target-col conditional-cols]]
   (let [table-rows @(rf/subscribe [:table-rows])
         result (search/anomaly-search model/spec target-col conditional-cols table-rows)
         virtual-rows @(rf/subscribe [:virtual-rows])
         virtual-result (search/anomaly-search model/spec target-col conditional-cols virtual-rows)]
     (rf/dispatch [:search-result result])
     (rf/dispatch [:virtual-search-result virtual-result]))
   db))

(rf/reg-event-db
 :generate-virtual-row
 event-interceptors
 (fn [db [_ conditions num-rows]]
   (let [constraint-addrs-vals (mmix/with-row-values {} conditions)
         gen-fn #(first (mp/infer-and-score
                         :procedure (search/optimized-row-generator model/spec)
                         :observation-trace constraint-addrs-vals))
         has-negative-vals? #(some (every-pred number? neg?) (vals %))

         overrides-map (get db ::db/column-override-fns)
         overrides-insert-fn (co/gen-insert-fn overrides-map)

         ;; TODO: '(remove negative-vals? ...)' is hack for StrangeLoop2019
         new-rows (take num-rows (map overrides-insert-fn (remove has-negative-vals? (repeatedly gen-fn))))]
     (db/with-virtual-rows db new-rows))))

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

(rf/reg-event-db
 :search-by-labeled
 event-interceptors
 (fn [db [_ pos-ids neg-ids unlabeled-ids]]
   (let [rows @(rf/subscribe [:table-rows])

         pos-rows (map rows pos-ids)
         neg-rows (map rows neg-ids)
         unlabeled-rows (map rows unlabeled-ids)

         example-rows (create-search-examples pos-rows neg-rows)

         scores (search/search model/spec search-column example-rows unlabeled-rows n-models beta-params)
         scores-ids-map (zipmap unlabeled-ids scores)

         scores-ids-map-lab (create-scores-map-for-labeled-rows pos-ids neg-ids)

         all-scores (->> (merge scores-ids-map scores-ids-map-lab)
                         (sort-by key)
                         (map second))]
     (rf/dispatch [:clear-virtual-scores])
     (rf/dispatch [:search-result all-scores]))
   db))

(rf/reg-event-db
 :search-result
 event-interceptors
 (fn [db [_ result]]
   (db/with-scores db result)))

(rf/reg-event-db
 :virtual-search-result
 event-interceptors
 (fn [db [_ result]]
   (db/with-virtual-scores db result)))

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

(rf/reg-event-db
 :compute-row-likelihoods
 event-interceptors
 (fn [db [_]]
   (let [table-rows (get db ::db/rows)
         likelihoods (score/row-likelihoods model/spec table-rows)]
     (assoc db ::db/row-likelihoods likelihoods))))

(rf/reg-event-db
 :compute-missing-cells
 event-interceptors
 (fn [db [_]]
   (let [table-rows (get db ::db/rows)
         headers (get db ::db/headers)
         missing-cells (score/impute-missing-cells model/spec headers table-rows)]
     (assoc db ::db/missing-cells missing-cells))))
