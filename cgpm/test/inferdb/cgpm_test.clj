(ns inferdb.cgpm-test
  (:require [clojure.test :refer [deftest is testing]]
            [metaprob.distributions :as dist]
            [metaprob.generative-functions :refer [at gen]]
            [metaprob.prelude :as mp]
            [inferdb.cgpm.main :as cgpm]))

;;; UTILITIES

(def abs (fn [n] (max n (- n))))
(def relerr (fn [a b] (abs (- a b))))

(defn make-identity-output-addr-map
  [output-addrs-types]
  (let [output-addrs (keys output-addrs-types)
        trace-addrs  (mp/map clojure.core/name output-addrs)]
    (clojure.core/zipmap output-addrs trace-addrs)))

(deftest test-make-identity-output-addr-map
  (is (= (make-identity-output-addr-map {:d 1 :f 1})
         {:d "d" :f "f"})))

;;; TESTS FOR DUMMY CGPM

(def generate-dummy-row
  (gen [_]
    (let [x0 (at "x0" dist/uniform-discrete [1 2 3 4])
          x1 (at "x1" dist/uniform 9 199)
          x2 (at "x2" dist/gaussian 0 10)
          x3 (at "x3" dist/uniform-discrete ["foo" "bar" "baz"])]
      [x0 x1 x2 x3])))

(def dummy-cgpm
  (let [inputs-addrs-types  {:y cgpm/real-type}
        outputs-addrs-types {:x0 (cgpm/make-nominal-type #{1 2 3 4})
                             :x1 (cgpm/make-ranged-real-type 9 199)
                             :x2 cgpm/real-type
                             :x3 (cgpm/make-nominal-type #{"foo" "bar" "baz"})}
        output-addr-map     (make-identity-output-addr-map outputs-addrs-types)
        input-addr-map      {:y 0}]
    (cgpm/make-cgpm generate-dummy-row
                    outputs-addrs-types
                    inputs-addrs-types
                    output-addr-map
                    input-addr-map)))

;;; TODO: Write tests which capture assertion fails for initialize.
;;; 1. output-addrs and and input-addrs overlap.
;;; 2. output-addr-map is missing keys.
;;; 3. input-addr-map is missing keys.
;;; 4. output-addr-map has duplicate values.
;;; 5. input-addr-map map to non-integers.
;;; 6. input-addr-map map to non-contiguous integers.

;;; TODO: Write tests which capture assertion fails for simulate/logpdf errors:
;;; 1. Unknown variable in target.
;;; 2. Unknown variable in constraint.
;;; 3. Unknown variable in input.
;;; 4. Overlapping target and constraint in logpdf.
;;; 5. Provided values disagree with statistical data types.

;;; TODO: Write tests which capture assertion fails for KL divergence errors:
;;; 1. Different base measures of target-addrs-0 and target-addrs-1.

(deftest dummy-row-logpdf
  (is (< (cgpm/cgpm-logpdf dummy-cgpm {:x0 2} {} {:y 100}) 0))
  (is (< (cgpm/cgpm-logpdf dummy-cgpm {:x1 120} {:x0 2} {:y 100}))) ;; XXX -- what's that
  (is (< (cgpm/cgpm-logpdf dummy-cgpm {:x0 2 :x1 120} {} {:y 100})))) ;; XXX -- what's that

(deftest dummy-row-simulate
  (testing "dummy-row-simulate"
    (let
        [sample-1 (cgpm/cgpm-simulate dummy-cgpm [:x0 :x1 :x2] {} {:y 100} 10)
         sample-2 (cgpm/cgpm-simulate
                   dummy-cgpm
                   [:x0 :x1 :x2]
                   {:x3 "foo"}
                   {:y 100} 20)]
      (is (= (count sample-1) 10))
      (is (= (count sample-2) 20)))))

(deftest dummy-row-mutual-information-zero
  (testing "dummy-row-mutual-information-zero"
    (is (< (cgpm/cgpm-mutual-information dummy-cgpm [:x0] [:x1] [] {:x3 "foo"}
                                    {:y 100} 10 1)
           1E-10))))

(deftest dummy-row-kl-divergence-zero
  (testing "dummy-row-kl-divergence-zero"
    (is (< (cgpm/cgpm-kl-divergence dummy-cgpm [:x0] [:x0] {} {:x3 "foo"} {:y 100} 10)
           1E-10))))

(deftest dummy-row-kl-divergence-non-zero
  (testing "dummy-row-kl-divergence-non-zero"
    (is (> (cgpm/cgpm-kl-divergence dummy-cgpm [:x1] [:x2] {} {:x3 "foo"} {:y 100} 10)
           1))))
