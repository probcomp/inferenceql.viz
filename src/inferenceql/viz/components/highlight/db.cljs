(ns inferenceql.viz.components.highlight.db
  (:require [clojure.spec.alpha :as s]))

(def default-db
  {:highlight-component nil})

(s/def ::highlight-component (s/nilable (s/keys :opt-un [::row-likelihoods
                                                         ::missing-cells])))

(s/def ::score number?)

;;; Specs related to computed scores of existing rows.

(s/def ::row-likelihoods (s/coll-of ::score))

;;; Specs related to computed scores of missing cells.

(s/def ::column-name string?)
(s/def ::value any?)
(s/def ::meets-threshold boolean?)
(s/def ::value-score-map (s/keys :req-un [::value
                                          ::score]
                                 :opt-un [::meets-threshold]))

(s/def ::map-for-row (s/map-of ::column-name ::value-score-map))
(s/def ::missing-cells (s/coll-of ::map-for-row))

;;; Accessor functions to parts of the highlight component db.

(defn row-likelihoods [db]
  (get-in db [:highlight-component :row-likelihoods]))

(defn missing-cells [db]
  (get-in db [:highlight-component :missing-cells]))
