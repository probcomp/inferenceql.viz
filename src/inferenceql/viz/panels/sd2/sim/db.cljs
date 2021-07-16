(ns inferenceql.viz.panels.sd2.sim.db
  (:require [clojure.spec.alpha :as s]))

(def default-db
  {:sd2-sim-panel {:target-gene :stoA
                   :essential-genes []
                   ;; Keys related to constraining target gene expr level.
                   :conditioned false
                   :expr-level nil
                   :constraints {}}})

(s/def ::sd2-sim-panel (s/keys :req-un []))

