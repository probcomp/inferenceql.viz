(ns inferenceql.viz.panels.control.db
  (:require [clojure.spec.alpha :as s]
            [inferenceql.viz.config :refer [config transitions]]))

(def default-db
  {:control-panel {:iteration 0
                   :col-selection (set (map keyword (get (first transitions) "col_names")))
                   :plot-type :select-vs-simulate
                   :marginal-types #{:1D}
                   ; :cluster-selected
                   :show-plot-options false
                   :mi-threshold 0}})

(s/def ::control-panel (s/keys :req-un [::iteration
                                        ::col-selection
                                        ::plot-type
                                        ::marginal-types
                                        ::show-plot-options
                                        ::mi-threshold]
                               :opt-un [::cluster-selected]))

(s/def ::iteration integer?)
(s/def ::col-selection set?)
(s/def ::plot-type #{:select-vs-simulate :mutual-information})
(s/def ::marginal-types some?) ;; TODO
(s/def ::show-plot-options boolean?)
(s/def ::mi-threshold number?)

(s/def ::cluster-selected some?) ;; TODO
