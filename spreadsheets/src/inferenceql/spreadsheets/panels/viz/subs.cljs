(ns inferenceql.spreadsheets.panels.viz.subs
  (:require [re-frame.core :as rf]
            [metaprob.prelude :as mp]
            [inferenceql.multimixture :as mmix]
            [inferenceql.multimixture.search :as search]
            [inferenceql.spreadsheets.model :as model]
            [inferenceql.spreadsheets.panels.viz.vega :as vega]
            [inferenceql.spreadsheets.panels.override.helpers :as co]
            [inferenceql.spreadsheets.panels.table.handsontable :as hot]))

;; These are column names that cannot be simulated.
;; `hot/label-col-header` and `hot/score-col-header` are not part of any dataset.
;; And `geo-fips` and `NAME` are columns from the NYTimes dataset that have been excluded.
(def cols-invalid-for-sim #{"geo_fips" "NAME" hot/label-col-header hot/score-col-header})

(rf/reg-sub :viz/column-to-simulate
            :<- [:table/selected-columns]
            (fn [columns]
              (first columns)))

(rf/reg-sub :viz/selection-simulatable
            :<- [:table/one-cell-selected]
            :<- [:viz/column-to-simulate]
            (fn [[one-cell-selected col]]
              (and one-cell-selected
                   (not (contains? cols-invalid-for-sim col)))))

(rf/reg-sub :viz/selection-facetable
            :<- [:table/selected-columns]
            (fn [cols]
              false))

(rf/reg-sub :viz/selections-faceted
            :<- [:table/selection-layers]
            :<- [:viz/selection-facetable]
            ;; TODO: modify to use selection layers.
            (fn [[table-states selection-facetable]]
              (when selection-facetable
                (let [facet-attr :table
                      selection-real (->> (get-in table-states [:real-table :selections])
                                          (map #(assoc % facet-attr "Real Data")))

                      selection-virtual (->> (get-in table-states [:virtual-table :selections])
                                             (map #(assoc % facet-attr "Virtual Data")))]
                  ;; This are the selections from both the real and virtual tables combined.
                  (concat selection-real selection-virtual)))))

(rf/reg-sub :viz/vega-lite-spec
            ;; Subs related to simulations.
            :<- [:viz/selection-simulatable]
            :<- [:viz/column-to-simulate]
            ;; Subs related to selection state in the last-clicked-on table.
            :<- [:table/selections]
            :<- [:table/selected-columns]
            :<- [:table/row-at-selection-start]
            ;; Subs related to selection data merged between the :real-table and :virtual-table.
            :<- [:viz/selection-facetable]
            :<- [:viz/selections-faceted]
            (fn [[simulatable sim-col selections cols row facetable selections-faceted]]
              (when selections
                ;; When we have a faceted selection use that over the regular selection.
                (let [selections-to-use (if facetable selections-faceted selections)
                      facet-attr (when facetable (name :table))]
                  (clj->js
                    (cond simulatable ; One cell selected.
                          (vega/gen-simulate-plot sim-col row)

                          (= 1 (count cols)) ; One column selected.
                          (vega/gen-histogram (first cols) selections-to-use facet-attr)

                          :else ; Two or more columns selected.
                          (vega/gen-comparison-plot (take 2 cols) selections-to-use facet-attr)))))))

(rf/reg-sub :viz/vega-lite-log-level
            :<- [:table/one-cell-selected]
            (fn [one-cell-selected]
              (if one-cell-selected
                (.-Error js/vega)
                (.-Warn js/vega))))

(rf/reg-sub :viz/generator
            :<- [:viz/selection-simulatable]
            :<- [:viz/column-to-simulate]
            :<- [:table/row-at-selection-start]
            :<- [:override/column-override-fns]
            (fn [[simulatable col-to-sim row override-fns]]
              (when simulatable
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
                  #(take 1 (map override-insert-fn (remove has-negative-vals? (repeatedly gen-fn))))))))
