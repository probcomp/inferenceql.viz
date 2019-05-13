(ns inferdb.data-generators
  (:refer-clojure :exclude [map apply replicate])
  (:require [clojure.pprint :refer [pprint]]
            [clojure.repl :refer [doc source]]
            [clojure.test :refer :all]
            [clojure.string :refer [index-of]]))

(deftest fixed-rand-seed
  (testing "whether we can fix the rand seed."
    (let [x (uniform 0 1)
          y (binding [metaprob.prelude/*rng* (java.util.Random. 42)]
              (uniform 0 1))]
    (is (= x y)))))
