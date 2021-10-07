(ns inferenceql.viz.config
  ;; It's unclear why, but without the following line ClojureScript compilation
  ;; fails if the compiled-in model makes use of data-reader functions from
  ;; namespaces at or below `inferenceql.inference.gpm`.
  (:require [inferenceql.inference.gpm])
  (:require-macros [inferenceql.viz.config-reader :as config-reader]))

(def config (config-reader/read))

