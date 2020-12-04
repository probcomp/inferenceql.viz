(ns inferenceql.viz.panels.override.db
  (:require [clojure.spec.alpha :as s]))

(def default-db
  {:override-panel {:column-overrides nil
                    :column-override-fns nil}})

(s/def ::override-panel (s/keys :req-un [::column-overrides
                                         ::column-override-fns]))

;;; Specs related to foreign function overrides on columns.

(s/def ::column-name string?)
(s/def ::function-text string?)
(s/def ::function-obj fn?)
(s/def ::column-overrides (s/nilable (s/map-of ::column-name ::function-text)))
(s/def ::column-override-fns (s/nilable (s/map-of ::column-name ::function-obj)))
