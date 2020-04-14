(ns inferenceql.spreadsheets.panels.viz.subs
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.panels.viz.vega :as vega]
            [inferenceql.spreadsheets.panels.viz.circle :as circle]
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



(rf/reg-sub :viz/dependencies
            :<- [:viz/timestep]
            (fn [timestep]
              (vega/circle-dependencies timestep)))

(rf/reg-sub :viz/circle-spec
            :<- [:viz/dependencies]
            (fn [dependencies]
              (let [spec (clj->js
                           (circle/spec (vega/circle-tree) dependencies))]
                (.log js/console "spec: " spec)
                spec)))

(rf/reg-sub :viz/generators
            (fn []
              ;{:points #(vega/points 1)}))
              nil))
