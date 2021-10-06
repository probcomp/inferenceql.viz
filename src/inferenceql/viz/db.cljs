(ns inferenceql.viz.db
  (:require [clojure.spec.alpha :as s]
            [inferenceql.viz.components.store.db :as store-component]))

;;; Primary DB spec.

(s/def ::db (s/keys :req-un [::store-component/store-component]))

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
             store-component/default-db]]
    (apply merge dbs)))
