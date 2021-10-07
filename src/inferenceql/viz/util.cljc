(ns inferenceql.viz.util
  (:require [medley.core :as medley]))

(defn keywordize-kv [a-map]
  "Returns `a-map` with both keys and values keywordized."
  (medley/map-kv (fn [col type] [(keyword col) (keyword type)])
                 a-map))
