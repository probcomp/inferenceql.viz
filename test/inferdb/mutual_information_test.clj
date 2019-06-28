(ns inferdb.mutual-information-test
  (:require [clojure.test :refer [deftest is testing]]
            [inferdb.cgpm.main :as cgpm]
            [inferdb.utils :as utils]
            [inferdb.plotting.generate-vljson :as plot]
            [inferdb.multimixture.dsl :as dsl]
            [metaprob.distributions :as dist]))

;; XXX: why is this still here?
(defn make-identity-output-addr-map
  [output-addrs-types]
  (let [output-addrs (keys output-addrs-types)
        trace-addrs  (map name output-addrs)]
    (zipmap output-addrs trace-addrs)))

(def generate-crosscat-row-for-mi
  (dsl/multi-mixture
   (dsl/view
    {"x" dist/gaussian
     "y" dist/gaussian
     "a" dist/categorical}
    (dsl/clusters
       0.25 {"x" [1 0.1]
             "y" [1 0.1]
             "a" [[1 0 0 0]]}
       0.25 {"x" [2 0.1]
             "y" [2 0.1]
             "a" [[0 1 0 0]]}
       0.25 {"x" [3 0.1]
             "y" [3 0.1]
             "a" [[0 0 1 0]]}
       0.25 {"x" [4 0.1]
             "y" [4 0.1]
             "a" [[0 0 0 1]]}))
   (dsl/view
    {"v" dist/gaussian
     "w" dist/gaussian}
    (dsl/clusters
       1.00 {"v" [1 1]
             "w" [1 1] }))))

(def crosscat-cgpm-mi
  (let [outputs-addrs-types {;; Variables in the table.
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
        output-addr-map (make-identity-output-addr-map outputs-addrs-types)
        inputs-addrs-types {}
        input-addr-map {}]
    (cgpm/make-cgpm generate-crosscat-row-for-mi
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
;; TODO: implemet tests according to test/inferdb/README.md
