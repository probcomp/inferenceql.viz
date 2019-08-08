(ns inferdb.spreadsheets.events
  (:require [clojure.edn :as edn]
            [re-frame.core :as rf]
            [inferdb.spreadsheets.data :as data]
            [inferdb.multimixture.search :as search]
            [inferdb.spreadsheets.model :as model]
            [inferdb.spreadsheets.db :as db]
            [inferdb.spreadsheets.events.interceptors :as interceptors]))

(def hooks [:after-deselect :after-selection-end])

(def event-interceptors
  [rf/debug interceptors/check-spec])

(rf/reg-event-db
 :initialize-db
 event-interceptors
 (fn [_ _]
   (db/default-db)))

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
(def ^:private n-models 1)
(def ^:private beta-params {:alpha 0.001, :beta 0.001})

(rf/reg-event-db
 :search
 event-interceptors
 (fn [db [_ text]]
   (let [row (merge (edn/read-string text)
                    {search-column true})
         result (search/search model/spec search-column [row] data/nyt-data n-models beta-params)]
     ;; TODO: Re-implement search to use `inferdb.multimixture.search`
     (rf/dispatch [:search-result result]))
   db))

(rf/reg-event-db
 :search-result
 event-interceptors
 (fn [db [_ result]]
   (db/with-scores db result))) ; TODO: This is not resilient to sorting the table
