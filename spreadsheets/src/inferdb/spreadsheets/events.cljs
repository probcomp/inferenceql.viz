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

(def hooks [:after-deselect :after-selection-end :after-change])
(def virtual-hot-hooks [:after-deselect :after-selection-end])

(def event-interceptors
  [rf/debug interceptors/check-spec])

(rf/reg-event-db
 :initialize-db
 event-interceptors
 (fn [_ _]
   (db/default-db)))

(rf/reg-event-db
 :table-scroll
 []
 (fn [db [event-name emmitter-obj left-scroll-pos]]
   ;(.log js/console "table-scroll")
   ;(.log js/console emmitter-obj)
   ;(.log js/console left-scroll-pos)
   (db/with-left-scroll-pos db emmitter-obj left-scroll-pos)))

(rf/reg-event-db
 :simulate
 event-interceptors
 (fn [db [event-name conditions num-rows]]
   (.log js/console "conditions----")
   (.log js/console conditions)
   (let [constraint-addrs-vals (mmix/with-row-values {} conditions)
         gen #(first (mp/infer-and-score
                       :procedure (search/optimized-row-generator model/spec)
                       :observation-trace constraint-addrs-vals))
         new-rows (repeatedly num-rows gen)]
     (db/with-virtual-rows db new-rows))))

(rf/reg-event-db
 :clear-simulations
 event-interceptors
 (fn [db [event-name]]
   (db/clear-simulations db)))

(rf/reg-event-db
 :after-change
 event-interceptors
 (fn [db [_ hot changes]]

   ; may end up using this later on
   #_(doseq [change changes])
     (let [[row prop oldVal newVal] change]
       ; do something
       (+ 1 1))

   ; TODO make this more robust, as row 0 might move
   (let [example-flags (.getDataAtCol hot 0)]
     (db/with-example-flags db (js->clj example-flags)))))

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

(def ^:private search-column "new property")
(def ^:private n-models 30)
(def ^:private beta-params {:alpha 0.001, :beta 0.001})


(defn anomaly-search?
  [text]
  (clojure.string/includes? (clojure.string/lower-case text) "probability"))

(defn generate-statement?
  [text]
  (clojure.string/includes? (clojure.string/lower-case text) "generate"))

(rf/reg-event-db
 :search
 event-interceptors
 (fn [db [_ text]]
   (let [text (str/trim text)]
     (cond
       (generate-statement? text)
       (do
         (cond
           (re-matches #"GENERATE ROW" text)
           (rf/dispatch [:simulate {} 1])

           (re-matches #"GENERATE ROW (\d+) TIMES" text)
           (do
             (let [[_ m1] (re-matches #"GENERATE ROW (\d+) TIMES" text)
                   num-rows (js/parseInt m1)]
               (rf/dispatch [:simulate {} num-rows])))

           (re-matches #"GENERATE ROW GIVEN ([A-Za-z][A-Za-z0-9_\+]*)=\"(.+)\" AND ([A-Za-z][A-Za-z0-9_\+]*)=\"(.+)\"" text)
           (do
             (let [[_ k1 v1 k2 v2] (re-matches #"GENERATE ROW GIVEN ([A-Za-z][A-Za-z0-9_\+]*)=\"(.+)\" AND ([A-Za-z][A-Za-z0-9_\+]*)=\"(.+)\"" text)]
               (rf/dispatch [:simulate {k1 v1 k2 v2} 1])))

           ; NOTE: saving this as an alternate version of the following regex actually used
           ; TODO: delete later
           ;(re-matches #"GENERATE ROW GIVEN ([A-Za-z][A-Za-z0-9_\+]*)=([A-Za-z0-9_]+)" text)
           (re-matches #"GENERATE ROW GIVEN ([A-Za-z][A-Za-z0-9_\+]*)=\"(.+)\"" text)
           (do
             (let [[_ k1 v1] (re-matches #"GENERATE ROW GIVEN ([A-Za-z][A-Za-z0-9_\+]*)=\"(.+)\"" text)]
               (rf/dispatch [:simulate {k1 v1} 1])))

           :else
           (.log js/console "ERROR: no match for generate call")))

       (anomaly-search? text)
       (let [result (search/anomaly-search model/spec text data/nyt-data)]
         (rf/dispatch [:search-result result]))

       :else
       (let [row (merge (edn/read-string text) {search-column true})
             result (search/search model/spec search-column [row] data/nyt-data n-models beta-params)]
         (rf/dispatch [:search-result result]))))
   db))

(rf/reg-event-db
 :search-by-flagged
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
   (db/with-scores db result))) ; TODO: This is not resilient to sorting the table
