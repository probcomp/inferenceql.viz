(ns inferenceql.spreadsheets.components.query.db
  "Contains the initial state of the db corresponding to the query component
  along with related specs."
  (:require [clojure.spec.alpha :as s]))

(def default-db
  {:query-component {:dataset-name :data
                     :model-name :model
                     :virtual false}})

(s/def ::query-component (s/keys :req-un [::dataset-name
                                          ::model-name
                                          ::virtual]))

;; The dataset referenced in the query executed.
(s/def ::dataset-name keyword?)
;; The model referenced in the query executed.
(s/def ::model-name keyword?)
;; Whether the query executed produces virtual data.
(s/def ::virtual boolean?)