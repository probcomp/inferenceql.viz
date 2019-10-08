(ns inferdb.multimixture.simulate-logpdf-test
  (:require [clojure.spec.alpha :as s]
            [clojure.test :as test :refer [deftest testing is]]
            [expound.alpha :as expound]
            [inferdb.multimixture.specification :as spec]))

;; The following data generator has some interesting properties:
;; - clusters 0 and 1 in view 0 share the samme mu parameter.
;; - a is a deterministic indicator of the cluster.
;; - b is a noisy copy of a.
;; - in both views, clusters are equally weighted.
;; - in view 1, the third Gaussian components (cluster 0) "spans" the domain of
;; all the other components and share a center with cluster 1.
;;
;; I'd encourage everyone who works with the file to run the tests in this file
;; and then run make charts to see how the components relate.

(def mmix
   {:vars {"x" :gaussian
           "y" :gaussian
           "z" :gaussian
           "a" :categorical
           "b" :categorical
           "c" :categorical}
    :views [[  {:probability 0.166666666
                :parameters {"x" {:mu 3 :sigma 1}
                             "y" {:mu 4 :sigma 0.1}
                             "a" {"0" 1.0 "1" 0.0 "2" 0.0 "3" 0.0 "4" 0.0 "5" 0.0}
                             "b" {"0" 0.95, "1" 0.01, "2" 0.01, "3" 0.01, "4" 0.01, "5" 0.01}}}
               {:probability 0.166666666
                :parameters {"x" {:mu 3 :sigma 0.1}
                             "y" {:mu 4 :sigma 1}
                             "a" {"0" 0.0 "1" 1.0 "2" 0.0 "3" 0.0 "4" 0.0 "5" 0.0}
                             "b" {"0" 0.01, "1" 0.95, "2" 0.01, "3" 0.01, "4" 0.01, "5" 0.01}}}
               {:probability 0.166666666
                :parameters {"x" {:mu 8  :sigma 0.5}
                             "y" {:mu 10 :sigma 1}
                             "a" {"0" 0.0 "1" 0.0 "2" 1.0 "3" 0.0 "4" 0.0 "5" 0.0}
                             "b" {"0" 0.01, "1" 0.01, "2" 0.95, "3" 0.01, "4" 0.01, "5" 0.01}}}
               {:probability 0.166666666
                :parameters {"x" {:mu 14  :sigma 0.5}
                             "y" {:mu  7  :sigma 0.5}
                             "a" {"0" 0.0 "1" 0.0 "2" 0.0 "3" 1.0 "4" 0.0 "5" 0.0}
                             "b" {"0" 0.01, "1" 0.01, "2" 0.01, "3" 0.95, "4" 0.01, "5" 0.01}}}
               {:probability 0.166666666
                :parameters {"x" {:mu 16  :sigma 0.5}
                             "y" {:mu  9  :sigma 0.5}
                             "a" {"0" 0.0 "1" 0.0 "2" 0.0 "3" 0.0 "4" 1.0 "5" 0.0}
                             "b" {"0" 0.01, "1" 0.01, "2" 0.01, "3" 0.01, "4" 0.95, "5" 0.01}}}
               {:probability 0.166666666
                :parameters {"x" {:mu  9  :sigma 2.5}
                             "y" {:mu 16  :sigma 0.1}
                             "a" {"0" 0.0 "1" 0.0 "2" 0.0 "3" 0.0 "4" 0.0 "5" 1.0}
                             "b" {"0" 0.01, "1" 0.01, "2" 0.01, "3" 0.01, "4" 0.01, "5" 0.95}}}]
              [{:probability 0.25
                :parameters {"z" {:mu 0 :sigma 1}
                             "c" {"0" 1.0, "1" 0.0, "2" 0.0, "3" 0.0}}}
               {:probability 0.25
                :parameters {"z" {:mu 15 :sigma 1}
                             "c" {"0" 0.0, "1" 1.0, "2" 0.0, "3" 0.0}}}
               {:probability 0.25
                :parameters {"z" {:mu 30 :sigma 1}
                             "c" {"0" 0.0, "1" 0.0, "2" 1.0, "3" 0.0}}}
               {:probability 0.25
                :parameters {"z" {:mu 15 :sigma 8}
                             "c" {"0" 0.0, "1" 0.0, "2" 0.0, "3" 1.0}}}]]})

(deftest mmix-is-valid
  (when-not (s/valid? ::spec/multi-mixture mmix)
    (expound/expound ::spec/multi-mixture mmix))
  (is (s/valid? ::spec/multi-mixture mmix)))

(use 'clojure.test)
(run-tests)
