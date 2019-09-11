(ns inferdb.search-by-example.api-test
  (:require [clojure.test :as test :refer [deftest is testing]]
            [inferdb.search-by-example.api :as api]))

(def spec
  {:vars {"x" :gaussian
          "y" :categorical
          "a" :gaussian
          "b" :categorical}
   :views [[{:probability 1
             :parameters {"x" {:mu 2 :sigma 3}
                          "y" {"0" 0.1 "1" 0.2 "2" 0.3 "3" 0.4}}}]
           [{:probability 0.4
             :parameters {"a" {:mu 4 :sigma 5}
                          "b" {"0" 0.9, "1" 0.01, "2" 0.02, "3" 0.03, "4" 0.04}}}
            {:probability 0.6
             :parameters {"a" {:mu 6 :sigma 7}
                          "b" {"0" 0.99, "1" 0.001, "2" 0.002, "3" 0.003, "4" 0.004}}}]]})

(def example-with-invalid-column {"invalid_column" 0.1})
(def example-with-invalid-numeric-value {"x" "invalid numeric value"})
(def example-with-invalid-nominal-value {"y" 0.1})

(def all-invalid-examples
  #{example-with-invalid-column
    example-with-invalid-numeric-value
    example-with-invalid-nominal-value})

(deftest validation-errors
  (let [validation-errors #'api/validation-errors]
    (testing "valid"
      (is (empty? (validation-errors spec {})))
      (is (empty? (validation-errors spec {"x" 0.1}))))
    (testing "invalid column"
      (let [[error :as errors] (validation-errors spec example-with-invalid-column)]
        (is (= 1 (count errors)))
        (is (= :invalid-column (:type error)))))
    (testing "invalid value"
      (testing "numeric"
        (let [[error :as errors] (validation-errors spec example-with-invalid-numeric-value)]
          (is (= 1 (count errors)))
          (is (= :invalid-value (:type error)))))
      (testing "nominal"
        (let [[error :as errors] (validation-errors spec example-with-invalid-nominal-value)]
          (is (= 1 (count errors)))
          (is (= :invalid-value (:type error))))))
    (testing "multiple validation issues"
      (let [errors (validation-errors spec (merge example-with-invalid-column
                                                  example-with-invalid-numeric-value
                                                  example-with-invalid-nominal-value))]
        (is (= 3 (count errors)))))))
