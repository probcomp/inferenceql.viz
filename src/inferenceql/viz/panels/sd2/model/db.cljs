(ns inferenceql.viz.panels.sd2.model.db
  (:require [clojure.spec.alpha :as s]))

(def default-db
  {:sd2-panel {:display {}}})

(s/def ::sd2-panel (s/keys :req-un []))

