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

(rf/reg-sub :viz/vega-lite-spec
            :<- [:table/both-table-states]
            :<- [:table/table-last-clicked]
            :<- [:viz/selection-simulatable]
            :<- [:viz/column-to-simulate]

            :<- [:table/selections]
            :<- [:table/selected-columns]
            :<- [:table/row-at-selection-start]
            (fn [[both-table-states last-clicked selection-simulatable sim-col selections cols row]]
              (when (first selections) ; At least one selection layer.
                (clj->js
                  (cond selection-simulatable ; One cell selected.
                        (vega/gen-simulate-plot sim-col row)

                        (= 1 (count cols)) ; One column selected.
                        (vega/gen-histogram both-table-states last-clicked)

                        (some #{"geo_fips"} cols)
                        (vega/gen-choropleth selections cols)

                        :else ; Two or more columns selected.
                        (vega/gen-comparison-plot both-table-states last-clicked))))))

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

(rf/reg-sub :viz/tables-visualized
            :<- [:table/both-table-states]
            :<- [:table/table-last-clicked]
            :<- [:table/one-cell-selected]
            (fn [[table-states t-clicked one-cell-selected]]
              (let [cols-real (take 2 (get-in table-states [:real-table :selected-columns]))
                    cols-virtual (take 2 (get-in table-states [:virtual-table :selected-columns]))
                    cols-last-selected (take 2 (get-in table-states [t-clicked :selected-columns]))]

                ;; This first case is when we have data from the same columns selected in both
                ;; the real-data table and the virtual-data table and more than a
                ;; single cell is selected in both plots.
                (cond (and (= cols-real cols-virtual)
                           (< 0 (count cols-real))
                           (< 0 (count cols-virtual))
                           (not one-cell-selected))
                      ;; Return the names of both tables.
                      (keys table-states)

                      ;; This case is when we have at least one column selected
                      ;; in the last clicked on table.
                      (< 0 (count cols-last-selected))
                      ;; Return the name of just the last-clicked-on table.
                      [t-clicked]

                      ;; This case is when we have no selections in either table. This would result
                      ;; when the user uses alt-click to deselect all in both tables.
                      :else
                      nil))))

(rf/reg-sub :viz/real-table-in-viz
            :<- [:viz/tables-visualized]
            (fn [tables-visualized]
              (some #{:real-table} tables-visualized)))

(rf/reg-sub :viz/virtual-table-in-viz
            :<- [:viz/tables-visualized]
            (fn [tables-visualized]
              (some #{:virtual-table} tables-visualized)))
