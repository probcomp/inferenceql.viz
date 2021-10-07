(ns inferenceql.viz.db
  (:require [clojure.spec.alpha :as s]
            [inferenceql.viz.components.store.db :as store-component]
            [inferenceql.viz.panels.control.db :as control-panel]))

;;; Primary DB spec.

(s/def ::db (s/keys :req-un [::store-component/store-component
                             ::control-panel/control-panel]))

(defn default-db
  "When the application starts, this will be the value put in `app-db`.
  It consists of keys and values from the general db
  and panel specific dbs all merged together."
  []
  (let [dbs [store-component/default-db
             control-panel/default-db]]
    (apply merge dbs)))
