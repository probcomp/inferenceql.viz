(ns inferenceql.spreadsheets.db
  (:require [clojure.spec.alpha :as s]
            [inferenceql.spreadsheets.panels.control.db :as control-panel]
            [inferenceql.spreadsheets.panels.override.db :as override-panel]
            [inferenceql.spreadsheets.panels.table.db :as table-panel]))

(s/def ::score number?)
(s/def ::topojson any?)

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

;;; Primary DB spec.

(s/def ::db (s/keys :opt [::topojson
                          ::row-likelihoods
                          ::missing-cells]
                    :req-un [::control-panel/control-panel
                             ::override-panel/override-panel
                             ::table-panel/table-panel]))


(def default-general-db
  "This db map is meant to contain keys and values not specific to
  any application panel."
  {})

(defn default-db
  "When the application starts, this will be the value put in `app-db`.
  It consists of keys and values from the general db
  and panel specific dbs all merged together."
  []
  (let [dbs [default-general-db
             control-panel/default-db
             override-panel/default-db
             table-panel/default-db]]
    (apply merge dbs)))
