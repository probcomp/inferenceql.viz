(ns inferenceql.spreadsheets.panels.jsmodel.subs
  (:require [cljstache.core :refer [render]]
            [inferenceql.spreadsheets.config :refer [config]]
            [inferenceql.spreadsheets.model :as model]
            [inferenceql.spreadsheets.panels.jsmodel.multimix :as multimix]
            [re-frame.core :as rf]))

(defn ^:sub source-code
  "Returns the current model as javascript program text."
  [model]
  (let [multimix-spec (inferenceql.inference.gpm.crosscat/xcat->mmix model)]
    (render (:js-model-template config) (multimix/template-data multimix-spec))))
(rf/reg-sub :jsmodel/source-code
            :<- [:query/model]
            source-code)

