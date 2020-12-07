(ns inferenceql.viz.panels.jsmodel.subs
  (:require [cljstache.core :refer [render]]
            [inferenceql.viz.config :refer [config]]
            [inferenceql.viz.model :as model]
            [inferenceql.viz.panels.jsmodel.multimix :as multimix]
            [inferenceql.inference.gpm.crosscat :as crosscat]
            [re-frame.core :as rf]))

(defn ^:sub source-code
  "Returns the current model as javascript program text."
  [model]
  (let [multimix-spec (crosscat/xcat->mmix model)]
    (render (:js-model-template config) (multimix/template-data multimix-spec))))
(rf/reg-sub :jsmodel/source-code
            :<- [:query/model]
            source-code)

