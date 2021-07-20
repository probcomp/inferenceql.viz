(ns inferenceql.viz.observable.jsprogram
  (:require [cljstache.core :refer [render]]
            [inferenceql.viz.config :refer [config]]
            [inferenceql.viz.panels.jsmodel.multimix :as multimix]
            [inferenceql.inference.gpm.crosscat :as crosscat]))

(defn ^:export xcatToMMIX
  [model]
  (crosscat/xcat->mmix model))

(defn ^:export xcatToMMIXstr
  [model]
  (str (crosscat/xcat->mmix model)))

(defn ^:export mmixToJSPROG
  [mmix]
  (render (:js-model-template config) (multimix/template-data mmix)))
