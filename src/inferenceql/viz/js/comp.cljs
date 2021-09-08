(ns inferenceql.viz.js.comp
  (:require [inferenceql.viz.js.components.plot.core
             :refer [plot circle-plot js-highlight] :rename {plot plot-comp
                                                             circle-plot circle-plot-comp
                                                             js-highlight js-highlight-comp}]
            [inferenceql.viz.js.components.table.core
             :refer [table] :rename {table table-comp}]
            [inferenceql.viz.js.components.mini-app.core
             :refer [mini-app] :rename {mini-app mini-app-comp}]))

(def ^:export plot plot-comp)
(def ^:export circle-plot circle-plot-comp)
(def ^:export js-highlight js-highlight-comp)

(def ^:export table table-comp)

(def ^:export mini-app mini-app-comp)
