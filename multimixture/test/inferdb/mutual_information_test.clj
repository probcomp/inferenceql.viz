(ns inferdb.mutual-information-test
  (:require [clojure.test :refer [deftest is testing]]
            [inferdb.cgpm.main :as cgpm]
            [inferdb.utils :as utils]
            [inferdb.plotting.generate-vljson :as plot]
            [inferdb.multimixture.dsl-test :as mm-test]
            [inferdb.multimixture.specification :as spec]))

(def multi-mixture
  [{:vars {"x" :gaussian
           "y" :gaussian
           "a" :categorical}
    :clusters [{:probability 0.25
                :parameters {"x" [1 0.1]
                             "y" [1 0.1]
                             "a" [[1 0 0 0]]}}
               {:probability 0.25
                :parameters {"x" [2 0.1]
                             "y" [2 0.1]
                             "a" [[0 1 0 0]]}}
               {:probability 0.25
                :parameters {"x" [3 0.1]
                             "y" [3 0.1]
                             "a" [[0 0 1 0]]}}
               {:probability 0.25
                :parameters {"x" [4 0.1]
                             "y" [4 0.1]
                             "a" [[0 0 0 1]]}}]}
   {:vars {"v" :gaussian
           "w" :gaussian}
    :clusters [{:probability 1.00
                :parameters {"v" [1 1]
                             "w" [1 1]}}]}])

(def crosscat-cgpm-mi
  (let [generate-crosscat-row (spec/crosscat-row-generator multi-mixture)
        outputs-addrs-types {;; Variables in the table.
                             :x cgpm/real-type
                             :y cgpm/real-type
                             :v cgpm/real-type
                             :w cgpm/real-type
                             :a cgpm/integer-type
                             ;; Exposed latent variables.
                             :cluster-for-x cgpm/integer-type
                             :cluster-for-y cgpm/integer-type
                             :cluster-for-v cgpm/integer-type
                             :cluster-for-w cgpm/integer-type
                             :cluster-for-a cgpm/integer-type}
        output-addr-map (mm-test/make-identity-output-addr-map outputs-addrs-types)
        inputs-addrs-types {}
        input-addr-map {}]
    (cgpm/make-cgpm generate-crosscat-row
                    outputs-addrs-types
                    inputs-addrs-types
                    output-addr-map
                    input-addr-map)))

;; How many points do we want to create for our plot?
(def n 1000)

(deftest crosscatsimulate-simulate-MI-model
  "This tests saves plots for all simulated data in out/json results/"
  ;; Charts can be generated with make charts.
  (testing "(smoke) simulate n complete rows and save them as vl-json"
    (let [num-samples n
          samples (cgpm/cgpm-simulate
                   crosscat-cgpm-mi
                   [:x :y :a :v :w]
                   {}
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
                                                                    [:v :w])
                                               []
                                               [-4 6]
                                               "View 2: V W"))
      (is (= (count samples) n)))))

(deftest mi-smoke
  (testing "smoke testing generic, CGPM based mutual information Oapproximation"
    (let [mi (cgpm/cgpm-mutual-information
              crosscat-cgpm-mi
              [:x :y]
              [:a]
              []
              {}
              {}
              2
              2)]
      (is (number? mi)))))

(deftest cmi-smoke-estimate-condition
  (testing "smoke testing generic, CGPM based mutual information Oapproximation"
    (let [mi (cgpm/cgpm-mutual-information
              crosscat-cgpm-mi
              [:x :y]
              [:a]
              [:v]
              {}
              {}
              2
              2)]
      (is (number? mi)))))

(deftest cmi-smoke-specify-condition
  (testing "smoke testing generic, CGPM based mutual information Oapproximation"
    (let [mi (cgpm/cgpm-mutual-information
              crosscat-cgpm-mi
              [:x :y]
              [:a]
              []
              {:v 0}
              {}
              2
              2)]
      (is (number? mi)))))

;; MI of x and y is larger than 0 because x carries information about y.
(deftest positive-mi
  (testing "Test the first invariant descrited in test/inferdb/README.md"
    (let [mi (cgpm/cgpm-mutual-information
              crosscat-cgpm-mi
              [:x]
              [:y]
              []
              {}
              {}
              100
              1)]
      (is (< 0. mi)))))


(def threshold 0.01)
(defn- almost-equal? [a b] (utils/almost-equal? a b utils/relerr threshold))

(deftest zero-mi
  (testing "Test the third invariant descrited in test/inferdb/README.md"
    (let [mi (cgpm/cgpm-mutual-information
              crosscat-cgpm-mi
              [:v]
              [:w]
              []
              {}
              {}
              100
              1)]
      (is (almost-equal? 0. mi)))))

(deftest zero-cmi
  (testing "Test the second invariant descrited in test/inferdb/README.md"
    (let [mi (cgpm/cgpm-mutual-information
              crosscat-cgpm-mi
              [:x]
              [:y]
              []
              {:a 0}
              {}
              100
              1)]
      (is (almost-equal? 0. mi)))))
