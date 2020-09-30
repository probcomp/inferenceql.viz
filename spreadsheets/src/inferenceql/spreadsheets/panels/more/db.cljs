(ns inferenceql.spreadsheets.panels.more.db
  (:require [clojure.spec.alpha :as s]))

(def default-db
  {:more-panel {:show-menu false}})

(s/def ::more-panel (s/keys :req-un [::show-menu]))

;; This determines whether the more menu is displayed or not.
(s/def ::show-menu boolean?)
