(ns inferenceql.spreadsheets.panels.jsmodel.multimix-test
    (:require [clojure.test :refer [deftest is testing]]
              [inferenceql.spreadsheets.panels.jsmodel.multimix :as multimix]))

(def spec
  "A generic multimix model spec."
  {:vars {:gender :categorical, :height :gaussian, :age :gaussian},
   :views [;; view 1
           [{:probability 0.35,
             :parameters {:height {:mu 160, :sigma 0.7},
                          :gender {"female" 0.89, "male" 0.11},}}
            {:probability 0.65,
             :parameters {:height {:mu 176, :sigma 0.6},
                          :gender {"female" 0.19, "male" 0.81},}}]
           ;; view 2
           [{:probability 1,
             :parameters {:age {:mu 40, :sigma 1},}}]]})

(def categories-section-expected
  "Expected test data"
  [{:name "gender", :values "\"female\", \"male\""}])

(def views-section-expected
  "Expected test data"
  [;; view 1
   {:num 1
    :cluster-probs "0.35, 0.65",
    :clusters [{:first true,
                :num 1,
                :parameters [{:gaussian true,
                              :last false
                              :mu 160,
                              :name "height",
                              :sigma 0.7}
                             {:categorical true,
                              :last true,
                              :name "gender",
                              :weights "0.89, 0.11"}]}
               {:first false,
                :num 2,
                :parameters [{:gaussian true,
                              :last false
                              :mu 176,
                              :name "height",
                              :sigma 0.6}
                             {:categorical true,
                              :last true,
                              :name "gender",
                              :weights "0.19, 0.81"}]}]}
   ;; view 2
   {:num 2
    :cluster-probs "1",
    :clusters [{:first true,
                :num 1,
                :parameters [{:gaussian true,
                              :last true,
                              :mu 40,
                              :name "age",
                              :sigma 1}]}]}])

(def model-section-expected
  "Expected test data"
  {:splats [{:num 1, :last false}
            {:num 2, :last true}],
   :view-fn-nums [1 2]})

(deftest multimix-transformation-functions
  (do
    (testing "categories section production"
      (is (= categories-section-expected
             (multimix/categories-section spec))))

    (testing "views section production"
      (is (= views-section-expected
             (multimix/views-section spec))))

    (testing "model section production"
      (is (= model-section-expected
             (multimix/model-section spec))))))
