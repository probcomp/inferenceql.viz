(ns inferdb.spreadsheets.events
  (:require [clojure.edn :as edn]
            [clojure.string :as str]
            [re-frame.core :as rf]
            [metaprob.prelude :as mp]
            [inferdb.multimixture :as mmix]
            [inferdb.multimixture.search :as search]
            [inferdb.spreadsheets.data :as data]
            [inferdb.spreadsheets.model :as model]
            [inferdb.spreadsheets.db :as db]
            [inferdb.spreadsheets.events.interceptors :as interceptors]))

(def real-hot-hooks [:after-deselect :after-selection-end :after-column-move :after-column-resize :after-on-cell-mouse-down :before-change])
(def virtual-hot-hooks [:after-deselect :after-selection-end :after-column-move :after-column-resize :after-on-cell-mouse-down :after-change])

(def event-interceptors
  [rf/debug interceptors/check-spec])

(def ^:private search-column "new property")
(def ^:private n-models 10)
(def ^:private beta-params {:alpha 0.001, :beta 0.001})

(rf/reg-event-db
 :initialize-db
 event-interceptors
 (fn [db _]
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

;; TODO this should be converted to reg-event-fx
(rf/reg-event-db
 :after-change
 event-interceptors
 (fn [db [_ hot id changes source]]

   ; When the change is the result of loading new table
   ; into the table.
    (when (= source "loadData")
      (let [table-state @(rf/subscribe [:table-state id])]
        (if-let [header-clicked (:header-clicked table-state)]
          (let [current-selection (.getSelectedLast hot)
                [_row1 col1 _row2 col2] (js->clj current-selection)]
            ;; Take the current selection and expand it so the whole columns
            ;; are selected.
            (.selectColumns hot col1 col2)))))
   db))

(rf/reg-event-db
 :after-column-move
 event-interceptors
 (fn [db [_ hot id columns target]]
   db))

(rf/reg-event-db
 :after-column-resize
 event-interceptors
 (fn [db [_ hot id current-column new-size is-double-click]]
   db))

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
         (assoc-in [:hot-state id :selected-columns] selected-columns)
         (assoc-in [:hot-state id :selections] selected-maps)
         (assoc-in [:hot-state id :selected-row-index] row-index)
         (assoc-in [:hot-state id :row-at-selection-start] row)))))

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
     (if alt-key-pressed
       (.deselectCell hot))

     (-> db
       (assoc-in [:hot-state id :header-clicked] header-clicked-flag)
       (assoc :table-last-clicked new-table-clicked-id)))))

(rf/reg-event-db
 :after-deselect
 event-interceptors
 (fn [db [_ hot id]]
   ;; clears selections associated with table
   (update-in db [:hot-state id] dissoc :selected-columns :selections :selected-row-index :row-at-selection-start)))

(rf/reg-event-db
 :run-inference-ql
 event-interceptors
 (fn [db [_ text]]
   (condp re-matches (str/trim text)
     #"GENERATE ROW" :>>
     #(rf/dispatch [:generate-virtual-row {} 1])

     #"GENERATE ROW (\d+) TIMES" :>>
     (fn [[_ m1]]
       (let [num-rows (js/parseInt m1)]
         (rf/dispatch [:generate-virtual-row {} num-rows])))

     #"GENERATE ROW GIVEN ([A-Za-z][A-Za-z0-9_\+]*)=\"(.+)\" AND ([A-Za-z][A-Za-z0-9_\+]*)=\"(.+)\"" :>>
     (fn [[_ k1 v1 k2 v2]]
       (rf/dispatch [:generate-virtual-row {k1 v1 k2 v2} 1]))

     #"GENERATE ROW GIVEN ([A-Za-z][A-Za-z0-9_\+]*)=\"(.+)\"" :>>
     (fn [[_ k1 v1]]
       (rf/dispatch [:generate-virtual-row {k1 v1} 1]))

     #"SCORE PROBABILITY OF label=\"True\" GIVEN ROW" :>>
     #(let [pos-ids @(rf/subscribe [:row-ids-labeled-pos])
            neg-ids @(rf/subscribe [:row-ids-labeled-neg])
            unlabeled-ids @(rf/subscribe [:row-ids-unlabeled])]
       (rf/dispatch [:search-by-labeled pos-ids neg-ids unlabeled-ids]))

     #"SCORE PROBABILITY OF ([A-Za-z][A-Za-z0-9_\+]*)" :>>
     (fn [[_ target-col]]
       (rf/dispatch [:anomaly-search target-col []]))

     #"SCORE PROBABILITY OF ([A-Za-z][A-Za-z0-9_\+]*) GIVEN ROW" :>>
     (fn [[_ target-col]]
       (rf/dispatch [:anomaly-search target-col ["ROW"]]))

     #"SCORE PROBABILITY OF ([A-Za-z][A-Za-z0-9_\+]*) GIVEN ([A-Za-z][A-Za-z0-9_\+]*)" :>>
     (fn [[_ target-col cond-col-1]]
       (rf/dispatch [:anomaly-search target-col [cond-col-1]]))

     #"SCORE PROBABILITY OF ([A-Za-z][A-Za-z0-9_\+]*) GIVEN ([A-Za-z][A-Za-z0-9_\+]*) AND ([A-Za-z][A-Za-z0-9_\+]*)" :>>
     (fn [[_ target-col cond-col-1 cond-col-2]]
       (rf/dispatch [:anomaly-search target-col [cond-col-1 cond-col-2]]))

     ; legacy search-by-example
     #"\{(.+)\}" :>>
     (fn []
       (let [example-row (edn/read-string text)]
         (rf/dispatch [:search-by-example example-row])))

     ; else condition
     (.error js/console "Could not parse query: \"" text "\""))
   db))

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
 (fn [db [event-name conditions num-rows]]
   (let [constraint-addrs-vals (mmix/with-row-values {} conditions)
         gen-fn #(first (mp/infer-and-score
                           :procedure (search/optimized-row-generator model/spec)
                           :observation-trace constraint-addrs-vals))
         negative-salary? #(< (% "salary_usd") 0)
         new-rows (take num-rows (remove negative-salary? (repeatedly gen-fn)))]
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
 :set-confidence-threshold
 event-interceptors
 (fn [db [_ value]]
   (assoc db ::db/confidence-threshold value)))
