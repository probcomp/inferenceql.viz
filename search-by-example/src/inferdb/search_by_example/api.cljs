(ns inferdb.search-by-example.api
  (:require [inferdb.spreadsheets.data :as data]
            [inferdb.spreadsheets.search :as search]))

(def ^:export data (clj->js data/nyt-data))

(defn ^:export search
  [example emphasis]
  (clj->js
   (search/search-by-example (js->clj example)
                             (js->clj emphasis)
                             1)))

(defn ^:export isVeryAnomalous
  [example]
  (search/very-anomalous? (reduce-kv (fn [m k v]
                                       (assoc m (keyword k) v))
                                     {}
                                     (js->clj example))))
