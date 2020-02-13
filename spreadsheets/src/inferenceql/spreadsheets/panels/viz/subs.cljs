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
            ;; Subs related to simulations.
            :<- [:viz/selection-simulatable]
            :<- [:viz/column-to-simulate]
            ;; Subs related to selection state in the last-clicked-on table.
            :<- [:table/selections]
            :<- [:table/selected-columns]
            :<- [:table/row-at-selection-start]
            (fn [[simulatable sim-col selections cols row]]
              (when selections
                (clj->js
                  (cond simulatable ; One cell selected.
                        (vega/gen-simulate-plot sim-col row)

                        (= 1 (count cols)) ; One column selected.
                        (vega/gen-histogram (first cols) selections)

                        :else ; Two or more columns selected.
                        (vega/gen-comparison-plot (take 2 cols) selections))))))

(rf/reg-sub :viz/vega-lite-log-level
            :<- [:table/one-cell-selected]
            (fn [one-cell-selected]
              (if one-cell-selected
                (.-Error js/vega)
                (.-Warn js/vega))))

(rf/reg-sub :viz/generator
            (fn []
              nil))
