(ns inferenceql.spreadsheets.panels.viz.db
  (:require [clojure.spec.alpha :as s]))

(def default-db
  {:viz-panel nil})

(s/def ::viz-panel (s/nilable (s/keys :opt-un [::pts-store])))
(s/def ::pts-store any?)

;; Accessor functions for indexing into parts of the control-panel's db.

(defn pts-store [db]
  (get-in db [:viz-panel :pts-store]))
