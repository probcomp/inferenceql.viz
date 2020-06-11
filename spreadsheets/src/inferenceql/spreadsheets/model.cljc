(ns inferenceql.spreadsheets.model
  (:require [inferenceql.spreadsheets.config :as config]))

(def spec (get config/config :model))
