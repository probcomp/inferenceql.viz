(ns inferenceql.spreadsheets.model
  (:require [inferenceql.spreadsheets.config :as config]
            [inferenceql.multimixture.specification :as spec]))

(def spec (get config/config :model))
