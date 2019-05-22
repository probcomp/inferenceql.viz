(ns inferdb.spreadsheets.events
  (:require [clojure.edn :as edn]
            [clojure.walk :as walk]
            [clojure.core.async :as async :refer [go]]
            [re-frame.core :as rf]
            [inferdb.cgpm.main :as cgpm]
            [inferdb.spreadsheets.db :as db]
            [inferdb.spreadsheets.events.interceptors :as interceptors]
            [inferdb.spreadsheets.model :as model]
            [inferdb.spreadsheets.search :as search]))

(def hooks [:after-deselect :after-selection-end])

(def event-interceptors
  [rf/debug interceptors/check-spec])

(rf/reg-event-db
 :initialize-db
 event-interceptors
 (fn [db _]
   (db/default-db)))

(rf/reg-event-db
 :simulate
 [interceptors/check-spec]
 (fn [db [_ simulated-rows]]
   (db/with-simulated-rows db simulated-rows)))

(rf/reg-event-db
 :after-selection-end
 event-interceptors
 (fn [db [_ hot row-index col row2 col2 prevent-scrolling selection-layer-level]]
   (when-let [stop-simulator! (db/simulator db)]
     (stop-simulator!))
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
     (let [new-db (-> db
                      (db/with-selected-columns selected-columns)
                      (db/with-selections selected-maps)
                      (db/with-selected-row-index row-index)
                      (db/with-row-at-selection-start row)
                      (db/clear-simulated-rows))]
       (cond-> new-db
         (db/one-cell-selected? new-db)
         (db/with-simulator (let [c (async/chan)]
                              (go (while (async/alt! c false :default true)
                                    (let [column (first selected-columns)
                                          simulated-rows (-> (cgpm/cgpm-simulate model/census-cgpm
                                                                                 [(keyword column)]
                                                                                 (reduce-kv (fn [acc k v]
                                                                                              (cond-> acc
                                                                                                v (assoc k v)))
                                                                                            {}
                                                                                            (-> row
                                                                                                (select-keys (keys model/stattypes))
                                                                                                (dissoc column)
                                                                                                (walk/keywordize-keys)))
                                                                                 {}
                                                                                 10)
                                                             (walk/stringify-keys))]
                                      (rf/dispatch-sync [:simulate simulated-rows]))
                                    (async/<! (async/timeout 1))))
                              #(async/close! c))))))))

(rf/reg-event-db
 :after-deselect
 event-interceptors
 (fn [db _]
   (when-let [stop-simulator! (db/simulator db)]
     (stop-simulator!))
   (-> db
       (db/clear-selections)
       (db/clear-simulated-rows))))

(rf/reg-event-db
 :search
 event-interceptors
 (fn [db [_ text]]
   (let [row (edn/read-string text)]
     (let [result (search/search-by-example row :cluster-for-percap 1)]
       (rf/dispatch [:search-result result])))
   db))

(rf/reg-event-db
 :search-result
 event-interceptors
 (fn [db [_ result]]
   (db/with-scores db (->> result
                           (sort-by first)
                           (mapv second)))))
