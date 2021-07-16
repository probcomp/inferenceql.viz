(ns inferenceql.viz.panels.sd2.start.db
  (:require [clojure.spec.alpha :as s]
            [inferenceql.viz.config :refer [config]]))

(def default-db
  {:sd2-start-panel {:rec-genes-filter true
                     :not-rec-genes-filter false

                     :gene-selection-list-rec  (:gene-selection-list-rec config)
                     :gene-selection-list-not-rec (:gene-selection-list-not-rec config)

                     :plot-data-rec (:plot-data-rec config)
                     :plot-data-not-rec (:plot-data-not-rec config)}})

(s/def ::sd2-start-panel (s/keys :req-un []))
