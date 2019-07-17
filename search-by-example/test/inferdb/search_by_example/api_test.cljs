(ns inferdb.search-by-example.api-test
  (:require [clojure.test :as test :refer [deftest is testing]]
            [inferdb.search-by-example.api :as api]))

(deftest validation-errors
  (testing "empty map"
    (is (empty? (api/validation-errors {}))))
  (testing "valid"
    (is (empty? (api/validation-errors {"percent_married" 0.01}))))
  (testing "invalid"
    (is (not-empty (api/validation-errors {"invalid_column" 0.01})))))
