(ns inferdb.multimixture.specification-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :as test :refer [deftest testing is are]]
            [expound.alpha :as expound]
            [metaprob.distributions :as dist]
            [inferdb.multimixture.specification :as spec]))

(def mmix
  [{:vars {"x" dist/gaussian
           "y" dist/categorical}
    :clusters [{:probability 1
                :parameters {"x" [2 3]
                             "y" [[0.1 0.2 0.3 0.4]]}}]}
   {:vars {"a" dist/gaussian
           "b" dist/categorical}
    :clusters [{:probability 0.4
                :parameters {"a" [4 5]
                             "b" [[0.9 0.01 0.02 0.03 0.04]]}}
               {:probability 0.6
                :parameters {"a" [6 7]
                             "b" [[0.99 0.001 0.002 0.003 0.004]]}}]}])

(deftest mmix-is-valid
  (when-not (s/valid? ::spec/multi-mixture mmix)
    (expound/expound ::spec/multi-mixture mmix))
  (is (s/valid? ::spec/multi-mixture mmix)))

(deftest view-variables-test
  (is (= #{:x :y} (spec/view-variables (first mmix))))
  (is (= #{:a :b} (spec/view-variables (second mmix)))))

(deftest variables-test
  (is (= #{:x :y :a :b} (spec/variables mmix))))

(deftest stattype-test
  (is (= dist/gaussian (spec/stattype mmix :x))))

(deftest statistical-types-test
  (let [variables (spec/variables mmix)]
    (testing "numerical?"
      (is (= #{:x :a}
             (into #{}
                   (filter #(spec/numerical? mmix %))
                   variables))))
    (testing "nominal?"
      (is (= #{:y :b}
             (into #{}
                   (filter #(spec/nominal? mmix %))
                   variables))))))

(deftest parameters-test
  (is (= [2 3] (spec/parameters mmix :x 0))))

(deftest categorical-probabilities-test
  (is (= [0.1 0.2 0.3 0.4] (spec/categorical-probabilities mmix :y 0)))
  (is (= [0.4 0.6]
         (let [mmix [{:vars {"x" dist/categorical}
                      :clusters [{:probability 0.6
                                  :parameters {"x" [[0.4 0.6]]}}
                                 {:probability 0.4
                                  :parameters {"x" [[0.6 0.4]]}}]}]]
           (spec/categorical-probabilities mmix :x 0 1)))))
