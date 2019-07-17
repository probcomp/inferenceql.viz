(ns inferdb.search-by-example.api-test
  (:require [clojure.test :as test :refer [are deftest is testing]]
            [inferdb.search-by-example.api :as api]))

(def invalid-column {"invalid_column" 0.1})
(def invalid-numeric-value {"percent_married" "invalid numeric value"})
(def invalid-nominal-value {"nyt_political_prediction" 0.1})
(def invalid-examples #{invalid-column invalid-numeric-value invalid-nominal-value})

(deftest validation-errors
  (let [validation-errors #'api/validation-errors]
    (testing "valid"
      (is (empty? (validation-errors {})))
      (is (empty? (validation-errors {"percent_married" 0.1}))))
    (testing "invalid column"
      (let [[error :as errors] (validation-errors invalid-column)]
        (is (= 1 (count errors)))
        (is (= :invalid-column (:type error)))))
    (testing "invalid value"
      (testing "numeric"
        (let [[error :as errors] (validation-errors invalid-numeric-value)]
          (is (= 1 (count errors)))
          (is (= :invalid-value (:type error)))))
      (testing "nominal"
        (let [[error :as errors] (validation-errors invalid-nominal-value)]
          (is (= 1 (count errors)))
          (is (= :invalid-value (:type error))))))
    (testing "multiple validation issues"
      (let [errors (validation-errors (merge invalid-column
                                             invalid-numeric-value
                                             invalid-nominal-value))]
        (is (= 3 (count errors)))))))

(deftest validation
  (doseq [f #{api/isVeryAnomalous api/search}]
    (testing f
      (doseq [example invalid-examples]
        (is (thrown? js/Error. (f (clj->js example))))))))
