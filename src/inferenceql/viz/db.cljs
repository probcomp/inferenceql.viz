(ns inferenceql.viz.db
  (:require [clojure.spec.alpha :as s]
            [inferenceql.viz.panels.control.db :as control-panel]
            [inferenceql.viz.panels.override.db :as override-panel]
            [inferenceql.viz.panels.table.db :as table-panel]
            [inferenceql.viz.panels.viz.db :as viz-panel]
            [inferenceql.viz.panels.more.db :as more-panel]
            [inferenceql.viz.panels.modal.db :as modal-panel]
            [inferenceql.viz.components.query.db :as query-component]
            [inferenceql.viz.components.store.db :as store-component]))

;;; Primary DB spec.

(s/def ::db (s/keys :req-un [::control-panel/control-panel
                             ::override-panel/override-panel
                             ::table-panel/table-panel
                             ::viz-panel/viz-panel
                             ::more-panel/more-panel
                             ::modal-panel/modal-panel
                             ::query-component/query-component
                             ::store-component/store-component]))

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
             query-component/default-db
             store-component/default-db]]
    (apply merge dbs)))
