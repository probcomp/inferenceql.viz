(ns inferenceql.spreadsheets.config
  #?(:cljs (:require-macros [inferenceql.spreadsheets.config-reader :as config-reader])
     :clj (:require [inferenceql.spreadsheets.config-reader :as config-reader])))

(def config (config-reader/read :app))
