(ns inferenceql.viz.panels.jsmodel.template-test
    (:require [clojure.test :refer [deftest is testing]]
              [cljstache.core :refer [render]]
              [metaprob.distributions]
              [inferenceql.viz.config :refer [config]]))

(def demo-mustache-template-data
  "Template data to be passed to the js-model mustache template."
  {:categories [{:name "bar" :values "\"red\", \"green\", \"blue\""}]
   :model {:view-fn-nums [1 2]
           :splats [{:num 1}
                    {:num 2 :last true}]}
   :views [{:num 1
            :cluster-probs "0.2, 0.8"
            :clusters [{:first true
                        :num 1
                        :parameters [{:name "foo" :gaussian true :mu 3 :sigma 4}
                                     {:name "bar" :categorical true :weights "0.3, 0.1, 0.6"
                                      :last true}]}
                       {:num 2
                        :parameters [{:name "foo" :gaussian true :mu 8 :sigma 1}
                                     {:name "bar" :categorical true :weights "0.1, 0.1, 0.8"
                                      :last true}]}]}
           {:num 2
            :cluster-probs "0.5, 0.5"
            :clusters [{:first true
                        :num 1
                        :parameters [{:name "biz" :gaussian true :mu 3 :sigma 4 :last true}]}
                       {:num 2
                        :parameters [{:name "biz" :gaussian true :mu 99 :sigma 1 :last true}]}]}]})

(deftest mustache-for-jsmodel
  (let [mustache-template (:js-model-template config)
        program-text (render mustache-template demo-mustache-template-data)]
    #?(:clj (let [correct-program-text (slurp "test-resources/jsmodel/program.txt")]
              (testing "generates valid source"
                (is (= program-text correct-program-text)))))
    #?(:cljs (do
               ;; Evaluating the generated program in the javascript runtime requries the
               ;; distribution functions from [metaprob.distributions] to be required.
               (js/eval program-text)
               (let [new-row (js->clj (js/model))]
                 (testing "generates working program"
                   (is (= #{"foo" "bar" "biz"} (set (keys new-row))))))))))


