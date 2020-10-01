(ns inferenceql.spreadsheets.panels.control.db
  (:require [clojure.spec.alpha :as s]))

(def default-db
  {:control-panel {:confidence-threshold 0.9
                   :reagent-forms {:confidence-mode :none}
                   :query-string "SELECT * FROM data;"
                   :selection-color :blue

                   :dataset-name :data
                   :model-name :model
                   :virtual false}})

(s/def ::control-panel (s/keys :req-un [::confidence-threshold
                                        ::reagent-forms
                                        ::query-string
                                        ::selection-color]
                               :opt-un [::dataset-name
                                        ::model-name
                                        ::virtual]))

(s/def ::confidence-threshold number?)
(s/def ::reagent-forms (s/keys :req-un [::confidence-mode]))
(s/def ::confidence-mode keyword?)
(s/def ::selection-color keyword?)
(s/def ::query-string string?)

(s/def ::dataset-name keyword?)
(s/def ::model-name keyword?)
(s/def ::virtual boolean?)

;; Accessor functions for indexing into parts of the control-panel's db.

(defn selection-color [db]
  (get-in db [:control-panel :selection-color]))
