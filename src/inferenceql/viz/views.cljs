(ns inferenceql.viz.views
  (:require [re-frame.core :as rf]
            [re-com.core :refer [v-box h-box gap]]
            [inferenceql.viz.config :refer [config]]
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
            [inferenceql.viz.components.store.db :as store-db]))

(def rows (->> store-db/compiled-in-dataset
               (map #(medley/remove-vals nil? %))))
               ;;(map #(dissoc % :household_size))))

(def schema (dissoc (:schema config) :household_size))

;; TODO: Off load all this stuff into DVC stages.
(def cgpm-models (:transitions config))
(def xcat-models (map (fn [cgpm]
                        (let [num-rows (count (get cgpm "X"))]
                          ;; TODO: better to use the schema in the db.
                          (import-cgpm cgpm (take num-rows rows) (:mapping-table config) schema)))
                      cgpm-models))
(def mmix-models (map crosscat/xcat->mmix xcat-models))


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

(defn samples-for-iteration [i]
  (let [n (nth num-points-at-iter i)]
    (concat (take n observed-samples)
            (take n virtual-samples))))

(defn app
  []
  (let [iteration @(rf/subscribe [:learning/iteration])

        cgpm-model (nth cgpm-models iteration)
        mmix-model (nth mmix-models iteration)

        js-model-text (render (:js-model-template config)
                              (multimix/template-data mmix-model))

        node-names (map keyword (get cgpm-model "names"))
        view-assignment (fn [col]
                          (let [col-to-num (zipmap node-names (get cgpm-model "outputs"))
                                col-num-to-view-num (into {} (get cgpm-model "Zv"))]
                            (-> col col-to-num col-num-to-view-num)))
        views (vals (group-by view-assignment node-names))
        edges (mapcat #(combinations % 2) views)
        circle-spec (circle-viz-spec node-names edges)

        samples (samples-for-iteration iteration)
        qc-spec (dashboard/spec samples schema nil nil 10)]
    [v-box
     :margin "20px"
     :children [[learning/panel]
                [gap :size "30px"]
                [:div {:id "controls" :style {:display "none"}}]
                [vega-lite qc-spec {:actions false} nil nil]
                [h-box
                 :children [[js-code-block js-model-text]
                            [gap :size "20px"]
                            [vega-lite circle-spec {:actions false :mode "vega"} nil nil]]]]]))
