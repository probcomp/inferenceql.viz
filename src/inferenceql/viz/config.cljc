(ns inferenceql.viz.config
  #?(:cljs (:require-macros [inferenceql.viz.config-reader :as config-reader])
     :clj (:require [inferenceql.viz.config-reader :as config-reader])))

(def config (config-reader/read))
