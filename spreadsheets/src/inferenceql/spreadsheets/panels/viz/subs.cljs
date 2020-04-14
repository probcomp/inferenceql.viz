(ns inferenceql.spreadsheets.panels.viz.subs
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.panels.viz.vega :as vega]
            [medley.core :as medley]))

(rf/reg-sub :viz/timestep
            (fn [db _]
              (get-in db [:viz-panel :timestep])))

(rf/reg-sub :viz/points
            :<- [:viz/timestep]
            (fn [timestep]
              (vega/agent-points timestep)))

(rf/reg-sub :viz/vega-lite-spec
            :<- [:viz/points]
            (fn [points]
              (clj->js
                (vega/map-spec points))))

(rf/reg-sub :viz/generators
            (fn []
              ;{:points #(vega/points 1)}))
              nil))
