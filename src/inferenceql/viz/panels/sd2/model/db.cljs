(ns inferenceql.viz.panels.sd2.model.db
  (:require [clojure.spec.alpha :as s]))

(def default-db
  {:sd2-model-panel {:display {}}})

(s/def ::sd2-model-panel (s/keys :req-un []))

