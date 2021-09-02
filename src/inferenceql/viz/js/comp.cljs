(ns inferenceql.viz.js.comp
  (:require [inferenceql.viz.js.components.plot.core
             :refer [plot] :rename {plot plot-comp}]
            [inferenceql.viz.js.components.table.core
             :refer [table] :rename {table table-comp}]
            [inferenceql.viz.js.components.mini-app.core
             :refer [mini-app] :rename {mini-app mini-app-comp}]))

(def ^:export plot plot-comp)

(def ^:export table table-comp)

(def ^:export mini-app mini-app-comp)
