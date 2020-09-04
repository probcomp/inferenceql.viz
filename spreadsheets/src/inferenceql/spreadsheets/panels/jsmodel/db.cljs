(ns inferenceql.spreadsheets.panels.jsmodel.db
  "Contains the initial state of the db corresponding to the jsmodel-panel
  along with related specs."
  (:require [clojure.spec.alpha :as s]))

(def default-db
  {:jsmodel-panel {:show-model false}})

(s/def ::viz-panel (s/keys :req-un [::show-model]))

;; This determines whether the jsmodel panel is displayed or not.
(s/def ::show-model boolean?)
