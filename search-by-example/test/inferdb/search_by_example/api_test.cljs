(ns inferdb.search-by-example.api-test
  (:require [clojure.test :as test :refer [deftest is testing]]
            [inferdb.search-by-example.api :as api]))

(comment

  (def example-with-invalid-column {"invalid_column" 0.1})
  (def example-with-invalid-numeric-value {"percent_married" "invalid numeric value"})
  (def example-with-invalid-nominal-value {"nyt_political_prediction" 0.1})

  (def all-invalid-examples
    #{example-with-invalid-column
      example-with-invalid-numeric-value
      example-with-invalid-nominal-value})

  (deftest validation-errors
    (let [validation-errors #'api/validation-errors]
      (testing "valid"
        (is (empty? (validation-errors {})))
        (is (empty? (validation-errors {"percent_married" 0.1}))))
      (testing "invalid column"
        (let [[error :as errors] (validation-errors example-with-invalid-column)]
          (is (= 1 (count errors)))
          (is (= :invalid-column (:type error)))))
      (testing "invalid value"
        (testing "numeric"
          (let [[error :as errors] (validation-errors example-with-invalid-numeric-value)]
            (is (= 1 (count errors)))
            (is (= :invalid-value (:type error)))))
        (testing "nominal"
          (let [[error :as errors] (validation-errors example-with-invalid-nominal-value)]
            (is (= 1 (count errors)))
            (is (= :invalid-value (:type error))))))
      (testing "multiple validation issues"
        (let [errors (validation-errors (merge example-with-invalid-column
                                               example-with-invalid-numeric-value
                                               example-with-invalid-nominal-value))]
          (is (= 3 (count errors)))))))

  (deftest validation
    (doseq [f #{api/isVeryAnomalous api/search}]
      (testing f
        (doseq [example all-invalid-examples]
          (is (thrown? js/Error. (f (clj->js example))))))))

  )
