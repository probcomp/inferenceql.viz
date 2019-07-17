(ns inferdb.search-by-example.api-test
  (:require [clojure.test :as test :refer [are deftest is testing]]
            [inferdb.search-by-example.api :as api]))

(deftest validation-errors
  (let [validation-errors #'api/validation-errors]
    (testing "valid"
      (is (empty? (validation-errors {})))
      (is (empty? (validation-errors {"percent_married" 0.1}))))
    (testing "invalid column"
      (let [[error :as errors] (validation-errors {"invalid_column" 0.1})]
        (is (= 1 (count errors)))
        (is (= :invalid-column (:type error)))))
    (testing "invalid value"
      (testing "numeric"
        (let [[error :as errors] (validation-errors {"percent_married" "invalid numeric value"})]
          (is (= 1 (count errors)))
          (is (= :invalid-value (:type error)))))
      (testing "nominal"
        (let [[error :as errors] (validation-errors {"nyt_political_prediction" 0.1})]
          (is (= 1 (count errors)))
          (is (= :invalid-value (:type error))))))
    (testing "invalid column and value"
      (let [errors (validation-errors {"percent_married" "invalid numeric value"
                                       "nyt_political_prediction" 0.1})]
        (is (= 2 (count errors)))))))

(deftest validation
  (doseq [f #{api/isVeryAnomalous api/search}]
    (testing f
      (are [example] (thrown? js/Error. (f example))
        #js {"invalid_column" 0.01}
        #js {"percent_married" "invalid numeric value"}
        #js {"nyt_political_prediction" 0.01}))))
