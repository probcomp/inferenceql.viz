(ns inferenceql.spreadsheets.config
  (:require-macros [inferenceql.spreadsheets.config :refer [read-config]]))

(def config (read-config "config.edn"))
