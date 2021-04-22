(ns inferenceql.viz.panels.sim.db
  (:require [clojure.spec.alpha :as s]))

(def default-db
  {:sim-panel {:display {}
               :target-gene :age
               :essential-genes [:age :gender :height]}})

(s/def ::sim-panel (s/keys :req-un []))

