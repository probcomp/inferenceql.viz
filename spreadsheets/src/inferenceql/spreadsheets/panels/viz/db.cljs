(ns inferenceql.spreadsheets.panels.viz.db
  (:require [clojure.spec.alpha :as s]))

(def default-db
  {:viz-panel {}})

(s/def ::viz-panel (s/keys :opt-un [::pts-store]))
(s/def ::pts-store (s/coll-of ::store-elem))

(s/def ::store-elem (s/keys :req-un [::fields
                                     ::values]))

;;; ::fields related

(s/def ::fields (s/coll-of ::field-def :kind vector?))
(s/def ::field-def (s/keys :req-un [::field
                                    ::type]
                           :opt-un [::channel]))
(s/def ::field string?)
(s/def ::type #{"R" "E"})
(s/def ::channel #{"x" "y"})

;;; ::values related

(s/def ::values (s/or :single-collection ::value-seq  ;; This form is returned by selections in choropleths.
                      :multiple-collections (s/coll-of ::value-seq :kind vector?)))
(s/def ::value-seq (s/coll-of any? :kind vector?))



