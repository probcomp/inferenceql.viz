(ns inferenceql.viz.panels.sd2.start.db
  (:require [clojure.spec.alpha :as s]
            [inferenceql.viz.config :as config]))


(def default-db
  {:sd2-start-panel {:gene-recs (:gene-recs config/config)
                     :gene-growth-curves (:gene-growth-curves config/config)}})


(s/def ::sd2-start-panel (s/keys :req-un []))


