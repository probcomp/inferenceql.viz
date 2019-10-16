(ns inferdb.spreadsheets.score-test
  (:require [clojure.test :as test :refer [is deftest]]
            [clojure.core.match :refer [match]]
            [inferdb.spreadsheets.score :as score]
            [inferdb.spreadsheets.model :as model]
            [inferdb.spreadsheets.data :refer [nyt-data]]
            [inferdb.spreadsheets.util :as util]))

(def db-rows nyt-data)
(def db-headers (into [] (keys (first nyt-data))))

(deftest row-scoring
  (let [rows (take 2 db-rows)
        likelihoods (score/row-likelihoods model/spec rows)]
    ;; We get back a sequence of numbers.
    (is (seq? likelihoods))
    (is (every? number? likelihoods))))

(deftest cell-scoring
  (let [rows (take 1 db-rows)
        likelihoods (score/cell-likelihoods model/spec rows)
        row (first rows)
        likelihood (first likelihoods)]
    ;; Returns a map with the same keys as rows (excluding keys mapped to nil)
    (is (= (set (keys (util/filter-nil-kvs row)))
           (set (keys likelihood))))
    ;; All the vals are likelihoods
    (is (every? number? (vals likelihood)))))

(deftest cell-imputation
  (let [rows (take 1 db-rows)
        missing-cells (score/impute-missing-cells model/spec db-headers rows)
        missing-cells-in-first-row (first missing-cells)

        ;; TODO: this test is data set specific
        match-result (match missing-cells-in-first-row
                            {:values {"years_coding_professionally" imputed-val}
                             :scores {"years_coding_professionally" likelihood}}
                            true
                            :else
                            false)]
    (is (true? match-result))))
