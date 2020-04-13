(ns inferenceql.spreadsheets.panels.viz.db
  (:require [clojure.spec.alpha :as s]
            [inferenceql.spreadsheets.data :refer [nyt-data]]))

(def default-db
  {:viz-panel {:points []}})
