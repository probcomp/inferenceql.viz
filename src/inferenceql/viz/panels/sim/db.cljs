(ns inferenceql.viz.panels.sim.db
  (:require [clojure.spec.alpha :as s]))

(def default-db
  {:sim-panel {:display {}
               :target-gene :stoA
               :essential-genes [:recR :mdxG]
               :conditioned false

               :expr-level nil
               :constraints {}}})


(s/def ::sim-panel (s/keys :req-un []))

