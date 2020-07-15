(ns inferenceql.spreadsheets.model
  (:require [inferenceql.spreadsheets.config :as config]
            [inferenceql.inference.gpm :as gpm]))

(def spec (gpm/dpmm (get config/config :model)))
