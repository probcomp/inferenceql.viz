(ns inferdb.spreadsheets.events
  (:require [clojure.edn :as edn]
            [re-frame.core :as rf]
            [inferdb.spreadsheets.db :as db]
            [inferdb.spreadsheets.events.interceptors :as interceptors]
            [inferdb.spreadsheets.search :as search]))

(def hooks [#_:after-deselect :after-selection])

#_
(defn selected-map
  [headers rows [row col row2 col2 selection-layer-level]]
  (let [selected-headers
        selected-rows    (subvec rows row (inc row2))]
    (mapv (fn [row]
            (into (sorted-map-by (fn [header1 header2]
                                   (< (.indexOf selected-headers header1)
                                      (.indexOf selected-headers header2))))
                  (select-keys row selected-headers)))
          selected-rows)))

(rf/reg-event-db
 :initialize-db
 [interceptors/check-spec]
 (fn [db _]
   (db/default-db)))

(rf/reg-event-db
 :after-selection
 (fn [db [_ hot row col row2 col2 prevent-scrolling selection-layer-level]]
   (let [headers (map #(.getColHeader hot %)
                      (range (min col col2) (inc (max col col2))))
         selected-maps (into []
                             (comp (map (fn [[row col row2 col2]]
                                          (js->clj (.getData hot row col row2 col2))))
                                   (map js->clj)
                                   (map (fn [rows]
                                          (into []
                                                (map (fn [row]
                                                       (zipmap headers row)))
                                                rows))))
                             (.getSelected hot))
         selected-columns (if (<= col col2) headers (reverse headers))]
     (-> db
         (db/with-selected-columns selected-columns)
         (db/with-selections selected-maps)))))

(rf/reg-event-db
 :after-deselect
 [interceptors/check-spec]
 (fn [db _]
   (db/clear-selections db)))

(rf/reg-event-db
 :search
 (fn [db [_ text]]
   (let [row (edn/read-string text)]
     (let [result #_(mapv (fn [i] [i 0])
                          (range 435))
           (search/search-by-example row #{:cluster-for-percap} 1)]
       (rf/dispatch [:search-result result])))
   db))

(rf/reg-event-db
 :search-result
 (fn [db [_ result]]
   (db/with-scores db (->> result
                           (sort-by first)
                           (mapv second)))))
