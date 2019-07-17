(ns inferdb.search-by-example.api-test
  (:require [clojure.test :as test :refer [are deftest is testing]]
            [inferdb.search-by-example.api :as api]))

(deftest validation-errors
  (let [validation-errors #'api/validation-errors]
    (is (empty? (validation-errors {})))
    (is (empty? (validation-errors {"percent_married" 0.01})))
    (is (not-empty (validation-errors {"invalid_column" 0.01})))))

(deftest validation
  (doseq [f #{api/isVeryAnomalous api/search}]
    (testing f
      (are [example] (thrown? js/Error. (f example))
        #js {"invalid_column" 0.01}
        #js {"percent_married" "invalid numeric value"}
        #js {"nyt_political_prediction" 0.01}))))
