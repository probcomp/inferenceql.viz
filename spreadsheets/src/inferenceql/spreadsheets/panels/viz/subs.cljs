(ns inferenceql.spreadsheets.panels.viz.subs
  (:require [re-frame.core :as rf]
            [metaprob.prelude :as mp]
            [inferenceql.multimixture :as mmix]
            [inferenceql.multimixture.search :as search]
            [inferenceql.spreadsheets.model :as model]
            [inferenceql.spreadsheets.panels.viz.vega :as vega]
            [inferenceql.spreadsheets.panels.override.helpers :as co]
            [inferenceql.spreadsheets.panels.table.handsontable :as hot]))

(rf/reg-sub :viz/vega-lite-spec
            :<- [:table/selection-layers-list]
            (fn [selection-layers]
              (clj->js
                (vega/generate-spec selection-layers))))

(rf/reg-sub :viz/vega-lite-log-level
            :<- [:table/one-cell-selected]
            (fn [one-cell-selected]
              (if one-cell-selected
                (.-Error js/vega)
                (.-Warn js/vega))))

(rf/reg-sub :viz/generator
            (fn []
              nil))
