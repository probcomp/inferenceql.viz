(ns inferenceql.spreadsheets.model
  (:require [inferenceql.spreadsheets.config :as config]
            [inferenceql.multimixture.specification :as spec]))

(def spec (spec/from-json (get config/config :model)))
