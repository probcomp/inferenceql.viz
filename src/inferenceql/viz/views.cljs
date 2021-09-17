(ns inferenceql.viz.views
  (:require [re-frame.core :as rf]
            [re-com.core :refer [v-box h-box gap box]]
            [inferenceql.viz.config :refer [config transitions]]
            [inferenceql.viz.panels.learning.views :as learning]
            [medley.core :as medley]
            [inferenceql.inference.gpm.crosscat :as crosscat]
            [inferenceql.viz.panels.jsmodel.multimix :as multimix]
            [cljstache.core :refer [render]]
            [inferenceql.auto-modeling.js :refer [import-cgpm]]
            [inferenceql.viz.panels.jsmodel.views :refer [js-code-block]]
            [inferenceql.viz.js.components.plot.views :refer [vega-lite]]
            [inferenceql.viz.panels.viz.circle :refer [circle-viz-spec]]
            [clojure.math.combinatorics :refer [combinations]]
            [inferenceql.inference.gpm :as gpm]
            [inferenceql.auto-modeling.qc.vega.dashboard :as dashboard]
            [inferenceql.viz.components.store.db :as store-db]
            [inferenceql.viz.js.components.table.views :refer [handsontable]]))

(def rows (->> store-db/compiled-in-dataset
               (map #(medley/remove-vals nil? %))))

(def schema store-db/compiled-in-schema)

;; TODO: Off load all this stuff into DVC stages.
(def cgpm-models transitions)

(def xcat-models (map (fn [cgpm]
                        (let [num-rows (count (get cgpm "X"))]
                          (import-cgpm cgpm (take num-rows rows) (:mapping-table config) schema)))
                      cgpm-models))
(def mmix-models (doall (map crosscat/xcat->mmix xcat-models)))


(def num-points-at-iter (map #(count (get % "X")) cgpm-models))
(def num-points-required (map - num-points-at-iter (conj num-points-at-iter 0)))

(defn sample-xcat
  "Samples all targets from an XCat gpm."
  [model sample-count]
  (let [targets (gpm/variables model)]
    (repeatedly sample-count #(gpm/simulate model targets {}))))

(def observed-samples (map #(assoc % :collection "observed") rows))
(def virtual-samples (->> (mapcat sample-xcat xcat-models num-points-required)
                          (map #(assoc % :collection "virtual"))))

(def all-samples (loop [ret [] os observed-samples
                        vs virtual-samples nps num-points-required
                        iters (range)]
                   (if-not (seq nps)
                     ret
                     (let [i (first iters)
                           np (first nps)
                           samples (concat (take np os) (take np vs))
                           samples (map #(assoc % :iter i) samples)]
                       (recur (concat ret samples)
                              (drop np os)
                              (drop np vs)
                              (rest nps)
                              (rest iters))))))

(defn app
  []
  (let [iteration @(rf/subscribe [:learning/iteration])
        cols @(rf/subscribe [:learning/col-selection])
        plot-type @(rf/subscribe [:learning/plot-type])
        marginal-types @(rf/subscribe [:learning/marginal-types])

        cgpm-model (nth cgpm-models iteration)
        mmix-model (nth mmix-models iteration)
        all-columns (keys schema)

        js-model-text (render (:js-model-template config)
                              (multimix/template-data mmix-model))

        node-names (map keyword (get cgpm-model "col_names"))
        view-assignment (fn [col]
                          (let [col-to-num (zipmap node-names (get cgpm-model "outputs"))
                                col-num-to-view-num (into {} (get cgpm-model "Zv"))]
                            (-> col col-to-num col-num-to-view-num)))
        views (vals (group-by view-assignment node-names))
        edges (mapcat #(combinations % 2) views)
        circle-spec (circle-viz-spec node-names edges)

        qc-spec (dashboard/spec all-samples schema nil cols 10 marginal-types)
        num-points (nth num-points-at-iter iteration)
        table-width 800]
    [v-box
     :margin "20px"
     :children [[h-box
                 :children [[learning/panel all-columns]
                            [gap :size "60px"]
                            [handsontable (take num-points rows)
                             {:height "500px"
                              :width (str table-width "px")
                              :cols (map name cols)}]]]
                [gap :size "30px"]
                [:div {:id "controls" :style {:display "none"}}]
                [h-box
                 :children [[js-code-block js-model-text]
                            [gap :size "20px"]
                            [box :style {:display (if (= plot-type :mutual-information)
                                                    "block"
                                                    "none")}
                             :child [vega-lite circle-spec {:actions false :mode "vega"} nil nil nil nil]]
                            [box :style {:display (if (= plot-type :select-vs-simulate)
                                                    "block"
                                                    "none")}
                             :child [vega-lite qc-spec {:actions false} nil nil all-samples {:iter iteration}]]]]]]))

