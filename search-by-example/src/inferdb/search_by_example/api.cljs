(ns inferdb.search-by-example.api
  (:require [inferdb.spreadsheets.search :as search]))

(defn ^:export search
  [example emphasis]
  (clj->js
   (search/search-by-example (js->clj example)
                             (js->clj emphasis)
                             1)))
