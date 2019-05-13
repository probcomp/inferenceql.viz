(ns inferdb.test-runner
  (:require [inferdb.data-generators :refer :all]))

(defn -main
  [& args]
  (binding [metaprob.prelude/*rng* (java.util.Random. 42)]
  (clojure.test/run-tests 'inferdb.data-generators)))
