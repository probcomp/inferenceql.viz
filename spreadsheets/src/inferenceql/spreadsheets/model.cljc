(ns inferenceql.spreadsheets.model
  (:require [inferenceql.spreadsheets.config :as config]
            [inferenceql.inference.multimixture.specification :as spec]))

(def spec (get config/config :model))
