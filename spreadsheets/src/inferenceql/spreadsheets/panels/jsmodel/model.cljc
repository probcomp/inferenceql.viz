(ns inferenceql.spreadsheets.panels.jsmodel.model
  "Holds a def for the javascript source code version of the default multimix model."
  (:require [cljstache.core :refer [render]]
            [inferenceql.spreadsheets.config :refer [config]]
            [inferenceql.spreadsheets.model :as model]
            [inferenceql.spreadsheets.panels.jsmodel.multimix :as multimix]))

(def source-code
  "The default multimix model as javascript program text."
  (render (:js-model-template config) (multimix/template-data model/spec)))
