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

(rf/reg-sub :viz/vega-lite-spec-blue
            :<- [:table/selection-layers-list-blue]
            :<- [:table/computed-rows]
            (fn [[selection-layers table-rows]]
              (clj->js
                (vega/generate-spec selection-layers table-rows))))

(rf/reg-sub :viz/vega-lite-spec-green
            :<- [:table/selection-layers-list-green]
            :<- [:table/computed-rows]
            (fn [[selection-layers table-rows]]
              (clj->js
                (vega/generate-spec selection-layers table-rows))))

(rf/reg-sub :viz/vega-lite-spec-red
            :<- [:table/selection-layers-list-red]
            :<- [:table/computed-rows]
            (fn [[selection-layers table-rows]]
              (clj->js
                (vega/generate-spec selection-layers table-rows))))

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

(rf/reg-sub :viz/generator-blue
            :<- [:table/selection-layer-blue]
            :<- [:override/column-override-fns]
            (fn [[layers override-fns]]
              (->> layers
                   (medley/map-vals (fn [layer]
                                      (let [{selections :selections
                                             cols :selected-columns
                                             row :row-at-selection-start} layer]
                                        (when (vega/simulatable? selections (first cols))
                                          (make-simulate-fn (first cols) row override-fns)))))
                   (medley/remove-vals nil?))))

(rf/reg-sub :viz/generator-green
            :<- [:table/selection-layer-green]
            :<- [:override/column-override-fns]
            (fn [[layers override-fns]]
              (->> layers
                   (medley/map-vals (fn [layer]
                                      (let [{selections :selections
                                             cols :selected-columns
                                             row :row-at-selection-start} layer]
                                        (when (vega/simulatable? selections (first cols))
                                          (make-simulate-fn (first cols) row override-fns)))))
                   (medley/remove-vals nil?))))

(rf/reg-sub :viz/generator-red
            :<- [:table/selection-layer-red]
            :<- [:override/column-override-fns]
            (fn [[layers override-fns]]
              (->> layers
                   (medley/map-vals (fn [layer]
                                      (let [{selections :selections
                                             cols :selected-columns
                                             row :row-at-selection-start} layer]
                                        (when (vega/simulatable? selections (first cols))
                                          (make-simulate-fn (first cols) row override-fns)))))
                   (medley/remove-vals nil?))))

(rf/reg-sub :viz/vega-mode-blue
            :<- [:table/selection-layer-blue]
            (fn [selection-layers]
              (let [{selections :selections
                     cols :selected-columns} (:blue selection-layers)
                    sim (vega/simulatable? selections (first cols))
                    c (first cols)
                    first-col-nominal (= "nominal" (when c (vega/get-col-type c)))]
                (if (and first-col-nominal (= 1 (count cols)))
                  "vega"
                  "vega-lite"))))

(rf/reg-sub :viz/vega-mode-green
            :<- [:table/selection-layer-green]
            (fn [selection-layers]
              (let [{selections :selections
                     cols :selected-columns} (:green selection-layers)
                    sim (vega/simulatable? selections (first cols))
                    c (first cols)
                    first-col-nominal (= "nominal" (when c (vega/get-col-type c)))]
                (if (and first-col-nominal (= 1 (count cols)))
                  "vega"
                  "vega-lite"))))

(rf/reg-sub :viz/vega-mode-red
            :<- [:table/selection-layer-red]
            (fn [selection-layers]
              (let [{selections :selections
                     cols :selected-columns} (:red selection-layers)
                    sim (vega/simulatable? selections (first cols))
                    c (first cols)
                    first-col-nominal (= "nominal" (when c (vega/get-col-type c)))]
                (if (and first-col-nominal (= 1 (count cols)))
                  "vega"
                  "vega-lite"))))
