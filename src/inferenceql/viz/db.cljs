(ns inferenceql.viz.db
  (:require [clojure.spec.alpha :as s]
            [inferenceql.viz.panels.sd2.model.db :as sd2-model-panel]
            [inferenceql.viz.panels.sd2.start.db :as sd2-start-panel]
            [inferenceql.viz.panels.sd2.sim.db :as sd2-sim-panel]
            [inferenceql.viz.components.store.db :as store-component]))

;;; Primary DB spec.

(s/def ::db (s/keys :req-un [::sd2-model-panel/sd2-model-panel
                             ::sd2-start-panel/sd2-start-panel
                             ::sd2-sim-panel/sd2-sim-panel
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
             sd2-model-panel/default-db
             sd2-start-panel/default-db
             sd2-sim-panel/default-db
             store-component/default-db]]
    (apply merge dbs)))
