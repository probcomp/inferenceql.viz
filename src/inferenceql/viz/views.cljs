(ns inferenceql.viz.views
  (:require [re-frame.core :as rf]
            [re-com.core :refer [v-box]]
            [inferenceql.viz.config :refer [config]]
            [inferenceql.viz.panels.learning.views :as learning]
            [medley.core :as medley]
            [inferenceql.inference.gpm.crosscat :as crosscat]
            [inferenceql.viz.panels.jsmodel.multimix :as multimix]
            [cljstache.core :refer [render]]
            [inferenceql.auto-modeling.js :refer [import-cgpm]]
            [inferenceql.viz.panels.jsmodel.views :refer [js-code-block]]))

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
                              (multimix/template-data mmix-model))]
    [v-box
     :children [[learning/panel]
                [js-code-block js-model-text]
                #_[viz/vega-lite vega-lite-spec {:actions false} generators pts-store]]]))
