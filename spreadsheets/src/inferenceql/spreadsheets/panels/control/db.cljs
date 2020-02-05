(ns inferenceql.spreadsheets.panels.control.db
  (:require [clojure.spec.alpha :as s]))

(def default-db
  {:control-panel {:confidence-threshold 0.9
                   :confidence-options {:mode :none}
                   :query-string ""}})


(s/def ::control-panel (s/keys :req-un [::confidence-threshold
                                        ::confidence-options
                                        ::query-string]))
(s/def ::confidence-threshold number?)
(s/def ::confidence-options (s/keys :req-un [::mode]))
(s/def ::mode keyword?)
(s/def ::query-string string?)
