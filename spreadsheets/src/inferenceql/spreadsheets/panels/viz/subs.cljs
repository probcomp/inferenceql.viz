(ns inferenceql.spreadsheets.panels.viz.subs
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.panels.viz.vega :as vega]
            [medley.core :as medley]))

(rf/reg-sub :viz/points
            (fn [db _]
              (get-in db [:viz-panel :points])))

(rf/reg-sub :viz/timestep
            (fn [db _]
              (get-in db [:viz-panel :timestep])))

(rf/reg-sub :viz/vega-lite-spec
            :<- [:viz/points]
            (fn [points]
              (clj->js
                (let [spec (vega/map-spec points)]
                  (.log js/console "spec: " spec)
                  spec))))

(rf/reg-sub :viz/generators
            (fn []
              ;{:points #(vega/points 1)}))
              nil))
