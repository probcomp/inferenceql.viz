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
            [inferenceql.auto-modeling.xcat :as xcat]
            [inferenceql.viz.panels.jsmodel.views :refer [js-code-block]]
            [inferenceql.viz.js.components.plot.views :refer [vega-lite]]
            [inferenceql.viz.panels.viz.circle :refer [circle-viz-spec]]
            [clojure.math.combinatorics :refer [combinations]]
            [inferenceql.inference.gpm :as gpm]
            [inferenceql.auto-modeling.qc.vega.dashboard :as dashboard]
            [inferenceql.viz.components.store.db :as store-db]
            [inferenceql.viz.js.components.table.views :refer [handsontable]]
            [clojure.walk :as walk]))

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

(def iteration-tags (mapcat (fn [iter count]
                              (repeat count {:iter iter}))
                            (range)
                            num-points-required))
(def observed-samples (->> (map #(assoc % :collection "observed") rows)
                           (map merge iteration-tags)))
(def virtual-samples (->> (mapcat sample-xcat xcat-models num-points-required)
                          (map #(assoc % :collection "virtual"))
                          (map merge iteration-tags)))
(def all-samples (concat observed-samples virtual-samples))

(defn columns-in-view [cgpm view-id]
  (when view-id
    (let [cgpm (-> cgpm walk/keywordize-keys xcat/fix-cgpm-maps)
          view-id (dec view-id)

          cols (mapv keyword (:col_names cgpm))
          view-assignments (zipmap (map #(nth cols %) (keys (:Zv cgpm)))
                                   (vals (:Zv cgpm)))
          ;_ (.log js/console :original-view-assignments view-assignments)
          view-assignments-new (zipmap (sort (distinct (vals view-assignments)))
                                       (range))
          view-assignments (medley/map-vals view-assignments-new view-assignments)]
          ;_ (.log js/console :new-view-assignments view-assignments)]

      ;(.log js/console :view-assignments view-assignments)
      ;(.log js/console :view-assignments-new view-assignments-new)
      (keep (fn [[col vid]]
              (when (= vid view-id) col))
            view-assignments))))

(defn rows-in-view-cluster [cgpm view-id cluster-id]
  (let [cgpm (-> cgpm walk/keywordize-keys xcat/fix-cgpm-maps)
        view-id (dec view-id)
        view-assignments (zipmap (map keyword (:col_names cgpm))
                                 (vals (:Zv cgpm)))
        view-reassignments (zipmap (range) (sort (distinct (vals view-assignments))))

        cluster-id (dec cluster-id)
        cluster-assignments (map vector (range) (get-in cgpm [:Zrv (view-reassignments view-id)]))

        cluster-reassignments (zipmap (sort (distinct (map second cluster-assignments)))
                                      (range))]

    ;(.log js/console :clusters (map second cluster-assignments))
    ;(.log js/console :clusters-distinct (sort (distinct (map second cluster-assignments))))
    (keep (fn [[row-num cid]]
            (when (= (cluster-reassignments cid) cluster-id) row-num))
          cluster-assignments)))

(defn all-row-assignments [cgpm]
  (let [cgpm (-> cgpm walk/keywordize-keys xcat/fix-cgpm-maps)
        range-1 (drop 1 (range))

        view-assignments (zipmap (map keyword (:col_names cgpm))
                                 (vals (:Zv cgpm)))
        view-reassignments (zipmap (sort (distinct (vals view-assignments)))
                                   range-1)

        cluster-reassign (fn [cluster-vals]
                            (let [reassignments (zipmap (sort (distinct cluster-vals))
                                                        range-1)]
                              (map reassignments cluster-vals)))

        cluster-assignments (->> (:Zrv cgpm)
                                 (medley/map-keys view-reassignments)
                                 (medley/map-vals cluster-reassign))

        view-names (map #(keyword (str "view_" %)) (keys cluster-assignments))]
    (apply map (fn [& a] (zipmap view-names a)) (vals cluster-assignments))))


(defn cells-fn [cgpm-model cluster-selected]
  (if-not cluster-selected
    (fn [_ _ _] #js {})
    (let [cols-set (set (columns-in-view cgpm-model (:view-id cluster-selected)))
          rows-set (set (rows-in-view-cluster cgpm-model
                                              (:view-id cluster-selected)
                                              (:cluster-id cluster-selected)))]
      (fn [row _col prop]
        (if (and (rows-set row)
                 (cols-set (keyword prop)))
          #js {:className "blue-highlight"}
          #js {})))))

(defn app
  []
  (let [iteration @(rf/subscribe [:learning/iteration])
        cols @(rf/subscribe [:learning/col-selection])
        plot-type @(rf/subscribe [:learning/plot-type])
        marginal-types @(rf/subscribe [:learning/marginal-types])
        cluster-selected @(rf/subscribe [:learning/cluster-selected])

        cgpm-model (nth cgpm-models iteration)
        mmix-model (nth mmix-models iteration)
        all-columns (keys schema)

        columns-in-view (set (columns-in-view cgpm-model (:view-id cluster-selected)))

        ;_ (.log js/console :col columns-in-view)
        ;_ (.log js/console :rows rows-in-view-cluster)

        ;_ (.log js/console :cgpm cgpm-model)
        ;_ (.log js/console :xcat (nth xcat-models iteration))
        ;_ (.log js/console :mmix mmix-model)

        js-model-text (render (:js-model-template config)
                              (multimix/template-data mmix-model))

        cols-incorporated (map keyword (get cgpm-model "col_names"))
        node-names cols-incorporated
        view-assignment (fn [col]
                          (let [col-to-num (zipmap node-names (get cgpm-model "outputs"))
                                col-num-to-view-num (into {} (get cgpm-model "Zv"))]
                            (-> col col-to-num col-num-to-view-num)))
        views (vals (group-by view-assignment node-names))
        edges (mapcat #(combinations % 2) views)
        circle-spec (circle-viz-spec node-names edges)

        ;; Merge in the view-cluster information only when we have to.
        all-samples (if cluster-selected
                      (let [view-cluster-assignments (all-row-assignments cgpm-model)]
                        (concat (map merge observed-samples view-cluster-assignments)
                                virtual-samples))
                      all-samples)

        qc-spec (dashboard/spec all-samples schema nil cols 10 marginal-types)
        num-points (nth num-points-at-iter iteration)
        table-width 700]
    [v-box
     :margin "20px"
     :children [[h-box
                 :children [[handsontable (take num-points rows)
                             {:height "500px"
                              :width (str table-width "px")
                              :cols (map name cols-incorporated)
                              :cells (cells-fn cgpm-model cluster-selected)}]
                            [gap :size "60px"]
                            [learning/panel all-columns]]]
                [gap :size "30px"]
                [:div {:id "controls" :style {:display "none"}}]
                [h-box
                 :children [[js-code-block js-model-text cluster-selected]
                            [gap :size "20px"]
                            [box :style {:display (if (= plot-type :mutual-information)
                                                    "block"
                                                    "none")}
                             :child [vega-lite circle-spec {:actions false :mode "vega"} nil nil nil nil]]
                            [box :style {:display (if (= plot-type :select-vs-simulate)
                                                    "block"
                                                    "none")}
                             :child [vega-lite qc-spec {:actions false} nil nil all-samples {:iter iteration
                                                                                             :cluster (:cluster-id cluster-selected)
                                                                                             :view_columns (clj->js (map name columns-in-view))
                                                                                             :view (some->> (:view-id cluster-selected) (str "view_"))}]]]]]]))


