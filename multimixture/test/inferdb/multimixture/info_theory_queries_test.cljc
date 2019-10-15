(ns inferdb.multimixture.info-theory-queries-test
  (:require [clojure.test :as test :refer [deftest testing is]]
            ;;#?(:clj [inferdb.plotting.generate-vljson :as plot]) ;;XXX Do I need this?
            [inferdb.multimixture.specification :as spec]
            [inferdb.utils :as utils]
            [inferdb.multimixture.info-theory-queries :as itq]
            [inferdb.multimixture.basic-queries :as bq]
            [inferdb.multimixture.search :as search] ;; XXX: why is the "optimized" row generator in search?
            [inferdb.plotting.generate-vljson :as plot]))

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
 (is (float? (itq/mutual-information row-generator ["x"] ["y"] {} 2))))

;; How many points do we want to create for our plot?
(def n 1000)

(deftest crosscatsimulate-simulate-MI-model
  "This tests saves plots for all simulated data in out/json results/"
  ;; Charts can be generated with make charts.
  (testing "(smoke) simulate n complete rows and save them as vl-json"
    (let [num-samples n
          samples (bq/simulate
                   row-generator
                   {}
                   num-samples)]
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
      (is (= (count samples) n)))))

(use 'clojure.test)
(run-tests)
