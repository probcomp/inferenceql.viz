(ns inferenceql.viz.panels.modal.db
  (:require [clojure.spec.alpha :as s]))

(def default-db
  {:modal-panel {}})

(s/def ::modal-panel (s/keys :opt-un [::content]))

;; ::content must be valid hiccup for display in the modal.
(s/def ::content (s/coll-of any? :kind vector? :min-count 1))
