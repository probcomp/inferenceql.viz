(ns inferenceql.viz.views
  (:require [re-frame.core :as rf]
            [re-com.core :refer [v-box h-box box gap line]]
            [inferenceql.viz.panels.sd2.start.views :as sd2-start]
            [inferenceql.viz.panels.sd2.sim.views :as sd2-sim]))

;;;; Views are expressed in Hiccup-like syntax. See the Reagent docs for more info.

(defn app
  []
  (let [page @(rf/subscribe [:page])]
    (case page
      :start
      [sd2-start/view]

      :knockout-sim
      [sd2-sim/view])))

