(ns inferdb.spreadsheets.events
  (:require [clojure.edn :as edn]
            [clojure.string :as str]
            [re-frame.core :as rf]
            [metaprob.prelude :as mp]
            [inferdb.spreadsheets.data :as data]
            [inferdb.multimixture.search :as search]
            [inferdb.multimixture :as mmix]
            [inferdb.spreadsheets.model :as model]
            [inferdb.spreadsheets.db :as db]
            [inferdb.spreadsheets.events.interceptors :as interceptors]))

(def real-hot-hooks [:after-deselect :after-selection-end :after-change])
(def virtual-hot-hooks [:after-deselect :after-selection-end])

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
   (db/clear-virtual-rows db)))

(rf/reg-event-db
 :after-change
 event-interceptors
 (fn [db [_ hot changes]]
   (let [example-flags-col (.getSourceDataAtCol hot 0)]
     (db/with-example-flags db (js->clj example-flags-col)))))

(rf/reg-event-db
 :after-selection-end
 event-interceptors
 (fn [db [_ hot row-index col _row2 col2 _prevent-scrolling _selection-layer-level]]
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
         (db/with-selected-columns selected-columns)
         (db/with-selections selected-maps)
         (db/with-selected-row-index row-index)
         (db/with-row-at-selection-start row)))))

(rf/reg-event-db
 :after-deselect
 event-interceptors
 (fn [db _]
   (db/clear-selections db)))

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
     #(rf/dispatch [:search-by-labeled])

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

     ; Else condition
     ; Defaults to legacy search-by-example
     (let [example-row (edn/read-string text)]
       (rf/dispatch [:search-by-example example-row])))
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
         result (search/anomaly-search model/spec target-col conditional-cols table-rows)]
     (rf/dispatch [:search-result result]))
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

(rf/reg-event-db
 :search-by-labeled
 event-interceptors
 (fn [db [_]]
   (let [remove-nils #(into {} (remove (comp nil? second) %))

         pos-rows-pairs @(rf/subscribe [:rows-flagged-pos])
         pos-rows-ids (map first pos-rows-pairs)
         pos-rows (->> (map second pos-rows-pairs)
                   (map #(merge % {search-column true}))
                   (map remove-nils))

         neg-rows-pairs @(rf/subscribe [:rows-flagged-neg])
         neg-rows-ids (map first neg-rows-pairs)
         neg-rows (->> (map second neg-rows-pairs)
                   (map #(merge % {search-column false}))
                   (map remove-nils))

         example-rows (concat pos-rows neg-rows)

         rows-not-flagged-pairs @(rf/subscribe [:rows-not-flagged])
         rows-not-flagged-ids (map first rows-not-flagged-pairs)
         rows-not-flagged (map second rows-not-flagged-pairs)

         calced-scores (search/search model/spec search-column example-rows rows-not-flagged n-models beta-params)

         calced-scores-ids-map (zipmap rows-not-flagged-ids calced-scores)
         pos-scores-ids-map (zipmap pos-rows-ids (repeat 1))
         neg-scores-ids-map (zipmap neg-rows-ids (repeat 0))

         scores-ids-map (merge calced-scores-ids-map pos-scores-ids-map neg-scores-ids-map)
         sorted-scores-ids (sort-by key scores-ids-map)
         scores (map second sorted-scores-ids)]
     (rf/dispatch [:search-result scores]))
   db))

(rf/reg-event-db
 :search-result
 event-interceptors
 (fn [db [_ result]]
   (db/with-scores db result))) 
