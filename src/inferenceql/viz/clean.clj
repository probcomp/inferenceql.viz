(ns inferenceql.viz.clean
  (:require [inferenceql.viz.config :as config]
            [clojure.data.csv :as csv]
            [clojure.java.io :as io]))

(def data (get config/config :data))

(def fixed-data (for [d data]
                  (let [update-map {"exercise" "exercise"
                                    "1.0" "1"
                                     "0.0" "0"}]
                    (update d 5 update-map))))

;; Checks.
(distinct (map #(nth % 5) fixed-data))
(first fixed-data)

(comment
 (with-open [writer (io/writer "fixed-data.csv")]
   (csv/write-csv writer fixed-data)))
