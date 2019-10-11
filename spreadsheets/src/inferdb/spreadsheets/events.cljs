(ns inferdb.spreadsheets.events
  (:require [clojure.core.match :refer [match]]
            [re-frame.core :as rf]
            [metaprob.prelude :as mp]
            [inferdb.multimixture :as mmix]
            [inferdb.multimixture.search :as search]
            [inferdb.spreadsheets.db :as db]
            [inferdb.spreadsheets.events.interceptors :as interceptors]
            [inferdb.spreadsheets.model :as model]
            [inferdb.spreadsheets.query :as query]
            [inferdb.spreadsheets.column-overrides :as co]
            [inferdb.spreadsheets.score :as score]))

(def real-hot-hooks [:after-deselect :after-selection-end :after-on-cell-mouse-down :before-change])
(def virtual-hot-hooks [:after-deselect :after-selection-end :after-on-cell-mouse-down :after-change])

(def event-interceptors
  [rf/debug interceptors/check-spec])

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

(rf/reg-event-db
 :before-change
 event-interceptors
 (fn [db [_ hot id changes]]
   (let [labels-col (.getSourceDataAtCol hot 0)]
     (db/with-labels db (js->clj labels-col)))))

(rf/reg-event-fx
 :after-change
 event-interceptors
 (fn [{:keys [db]} [_ hot id changes source]]
   ;; When the change is the result of loading new table
   ;; into the table.
   (when (= source "loadData")
     (let [table-state @(rf/subscribe [:table-state id])]
       (if-let [header-clicked (:header-clicked table-state)]
         (let [current-selection (.getSelectedLast hot)
               [_row1 col1 _row2 col2] (js->clj current-selection)]
           ;; Take the current selection and expand it so the whole columns
           ;; are selected.
           (.selectColumns hot col1 col2))))
    {})))

(rf/reg-event-db
 :after-selection-end
 event-interceptors
 (fn [db [_ hot id row-index col _row2 col2 _prevent-scrolling _selection-layer-level]]
   (let [selected-headers (map #(.getColHeader hot %)
                               (range (min col col2) (inc (max col col2))))
         row (js->clj (zipmap (.getColHeader hot)
                              (.getDataAtRow hot row-index)))
         selected-maps (into []
                             (comp (map (fn [[row col row2 col2]]
                                          (.getData hot row col row2 col2)))
                                   (map js->clj)
                                   (map (fn [rows]
                                          (into []
                                                (map (fn [row]
                                                       (zipmap selected-headers row)))
                                                rows))))
                             (.getSelected hot))
         selected-columns (if (<= col col2) selected-headers (reverse selected-headers))]
     (-> db
         (assoc-in [::db/hot-state id :selected-columns] selected-columns)
         (assoc-in [::db/hot-state id :selections] selected-maps)
         (assoc-in [::db/hot-state id :selected-row-index] row-index)
         (assoc-in [::db/hot-state id :row-at-selection-start] row)))))

(rf/reg-event-db
 :after-on-cell-mouse-down
 event-interceptors
 (fn [db [_ hot id mouse-event coords _TD]]
   (let [other-table-id @(rf/subscribe [:other-table id])

         ;; Stores whether the user clicked on one of the column headers.
         header-clicked-flag (= -1 (.-row coords))

         ;; Stores whether the user held alt during the click.
         alt-key-pressed (.-altKey mouse-event)
         ; Switch the last clicked on table-id to the other table on alt-click.
         new-table-clicked-id (if alt-key-pressed other-table-id id)]

     ; Deselect all cells on alt-click.
     (when alt-key-pressed
       (.deselectCell hot))

     (-> db
         (assoc-in [::db/hot-state id :header-clicked] header-clicked-flag)
         (assoc ::db/table-last-clicked new-table-clicked-id)))))

(rf/reg-event-db
 :after-deselect
 event-interceptors
 (fn [db [_ hot id]]
   ;; clears selections associated with table
   (update-in db [::db/hot-state id] dissoc :selected-columns :selections :selected-row-index :row-at-selection-start)))

(rf/reg-event-fx
 :parse-query
 event-interceptors
 (fn [{:keys [db]} [_ text]]
   (let [command (query/parse text)]
     (match command
       {:type :generate-virtual-row, :conditions c, :num-rows num-rows}
       {:dispatch [:generate-virtual-row c num-rows]}

       {:type :anomaly-search :column column :given true}
       {:dispatch [:anomaly-search column ["ROW"]]}

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

(defn query-for-conf-options [type threshold]
  (case type
    :none ""
    :row (str "COLOR ROWS WITH CONFIDENCE OVER " threshold)
    :cells-existing (str "COLOR CELLS EXISTING WITH CONFIDENCE OVER " threshold)
    :cells-missing (str "IMPUTE CELLS MISSING WITH CONFIDENCE OVER " threshold)))

(rf/reg-event-db
 :virtual-search-result
 event-interceptors
 (fn [db [_ result]]
   (db/with-virtual-scores db result)))

(rf/reg-event-fx
 :set-confidence-threshold
 event-interceptors
 (fn [{:keys [db]} [_ value]]
   (let [conf-mode (get-in db [::db/confidence-options :mode])
         new-query-string (query-for-conf-options conf-mode value)]
     {:db (assoc db ::db/confidence-threshold value)
      :dispatch [:set-query-string new-query-string]})))

(rf/reg-event-fx
 :set-confidence-options
 event-interceptors
 (fn [{:keys [db]} [_ path value]]
   (let [conf-threshold (get db ::db/confidence-threshold)
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
    {:db (assoc-in db (into [::db/confidence-options] path) value)
     :dispatch-n event-list})))

(rf/reg-event-db
 :update-confidence-options
 event-interceptors
 (fn [db [_ f path value]]
   (update-in db (into [::db/confidence-options] path) f value)))

(rf/reg-event-db
 :set-query-string
 event-interceptors
 (fn [db [_ new-val]]
   (assoc-in db [::db/query-string] new-val)))

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
   (let [string-to-compile (str "(" source-text ")")
         ;; TODO catch and print compilation errors gracefully
         evaled-fn (js/eval string-to-compile)]
     (-> db
         (assoc-in [::db/column-overrides col-name] source-text)
         (assoc-in [::db/column-override-fns col-name] evaled-fn)))))

(rf/reg-event-db
 :clear-column-function
 event-interceptors
 (fn [db [_ col-name]]
   (-> db
       (update-in [::db/column-overrides] dissoc col-name)
       (update-in [::db/column-override-fns] dissoc col-name))))

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
