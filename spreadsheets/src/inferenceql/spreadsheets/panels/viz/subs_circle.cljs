(ns inferenceql.spreadsheets.panels.viz.subs-circle
  (:require [re-frame.core :as rf]
            [medley.core :as medley]
            [inferenceql.spreadsheets.panels.viz.circle :as circle]))

(rf/reg-sub :circle/num-nodes
            (fn [db]
              (get-in db [:viz-panel :circle :num-nodes] 25)))

(rf/reg-sub :circle/tree
            :<- [:circle/num-nodes]
            (fn [num-nodes]
              (circle/tree num-nodes)))

(rf/reg-sub :circle/dependencies
            :<- [:circle/tree]
            (fn [tree]
              (circle/dependencies tree)))

(rf/reg-sub :circle/spec
            :<- [:circle/dependencies]
            :<- [:circle/tree]
            (fn [[dependencies tree]]
              (let [spec (clj->js
                           (circle/spec tree dependencies))]
                spec)))
