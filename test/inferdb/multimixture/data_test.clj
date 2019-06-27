(ns inferdb.multimixture.data-test
  (:require [clojure.test :as test :refer [deftest testing is are]]
            [metaprob.distributions :as dist]
            [inferdb.multimixture.data :as data]))

(def mmix
  [{:vars {"x" dist/gaussian
           "y" dist/categorical}
    :clusters [{:probability 1
                :args {"x" [2 3]
                       "y" [0.1 0.2 0.3 0.4]}}]}
   {:vars {"a" dist/gaussian
           "b" dist/categorical}
    :clusters [{:probability 0.4
                :args {"a" [4 5]
                       "b" [0.9 0.01 0.02 0.03 0.04]}}
               {:probability 0.6
                :args {"a" [6 7]
                       "b" [0.99 0.001 0.002 0.003 0.004]}}]}])

(deftest view-variables-test
  (is (= #{:x :y} (data/view-variables (first mmix))))
  (is (= #{:a :b} (data/view-variables (second mmix)))))

(deftest variables-test
  (is (= #{:x :y :a :b} (data/variables mmix))))

(deftest stattype-test
  (is (= dist/gaussian (data/stattype mmix :x))))

(deftest statistical-types-test
  (let [variables (data/variables mmix)]
    (testing "nominal?"
      (is (= #{:x :a}
             (into #{}
                   (filter #(data/nominal? mmix %))
                   variables))))
    (testing "categorical?"
      (is (= #{:y :b}
             (into #{}
                   (filter #(data/categorical? mmix %))
                   variables))))))

(deftest parameters-test
  (is (= [2 3] (data/parameters mmix :x 0))))
