(ns inferenceql.viz.panels.jsmodel.multimix-test
    (:require [clojure.test :refer [deftest is testing]]
              [inferenceql.viz.panels.jsmodel.multimix :as multimix]))

(def spec
  "A generic multimix model spec."
  {:vars {:gender :categorical, :height :gaussian, :age :gaussian :fav-color :categorical},
   :views [;; view 1
           [{:probability 0.35,
             :parameters {:height {:mu 160, :sigma 0.7},
                          :gender {"female" 0.89, "male" 0.11},}}
            {:probability 0.65,
             :parameters {:height {:mu 176, :sigma 0.6},
                          :gender {"female" 0.19, "male" 0.81},}}]
           ;; view 2
           [{:probability 1,
             :parameters {:age {:mu 40, :sigma 1},
                          :fav-color {"blue" 0.60 "green" 0.40}}}]]})

(def categories-section-expected
  "Expected test data"
  [{:name "gender", :values "\"female\", \"male\""}
   {:name "fav-color", :values "\"blue\", \"green\""}])

(def views-section-expected
  "Expected test data"
  [;; view 1
   {:num 1
    :cluster-probs "0.350, 0.650",
    :clusters [{:first true,
                :num 1,
                :parameters [{:gaussian true,
                              :last false
                              :mu "160",
                              :name "height",
                              :sigma "0.700"}
                             {:categorical true,
                              :last true,
                              :name "gender",
                              :weights "0.890, 0.110"}]}
               {:first false,
                :num 2,
                :parameters [{:gaussian true,
                              :last false
                              :mu "176",
                              :name "height",
                              :sigma "0.600"}
                             {:categorical true,
                              :last true,
                              :name "gender",
                              :weights "0.190, 0.810"}]}]}
   ;; view 2
   {:num 2
    :cluster-probs "1",
    :clusters [{:first true,
                :num 1,
                :parameters [{:gaussian true,
                              :last false,
                              :mu "40",
                              :name "age",
                              :sigma "1"}
                             {:categorical true,
                              :last true,
                              :name "fav-color",
                              :weights "0.600, 0.400"}]}]}])

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
