(ns inferenceql.spreadsheets.db
  (:require [clojure.spec.alpha :as s]
            [inferenceql.spreadsheets.panels.control.db :as control-panel]
            [inferenceql.spreadsheets.panels.override.db :as override-panel]
            [inferenceql.spreadsheets.panels.table.db :as table-panel]
            [inferenceql.spreadsheets.panels.viz.db :as viz-panel]
            [inferenceql.spreadsheets.panels.more.db :as more-panel]
            [inferenceql.spreadsheets.panels.modal.db :as modal-panel]
            [inferenceql.spreadsheets.components.highlight.db :as highlight-component]))

;;; Primary DB spec.

(s/def ::db (s/keys :req-un [::control-panel/control-panel
                             ::override-panel/override-panel
                             ::table-panel/table-panel
                             ::viz-panel/viz-panel
                             ::more-panel/more-panel
                             ::modal-panel/modal-panel
                             ::highlight-component/highlight-component]))

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
             table-panel/default-db
             viz-panel/default-db
             more-panel/default-db
             modal-panel/default-db
             highlight-component/default-db]]
    (apply merge dbs)))
