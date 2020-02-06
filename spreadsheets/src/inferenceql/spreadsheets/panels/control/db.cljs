(ns inferenceql.spreadsheets.panels.control.db
  (:require [clojure.spec.alpha :as s]))

(def default-db
  {:control-panel {:confidence-threshold 0.9
                   :reagent-forms {:confidence-mode :none
                                   :selection-color :blue}
                   :query-string ""}})


(s/def ::control-panel (s/keys :req-un [::confidence-threshold
                                        ::reagent-forms
                                        ::query-string]))
(s/def ::confidence-threshold number?)
(s/def ::reagent-forms (s/keys :req-un [::confidence-mode
                                        ::selection-color]))
(s/def ::confidence-mode keyword?)
(s/def ::selection-color keyword?)
(s/def ::query-string string?)
