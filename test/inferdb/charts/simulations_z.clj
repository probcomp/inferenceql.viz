(ns inferdb.charts.simulations-z
  (:require [clojure.walk :as walk]
            [cheshire.core :as cheshire]
            [inferdb.cgpm.main :as cgpm]
            [clojure.repl :refer [doc source]]
            [clojure.test :refer :all]
            [clojure.string :refer [index-of]]))

(defn -main [] (println (slurp "temp.txt")))
