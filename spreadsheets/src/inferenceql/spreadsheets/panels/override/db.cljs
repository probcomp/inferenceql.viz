(ns inferenceql.spreadsheets.panels.override.db
  (:require [clojure.spec.alpha :as s]))

(def default-db
  {:override-panel {:modal nil
                    :column-overrides nil
                    :column-override-fns nil}})

(s/def ::override-panel (s/keys :req-un [::modal
                                         ::column-overrides
                                         ::column-override-fns]))

;;; Specs related to foreign function overrides on columns.

(s/def ::column-name string?)
(s/def ::function-text string?)
(s/def ::function-obj fn?)
(s/def ::column-overrides (s/nilable (s/map-of ::column-name ::function-text)))
(s/def ::column-override-fns (s/nilable (s/map-of ::column-name ::function-obj)))

;;; Specs related to modal for entering foreign functions.

(s/def ::reagent-comp fn?)
(s/def ::child (s/nilable (s/tuple ::reagent-comp
                                   ::column-name
                                   (s/nilable ::function-text))))
(s/def ::size #{:extra-small :small :large :extra-large})
(s/def ::modal (s/nilable (s/keys :opt-un [::child
                                           ::size])))
