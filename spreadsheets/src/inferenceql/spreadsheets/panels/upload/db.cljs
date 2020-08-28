(ns inferenceql.spreadsheets.panels.upload.db
  "Contains the initial state of the db corresponding to the upload-panel
  along with related specs."
  (:require [clojure.spec.alpha :as s]))

(def default-db
  {:upload-panel {:display false}})

(s/def ::upload-panel (s/keys :req-un [::display]))

;; This determines whether the upload panel is displayed or not.
(s/def ::display boolean?)
