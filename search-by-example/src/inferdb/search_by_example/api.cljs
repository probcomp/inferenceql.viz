(ns inferdb.search-by-example.api
  (:require [inferdb.spreadsheets.data :as data]
            [inferdb.spreadsheets.search :as search]))

(def ^:export data (clj->js data/nyt-data))

(defn ^:export isVeryAnomalous
  [example]
  (search/very-anomalous? (reduce-kv (fn [m k v]
                                       (assoc m (keyword k) v))
                                     {}
                                     (js->clj example))))

(defn ^:export search
  [example]
  (if (isVeryAnomalous example)
    (throw (js/Error. "Example is too anomalous!"))
    (clj->js (search/search-by-example (js->clj example)))))
