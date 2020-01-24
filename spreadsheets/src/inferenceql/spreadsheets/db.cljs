(ns inferenceql.spreadsheets.db
  (:require [clojure.spec.alpha :as s]
            [inferenceql.spreadsheets.data :refer [nyt-data]]
            [inferenceql.spreadsheets.panels.control.db :as control-panel]))

(s/def ::header string?)
(s/def ::row (s/map-of ::header any?))
(s/def ::rows (s/cat :row (s/* ::row)))
(s/def ::virtual-rows ::rows)
(s/def ::headers (s/cat :header (s/* ::header)))

(s/def ::index nat-int?)
(s/def ::row-index ::index)
(s/def ::column-index ::index)

(s/def ::selection (s/coll-of ::row))
(s/def ::selections (s/coll-of ::selection))
(s/def ::selected-columns (s/coll-of ::header))

(s/def ::score number?)
(s/def ::scores (s/coll-of ::score))
(s/def ::virtual-scores (s/coll-of ::score))

(s/def ::label (s/nilable string?))
(s/def ::labels (s/coll-of ::label))

(s/def ::topojson any?)

(s/def ::selected-row-index ::row-index)
(s/def ::row-at-selection-start ::row)
(s/def ::header-clicked boolean?)

;;; Specs related to storing the state of both handsontables

(s/def ::table-id #{:real-table :virtual-table})
(s/def ::table-state (s/nilable (s/keys :opt-un [::row-at-selection-start
                                                 ::selected-row-index
                                                 ::selections
                                                 ::selected-columns
                                                 ::header-clicked])))
(s/def ::hot-state (s/map-of ::table-id ::table-state))

(s/def ::table-last-clicked ::table-id)

;;; Specs related to foreign function overrides on columns.

(s/def ::column-name ::header)
(s/def ::function-text string?)
(s/def ::function-obj fn?)
(s/def ::column-overrides (s/map-of ::column-name ::function-text))
(s/def ::column-override-fns (s/map-of ::column-name ::function-obj))

;;; Specs related to modal for entering foreign functions.

(s/def ::reagent-comp fn?)
(s/def ::child (s/nilable (s/tuple ::reagent-comp
                                   ::column-name
                                   (s/nilable ::function-text))))
(s/def ::size #{:extra-small :small :large :extra-large})
(s/def ::modal (s/keys :opt-un [::child
                                ::size]))

;;; Specs related to computed scores of existing rows.

(s/def ::row-likelihoods (s/coll-of ::score))

;;; Specs related to computed scores of missing cells.

(s/def :ms/value any?)
(s/def :ms/score ::score)
(s/def :ms/meets-threshold boolean?)
(s/def :ms/value-score-map (s/keys :req-un [:ms/value
                                            :ms/score]
                                   :opt-un [:ms/meets-threshold]))
(s/def :ms/map-for-row (s/map-of ::column-name :ms/value-score-map))
(s/def ::missing-cells (s/coll-of :ms/map-for-row))

;;; Primary DB spec.

(s/def ::db (s/keys :req [::headers
                          ::rows
                          ::virtual-rows
                          ::hot-state]
                    :opt [::scores
                          ::virtual-scores
                          ::labels
                          ::topojson
                          ::table-last-clicked
                          ::column-overrides
                          ::column-override-fns
                          ::modal
                          ::row-likelihoods
                          ::missing-cells]
                    :req-un [::control-panel/control-panel]))

(defn table-headers
  [db]
  (get-in db [::headers]))

(defn table-rows
  [db]
  (get-in db [::rows]))

(defn scores
  [db]
  (get-in db [::scores]))

(defn with-scores
  [db scores]
  (assoc-in db [::scores] scores))

(defn with-virtual-scores
  [db scores]
  (assoc-in db [::virtual-scores] scores))

(defn virtual-scores
  [db]
  (get-in db [::virtual-scores]))

(defn clear-virtual-scores
  [db]
  (dissoc db ::virtual-scores))

(defn virtual-rows
  [db]
  (get-in db [::virtual-rows]))

(defn with-virtual-rows
  [db new-v-rows]
  (let [cur-v-rows (virtual-rows db)]
    (assoc-in db [::virtual-rows] (concat new-v-rows cur-v-rows))))

(defn clear-virtual-rows
  [db]
  (assoc-in db [::virtual-rows] []))

(defn with-labels
  [db labels]
  (assoc-in db [::labels] labels))

(defn labels
  [db]
  (get-in db [::labels]))

(def default-general-db
  "This db map is meant to contain keys and values not specific to
  any application panel."
  {::headers (into [] (keys (first nyt-data)))
   ::rows nyt-data
   ::virtual-rows []
   ::hot-state {:real-table nil :virtual-table nil}})

(defn default-db
  "When the application starts, this will be the value put in `app-db`.
  It consists of keys and values from the general db
  and panel specific dbs all merged together."
  []
  (let [dbs [default-general-db
             control-panel/default-db]]
    (apply merge dbs)))
