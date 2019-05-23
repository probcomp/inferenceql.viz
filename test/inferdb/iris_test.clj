(ns inferdb.multimixture-test
  (:refer-clojure :exclude [map apply replicate])
  (:require [clojure.pprint :refer [pprint]]
            [clojure.repl :refer [doc source]]
            [clojure.test :refer :all]
            [clojure.string :refer [index-of]]
            [metaprob.generative-functions :refer :all]
            [metaprob.code-handlers :refer :all]
            [metaprob.expander :refer :all]
            [metaprob.trace :refer :all]
            [metaprob.autotrace :refer :all]
            [metaprob.prelude :refer :all]
            [metaprob.inference :refer :all]
            [metaprob.distributions :refer :all]
            [inferdb.cgpm.main :refer :all]
            [inferdb.multimixture.dsl :refer :all]))


;; util

(def abs (fn [n] (max n (- n))))

(def relerr (fn [a b] (abs (- a b))))

(defn make-identity-output-addr-map
  [output-addrs-types]
  (let [output-addrs (keys output-addrs-types)
        trace-addrs  (map clojure.core/name output-addrs)]
    (clojure.core/zipmap output-addrs trace-addrs)))


(def generate-crosscat-row
  (multi-mixture
    (view
      {"sepal_width" gaussian
       "petal_width" gaussian
       "name" categorical
       "sepal_length" gaussian
       "petal_length" gaussian}
      (clusters
       0.325162847357 {"sepal_width" [34.179974 3.771946]
                       "petal_width" [2.440002 1.061319]
                       "name" [[0.986639 0.002751 0.002445]]
                       "sepal_length" [50.060013 3.489470]
                       "petal_length" [14.640005 1.717676]}
       0.158811538073 {"sepal_width" [29.400019 2.727630]
                       "petal_width" [14.599998 1.296148]
                       "name" [[0.007976 0.972954 0.005166]]
                       "sepal_length" [63.080017 3.877322]
                       "petal_length" [45.879978 1.986359]}
       0.152157485702 {"sepal_width" [30.666668 2.153812]
                       "petal_width" [21.291676 2.423479]
                       "name" [[0.007944 0.009516 0.968360]]
                       "sepal_length" [65.874985 2.437782]
                       "petal_length" [55.375000 2.429196]}
       0.132195328587 {"sepal_width" [26.380953 2.256758]
                       "petal_width" [11.952375 1.290116]
                       "name" [[0.011187 0.958093 0.017295]]
                       "sepal_length" [56.238064 2.327993]
                       "petal_length" [39.714233 2.762729]}
       0.0922710143592 {"sepal_width" [27.133326 2.124983]
                        "petal_width" [18.266705 2.143722]
                        "name" [[0.005149 0.009685 0.963620]]
                        "sepal_length" [59.133389 3.461565]
                        "petal_length" [49.599995 1.624789]}
       0.0656548048737 {"sepal_width" [31.363638 3.960549]
                        "petal_width" [20.909092 2.108620]
                        "name" [[0.007970 0.016508 0.947206]]
                        "sepal_length" [74.999992 2.558409]
                        "petal_length" [63.454559 3.201247]}
       0.0124223859026 {"sepal_width" [22.999990 2.160242]
                        "petal_width" [10.333331 0.471404]
                        "name" [[0.040683 0.858193 0.044297]]
                        "sepal_length" [50.000000 0.816497]
                        "petal_length" [32.666668 2.054805]}
       0.0613245951451 {"sepal_width" [30.540000 4.321466]
                        "petal_width" [11.986667 7.606126]
                        "name" [[0.333333 0.333333 0.333333]]
                        "sepal_length" [58.433333 8.253013]
                        "petal_length" [37.586667 17.585292]}))))

(def crosscat-cgpm
  (let [outputs-addrs-types {;; Variables in the table.
                             :sepal_length real-type
                             :sepal_width real-type
                             :petal_length real-type
                             :petal_width real-type
                             :name integer-type
                             ;; Exposed latent variables.
                             :cluster-for-sepal_length integer-type
                             :cluster-for-sepal_width integer-type
                             :cluster-for-petal_length integer-type
                             :cluster-for-petal_width integer-type
                             :cluster-for-name integer-type}
        output-addr-map (make-identity-output-addr-map outputs-addrs-types)
        inputs-addrs-types {}
        input-addr-map {}]
    (make-cgpm generate-crosscat-row
               outputs-addrs-types
               inputs-addrs-types
               output-addr-map
               input-addr-map)))


;; TODO: Provide coverage for the following cases:
;; 1. A MultiMixture CGPM with more than one view.

;; TODO: Write tests which capture assertion fails for simulate/logpdf errors:
;; 1. cluster-for-[varname] in same view contradict each other.

(deftest crosscat-row-logpdf-agree-data
  (testing "crosscat-row-logpdf-agree-data"
    (let [lp-multimix (nth
                       (infer-and-score
                        :procedure generate-crosscat-row
                        :observation-trace {"sepal_width" {:value 34}})
                       2)
          lp-cgpm (cgpm-logpdf crosscat-cgpm {:sepal_width 34} {} {})]
      (is (< (relerr lp-cgpm lp-multimix) 1E-4)))))

(deftest crosscat-row-logpdf-agree-cluster
  (testing "crosscat-row-logpdf-agree-cluster"
    (let [lp-multimix (nth
                       (infer-and-score
                        :procedure generate-crosscat-row
                        :observation-trace {"cluster-for-sepal_length" {:value 0}})
                       2)
          lp-cgpm (cgpm-logpdf crosscat-cgpm
                               {:cluster-for-sepal_length 0} {} {})]
      (is (< (relerr lp-cgpm lp-multimix) 1E-4)))))

(deftest crosscat-row-logpdf-agree-joint
  (testing "crosscat-row-logpdf-agree-joint"
    (let [lp-multimix (nth
                       (infer-and-score
                        :procedure generate-crosscat-row
                        :observation-trace {"sepal_width" {:value 34}
                                            "cluster-for-sepal_length" {:value 0}})
                       2)
          lp-cgpm (cgpm-logpdf
                   crosscat-cgpm
                   {:sepal_width 34 :cluster-for-sepal_length 0}
                   {} {})]
      (is (< (relerr lp-cgpm lp-multimix) 1E-4)))))

(deftest crosscat-row-logpdf-conditional
  (testing "crosscat-row-logpdf-conditional"
    (let [lp-zx (cgpm-logpdf
                 crosscat-cgpm
                 {:sepal_width 34 :cluster-for-sepal_length 0}
                 {} {})
          lp-z (cgpm-logpdf
                crosscat-cgpm
                {:cluster-for-sepal_length 0}
                {} {})
          lp-x-given-z (cgpm-logpdf
                          crosscat-cgpm
                          {:sepal_width 34}
                          {:cluster-for-sepal_length 0}
                          {})
          lp-x (cgpm-logpdf
                crosscat-cgpm
                {:sepal_width 34}
                {} {})
          lp-z-given-x (cgpm-logpdf
                        crosscat-cgpm
                        {:cluster-for-sepal_length 0}
                        {:sepal_width 34}
                        {})]
      (is (< (relerr lp-x-given-z (- lp-zx lp-z)) 1E-4))
      (is (< (relerr lp-z-given-x (- lp-zx lp-x)) 1E-4)))))


(deftest crosscat-row-mi-nonzero
  (testing "crosscat-row-mi-nonzero"
    (let [mi (cgpm-mutual-information
              crosscat-cgpm
              [:sepal_length]
              [:sepal_width]
              {} {} {} 50 1)]
      (is (> mi 0.05)))))


(deftest crosscat-row-mi-conditional-indep-fixed-z
  (testing "crosscat-row-mi-conditional-indep-fixed-z"
    (let [mi (cgpm-mutual-information
               crosscat-cgpm
               [:sepal_length]
               [:sepal_width]
               {}
               {:cluster-for-sepal_length 0}
               {} 50 10)]
    (is mi (< 1E-5)))))

(deftest crosscat-row-mi-conditional-indep-marginalize-z
  (testing "crosscat-row-mi-conditional-indep-marginalize-z"
    (let [mi (cgpm-mutual-information
              crosscat-cgpm
              [:sepal_length]
              [:sepal_width]
              [:cluster-for-sepal_length]
              {}
              {} 50 1)]
      (is mi (< 1E-5)))))

(deftest crosscat-conditional-simulate-smoke
  (testing "gaussian-mixture-2d-cgpm-simulate"
    (let [num-samples 10
          samples
          (cgpm-simulate
           crosscat-cgpm
           [:petal_width]
           {:petal_length 1.}
           {}
           num-samples)]
      (is (= (count samples)
             10)))))

(deftest crosscat-cgpm-simulate
  (testing "crosscat-cgpm-simulate"
    (let [num-samples 10
          samples     (cgpm-simulate
                       crosscat-cgpm
                       [:cluster-for-sepal_length]
                       {}
                       {}
                       num-samples)]
      (is (= (count samples)
             num-samples)))))

;; crosscat latent variables
(def latent-variables  #{:cluster-for-sepal_length})
(def row-1 {:sepal_width  34.179974
            :petal_width  2.440002
            :name         0
            :sepal_length 50.060013
            :petal_length 14.640005})
(def row-2 {:sepal_width  200
            :petal_width  200
            :name         2
            :sepal_length 200
            :petal_length  200})


;; row wise-similarity. For seach-by-example, make row-1 a hypothetical row and
;; then loop over each row the data table (and make each row-2).
(deftest row-wise-similarity
  (testing "row-wise-similarity"
  (let [symmetrized-kl (+ (cgpm-kl-divergence
                            crosscat-cgpm
                            latent-variables
                            latent-variables
                            row-1
                            row-2
                            {}
                            10)
                          (cgpm-kl-divergence
                           crosscat-cgpm
                           latent-variables
                           latent-variables
                           row-2
                           row-1
                           {}
                           10))]
    (is (> symmetrized-kl 0)))))


;; Create a 2 d gaussian mixture with two components
(def gaussian-mixture-2d
  (multi-mixture
    (view
      {"x" gaussian, "y" gaussian}
      (clusters
        0.1 {"x" [-5 1], "y" [-10 1]}
        0.9 {"x" [5 1],  "y" [10 1]}))))

(def gmm-cgpm
  (let [outputs-addrs-types {;; Variables in the table.
                             :x real-type
                             :y real-type

                             ;; Exposed latent variables.
                             :cluster-for-x integer-type
                             :cluster-for-y integer-type}
        output-addr-map (make-identity-output-addr-map outputs-addrs-types)
        inputs-addrs-types {}
        input-addr-map {}]
  (make-cgpm gaussian-mixture-2d
             outputs-addrs-types
             inputs-addrs-types
             output-addr-map
             input-addr-map)))


(deftest gaussian-mixture-2d-cgpm-simulate-smoke
  (testing "gaussian-mixture-2d-cgpm-simulate"
    (let [num-samples 10
          samples     (cgpm-simulate
                       gmm-cgpm
                       [:x]
                       {:y 1.}
                       {}
                       num-samples)]
      (is (= (count samples)
             10)))))

(deftest gaussian-mixture-2d-cgpm-simulate-conditional-on-cluster-smoke
  (testing "gaussian-mixture-2d-cgpm-simulate"
    (let [num-samples 10
          samples     (cgpm-simulate
                       gmm-cgpm
                       [:x]
                       {:cluster-for-y 0}
                       {}
                       num-samples)]
      (is (= (count samples)
             10)))))

(deftest gaussian-mixture-2d-cgpm-simulate-conditional-on-cluster-smoke
  (testing "gaussian-mixture-2d-cgpm-simulate (smoke)"
    (let [num-samples 10
          samples     (cgpm-simulate
                       gmm-cgpm
                       [:x]
                       {:cluster-for-y 0}
                       {}
                       num-samples)]
      (is (= (count samples)
             10)))))


;; Compute simple average of items. XXX -- import from cgpm utils
(defn compute-avg2
  [items]
    (/ (reduce + 0 items) (count items)))


(defn get-col
  [col-key table]
  (map (fn [row] (get row col-key)) table))


(deftest gaussian-mixture-2d-cgpm-simulate-conditional-on-cluster
  (testing "gaussian-mixture-2d-cgpm-simulate"
    (let [num-samples 100
          samples     (cgpm-simulate
                       gmm-cgpm
                       [:x]
                       {:cluster-for-y 0}
                       {}
                       num-samples)]
      (is (< (relerr (compute-avg2 (get-col  :x samples)) -5)
             0.1)))))


(deftest gaussian-mixture-2d-cgpm-logpdf-conditional-on-cluster-smoke
  (testing "gaussian-mixture-2d-cgpm-simulate (smoke)"
    (let [logp     (cgpm-logpdf
                       gmm-cgpm
                       {:x 0}
                       {:cluster-for-y 0}
                       {})]
      (is (< logp 0)))))

(deftest gaussian-mixture-2d-cgpm-logpdf-conditional-on-cluster
  (testing "gaussian-mixture-2d-cgpm-simulate"
    (let [logp     (cgpm-logpdf
                    gmm-cgpm
                    {:x -3}
                    {:cluster-for-y 0}
                    {})
          expected-logp -2.9189385332046727] ;; Computed with scipy.stats
      (is (< (relerr logp expected-logp)
             0.0000001)))))


;; Create a bivariate Gaussian distribution (linearly related mean).
(def generate-biv-gaussian-row
  (gen []
       (let [x  (at "x" gaussian 0 10)
             y  (at "y" gaussian (* 2 x) 1)]
         [x y])))


(def biv-gaussian-cgpm
    (let [inputs-addrs-types  {}
          outputs-addrs-types {:x real-type :y real-type}
          output-addr-map     (make-identity-output-addr-map outputs-addrs-types)
          input-addr-map      {}]
      (make-cgpm generate-biv-gaussian-row
                 outputs-addrs-types
                 inputs-addrs-types
                 output-addr-map
                 input-addr-map)))


(defn square [x] (* x x))
(def rho 0.9987)
(def expected-mi (* -0.5 (log (- 1 (square rho)))))
(deftest biv-gaussian-cgpm-simulate-smoke
  (testing "bivariate gaussian simulate"
    (let [num-samples 10
          samples     (cgpm-simulate
                       biv-gaussian-cgpm
                       [:y]
                       {:x 1.}
                       {}
                       num-samples)]
      (is (= (count samples)
             10)))))


(deftest biv-gaussian-logpdf
  (testing "biv-gaussian-cgpm-logpdf"
    (let [logp (cgpm-logpdf
                biv-gaussian-cgpm
                {:y 1}
                {:x 1}
                {})
          expected-logp -1.4189385332046727] ;; Computed with scipy.stats
      (is (< (relerr logp expected-logp)
             0.0000001)))))
