(ns inferenceql.spreadsheets.panels.crosscat.db
  (:require [clojure.spec.alpha :as s]))

(def default-db
  {:crosscat-panel {:option :alpha}})

(s/def ::crosscat-panel (s/keys :opt-un [::option]))

;; Option is a setting that can take on the following values.
(s/def ::option #{:alpha :beta :gamma})
