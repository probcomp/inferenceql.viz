(ns inferdb.test-runner
  (:require [inferdb.multimixture-test :refer :all])
  (:require [inferdb.cgpm-test :refer :all])
  (:require [inferdb.analytical-solutions-mixture-test :refer :all]))

(defn -main
  [& args]
  (binding [metaprob.prelude/*rng* (java.util.Random. 42)]
  (assert false "I am never called!?")
  (clojure.test/run-tests 'inferdb.multimixture-test)
  (clojure.test/run-tests 'inferdb.cgpm-test)
  (clojure.test/run-tests 'inferdb.analytical-solutions-mixture-test)))
