(ns inferenceql.spreadsheets.panels.viz.subs
  (:require [re-frame.core :as rf]
            [metaprob.prelude :as mp]
            [inferenceql.multimixture :as mmix]
            [inferenceql.multimixture.search :as search]
            [inferenceql.spreadsheets.model :as model]
            [inferenceql.spreadsheets.panels.viz.vega :as vega]
            [inferenceql.spreadsheets.panels.override.helpers :as co]
            [inferenceql.spreadsheets.panels.table.handsontable :as hot]
            [medley.core :as medley]))

(rf/reg-sub :viz/vega-lite-spec
            (fn []
              (clj->js
                (let [spec (vega/map-spec)]
                  (.log js/console "spec: " spec)
                  spec))))

(defn make-simulate-fn
  [col-to-sim row override-fns]
  (let [override-map (select-keys override-fns [col-to-sim])
        override-insert-fn (co/gen-insert-fn override-map)
        constraints (mmix/with-row-values {} (-> row
                                                 (select-keys (keys (:vars model/spec)))
                                                 (dissoc col-to-sim)))
        gen-fn #(first (mp/infer-and-score :procedure (search/optimized-row-generator model/spec)
                                           :observation-trace constraints))
        has-negative-vals? #(some (every-pred number? neg?) (vals %))]
    ;; returns the first result of gen-fn that doesn't have a negative salary
    ;; TODO: (remove negative-vals? ...) is a hack for StrangeLoop2019
    #(take 1 (map override-insert-fn (remove has-negative-vals? (repeatedly gen-fn))))))

(rf/reg-sub :viz/generators
            :<- [:table/selection-layers]
            :<- [:override/column-override-fns]
            (fn [[layers override-fns]]
              (when false
                (->> layers
                     (medley/map-vals (fn [layer]
                                        (let [{selections :selections
                                               cols :selected-columns
                                               row :row-at-selection-start} layer]
                                          (when (vega/simulatable? selections cols)
                                            (make-simulate-fn (first cols) row override-fns)))))
                     (medley/remove-vals nil?)))))
