(ns inferdb.charts.simulations-b
  (:require [clojure.walk :as walk]
            [cheshire.core :as cheshire]
            [inferdb.cgpm.main :as cgpm]
            [clojure.repl :refer [doc source]]
            [clojure.test :refer :all]
            [clojure.string :refer [index-of]]))

(defn -main [] (println (slurp "out/json-results/simulations-b.json")))
