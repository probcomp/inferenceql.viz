(ns inferenceql.viz.panels.viz.db
  "Contains the initial state of the db corresponding to the viz-panel
  along with related specs."
  (:require [clojure.spec.alpha :as s]))

(def default-db
  {:viz-panel {}})

(s/def ::viz-panel (s/keys :opt-un [::pts-store]))

;; This holds the vega dataset, "pts-store".
;; This dataset corresponds to the vega-lite selection named "pts".
;; The format is determined by vega-lite.
;; See for more info: https://github.com/vega/vega-lite/issues/1830
;; We use this to store data representing the selections within vega-lite plots.
(s/def ::pts-store (s/coll-of ::store-elem))
(s/def ::store-elem (s/keys :req-un [::fields
                                     ::values]))

;;; ::fields related specs.

(s/def ::fields (s/coll-of ::field-def :kind vector?))
(s/def ::field-def (s/keys :req-un [::field
                                    ::type]
                           :opt-un [::channel]))
(s/def ::field string?)
;; Represets whether the selection for this field is a range ("R") selection or
;; a entity ("E") selection.
(s/def ::type #{"R" "E"})
;; Represents which vega-lite encoding channel this field's selection corresponds to.
(s/def ::channel #{"x" "y"})

;;; ::values related specs.

(s/def ::values (s/or :single-collection ::value-seq  ;; This form is returned by selections in choropleths.
                      :multiple-collections (s/coll-of ::value-seq :kind vector?)))
(s/def ::value-seq (s/coll-of any? :kind vector?))



