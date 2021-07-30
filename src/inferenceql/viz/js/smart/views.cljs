(ns inferenceql.viz.js.smart.views
  (:require [clojure.walk :refer [postwalk]]
            [cljs-bean.core :refer [->clj]]
            [inferenceql.viz.js.components.plot.views :refer [vega-lite]]
            [inferenceql.viz.js.smart.vega :refer [generate-spec]]
            [inferenceql.viz.js.util :refer [clj-schema]]))

(defn anomaly-plot
  "Reagent component for displaying an anomoly plot"
  [data schema selections]
  (if (some? data)
    (let [selections (postwalk #(if (string? %) (keyword  %) %)
                               selections)
          schema (clj-schema schema)
          data (->clj data)
          ;; Custom spec generating function for SMART app.
          spec (generate-spec schema data selections)]
      [vega-lite spec {:actions false} nil nil])))

