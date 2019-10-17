(ns inferdb.multimixture.info-theory-queries-test
  (:require [clojure.test :as test :refer [deftest testing is]]
            [inferdb.utils :as utils]
            #?(:clj [inferdb.plotting.generate-vljson :as plot])
            [inferdb.multimixture.specification :as spec]
            [inferdb.multimixture.info-theory-queries :as itq]
            [inferdb.multimixture.search :as search]
            [inferdb.multimixture.basic-queries :as bq]))

(def multi-mixture
  {:vars {"x" :gaussian
          "y" :gaussian
          "a" :categorical
          "v" :gaussian
          "w" :gaussian}
   :views [[{:probability 0.25
               :parameters {"x" {:mu 1 :sigma 0.1}
                            "y" {:mu 1 :sigma 0.1}
                            "a" {"0" 1.0 "1" 0.0 "2" 0.0 "3" 0.0}}}
            {:probability 0.25
             :parameters {"x" {:mu 2 :sigma 0.1}
                          "y" {:mu 2 :sigma 0.1}
                          "a" {"0" 0.0 "1" 1.0 "2" 0.0 "3" 0.0}}}
            {:probability 0.25
             :parameters {"x" {:mu 3 :sigma 0.1}
                          "y" {:mu 3 :sigma 0.1}
                          "a" {"0" 0.0 "1" 0.0 "2" 1.0 "3" 0.0}}}
            {:probability 0.25
             :parameters {"x" {:mu 4 :sigma 0.1}
                          "y" {:mu 4 :sigma 0.1}
                          "a" {"0" 0.0 "1" 0.0 "2" 0.0 "3" 1.0}}}]
           [{:probability 1.00
             :parameters {"v" {:mu 1.0 :sigma 1.0}
                          "w" {:mu 1.0 :sigma 1.0}}}]]})

;; Define the row-generator used below.
(def row-generator (search/optimized-row-generator multi-mixture))

(deftest test-smoke-row-generator
 (is (map? (row-generator))))

(deftest test-smoke-mi
 (is (utils/pos-float? (itq/mutual-information row-generator ["x"] ["y"] {} 2))))

(deftest test-smoke-cmi
 (is (float? (itq/mutual-information row-generator ["x"] ["y"] {"a" "0"} 2))))

(def sampled-points-for-plot 1000)

#?(:clj (deftest simulate-from-MI-model
          "This tests saves plots for all simulated data in out/json results/"
          ;; Charts can be generated with make charts.
         (testing "(smoke) simulate n complete rows and save them as vl-json"
           (let [samples (bq/simulate
                          row-generator
                          {}
                          sampled-points-for-plot)]
             (utils/save-json "simulations-for-mi-x-y"
                              (plot/scatter-plot-json ["x" "y"]
                                                      samples
                                                      []
                                                      [0 5]
                                                      "View 1: X, Y, A"))
             (utils/save-json "simulations-for-mi-v-w"
                              (plot/scatter-plot-json ["v" "w"]
                                                      (utils/column-subset samples
                                                                           ["v" "w"])
                                                      []
                                                      [-4 6]
                                                      "View 2: V W"))
             (is (= sampled-points-for-plot (count samples)))))))


(def num-samples 100)
(def threshold 0.01)
(defn- almost-equal? [a b] (utils/almost-equal? a b utils/relerr threshold))


(deftest positive-mi
 (is (< 0.5 (itq/mutual-information row-generator ["x"] ["y"] {} num-samples))))

(deftest zero-mi
 (is (almost-equal? 0. (itq/mutual-information row-generator ["v"] ["w"] {} num-samples))))

(deftest zero-cmi
 (is (almost-equal? 0. (itq/mutual-information row-generator ["x"] ["y"] {"a" "0"} num-samples))))

(deftest zero-cmi-marginal
 (is (almost-equal? 0. (itq/mutual-information row-generator ["x"] ["y"] ["a"] num-samples))))
