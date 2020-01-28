(ns inferenceql.spreadsheets.components.highlight.db
  (:require [clojure.spec.alpha :as s]))

(def default-db
  {:highlight-component nil})

(s/def ::highlight-component (s/nilable (s/keys :opt-un [::row-likelihoods
                                                         ::missing-cells])))

(s/def ::score number?)

;;; Specs related to computed scores of existing rows.

(s/def ::row-likelihoods (s/coll-of ::score))

;;; Specs related to computed scores of missing cells.

(s/def :ms/column-name string?)
(s/def :ms/value any?)
(s/def :ms/score ::score)
(s/def :ms/meets-threshold boolean?)
(s/def :ms/value-score-map (s/keys :req-un [:ms/value
                                            :ms/score]
                                   :opt-un [:ms/meets-threshold]))
(s/def :ms/map-for-row (s/map-of :ms/column-name :ms/value-score-map))
(s/def ::missing-cells (s/coll-of :ms/map-for-row))
