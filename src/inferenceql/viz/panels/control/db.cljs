(ns inferenceql.viz.panels.control.db
  (:require [clojure.spec.alpha :as s]
            [clojure.set]
            [inferenceql.viz.store :as store]))

(def default-col-selection
  (-> store/xcat-models first :latents :z keys set))

(def default-db
  {:control-panel {:iteration 0
                   :col-selection default-col-selection
                   :plot-type :select-vs-simulate
                   :marginal-types #{:1D}
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
(s/def ::marginal-types #(and (set? %)
                              (clojure.set/subset? % #{:1D :2D})))
(s/def ::show-plot-options boolean?)
(s/def ::mi-threshold number?)

(s/def ::cluster-selected (s/keys :req-un [::cluster-id ::view-id]))
(s/def ::cluster-id integer?)
(s/def ::view-id integer?)
