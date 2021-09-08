(ns inferenceql.viz.views
  (:require [re-frame.core :as rf]
            [re-com.core :refer [v-box h-box gap]]
            [inferenceql.viz.config :refer [config]]
            [inferenceql.viz.panels.learning.views :as learning]
            [medley.core :as medley]
            [inferenceql.inference.gpm.crosscat :as crosscat]
            [inferenceql.viz.panels.jsmodel.multimix :as multimix]
            [cljstache.core :refer [render]]
            [inferenceql.auto-modeling.js :refer [import-cgpm]]
            [inferenceql.viz.panels.jsmodel.views :refer [js-code-block]]
            [inferenceql.viz.js.components.plot.views :refer [vega-lite]]
            [inferenceql.viz.panels.viz.circle :refer [circle-viz-spec]]
            [clojure.math.combinatorics :refer [combinations]]))

;;;; Views are expressed in Hiccup-like syntax. See the Reagent docs for more info.

(defn app
  []
  (let [iteration @(rf/subscribe [:learning/iteration])
        cgpm-models (:transitions config)

        datasets @(rf/subscribe [:store/datasets])
        rows (->> (get-in datasets [:data :rows])
                  (map #(medley/remove-vals nil? %)))

        cgpm-model (nth cgpm-models iteration)
        num-rows (count (get cgpm-model "X"))
        xcat-model (import-cgpm cgpm-model
                                (take num-rows rows)
                                (:mapping-table config)
                                (:schema config)) ; TODO: better to use the schema in the db.
        mmix-model (crosscat/xcat->mmix xcat-model)
        js-model-text (render (:js-model-template config)
                              (multimix/template-data mmix-model))


        node-names (map keyword (get cgpm-model "names"))
        view-assignment (fn [col]
                          (let [col-to-num (zipmap node-names (get cgpm-model "outputs"))
                                col-num-to-view-num (into {} (get cgpm-model "Zv"))]
                            (-> col col-to-num col-num-to-view-num)))
        views (vals (group-by view-assignment node-names))
        edges (mapcat #(combinations % 2) views)
        circle-spec (circle-viz-spec node-names edges)]
    [v-box
     :margin "20px"
     :children [[learning/panel]
                [gap :size "10px"]
                [h-box
                 :children [[vega-lite circle-spec {:actions false :mode "vega"} nil nil]
                            [gap :size "20px"]
                            [js-code-block js-model-text]]]]]))
