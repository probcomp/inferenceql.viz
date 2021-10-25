(ns inferenceql.viz.js.components.plot.core
  (:require [goog.dom :as dom]
            [reagent.dom :as rdom]
            [cljs-bean.core :refer [->clj]]
            [clojure.walk :refer [postwalk]]
            [inferenceql.viz.panels.viz.views-simple :refer [vega-lite]]
            [inferenceql.viz.js.components.plot.vega :refer [generate-spec]]
            [inferenceql.viz.js.util :refer [clj-schema]]))

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
          component (fn []
                      [:div {:style {:overflow-x "auto"}} ;; This div may not be necessary.
                       [vega-lite spec {:actions false} nil nil]])]
      (rdom/render [component] node)
      node)))
