(ns inferenceql.viz.js.components.plot.core
  (:require [goog.dom :as dom]
            [reagent.dom :as rdom]
            [cljs-bean.core :refer [->clj]]
            [clojure.walk :refer [postwalk]]
            [inferenceql.viz.js.components.plot.views :refer [vega-lite]]
            [inferenceql.viz.js.components.plot.vega :refer [generate-spec]]
            [inferenceql.viz.js.util :refer [clj-schema]]
            [inferenceql.viz.panels.viz.circle :refer [circle-viz-spec]]))

(defn plot
  "Javascript interface to plot UI component. Returns a DOM node with a plot."
  [data schema selections]
  (if (some? data)
    (let [selections (postwalk #(if (string? %) (keyword  %) %)
                               (->clj selections))
          schema (clj-schema schema)
          data (->clj data)
          spec (generate-spec schema data selections)

          node (dom/createElement "div")
          component [vega-lite spec {:actions false} nil nil]]
      (rdom/render component node)
      node)))

(defn circle-plot
  "Javascript interface to circle plot UI component. Returns a DOM node with a circle plot."
  [node-names edges]
  (if (and node-names edges)
    (let [node-names (map keyword (->clj node-names))
          edges (for [e (->clj edges)]
                  (map keyword e))

          spec (circle-viz-spec node-names edges)
          node (dom/createElement "div")
          component [vega-lite spec {:actions false :mode "vega"} nil nil]]
      (rdom/render component node)
      node)))
