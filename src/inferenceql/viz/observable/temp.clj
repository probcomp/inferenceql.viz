(ns inferenceql.viz.observable.temp
  (:require [cljstache.core :refer [render]]
            [inferenceql.viz.config :refer [config]]
            [inferenceql.viz.panels.jsmodel.multimix :as multimix]
            [inferenceql.inference.gpm.crosscat :as crosscat]
            [inferenceql.auto-modeling.xcat :as xcat]))

(comment
  (defn ^:export xcatToMMIX
    [model]
    (crosscat/xcat->mmix model))

  (defn ^:export xcatToMMIXstr
    [model]
    (str (crosscat/xcat->mmix model)))

  (defn ^:export mmixToJSPROG
    [mmix]
    (render (:js-model-template config) (multimix/template-data mmix)))

  xcat/m

  (xcatToMMIX xcat/m)

  (multimix/template-data (xcatToMMIX xcat/m))

  (print (mmixToJSPROG (xcatToMMIX xcat/m))))