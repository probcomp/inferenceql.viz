(ns inferenceql.viz.views
  (:require [re-frame.core :as rf]
            [re-com.core :refer [v-box h-box gap box]]
            [inferenceql.viz.config :refer [config transitions mutual-info]]
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
            [clojure.walk :as walk]
            [clojure.edn :as edn]))

;; Util
(def range-1 (drop 1 (range)))

(def rows (->> store-db/compiled-in-dataset
               (map #(medley/remove-vals nil? %))))

(def schema store-db/compiled-in-schema)

(def cgpm-models transitions)
;; TODO: Off load the conversion into xcat into DVC stage.
(def xcat-models (map (fn [cgpm]
                        (let [num-rows (count (get cgpm "X"))]
                          (import-cgpm cgpm (take num-rows rows) (:mapping-table config) schema)))
                      cgpm-models))
(def mmix-models (doall (map crosscat/xcat->mmix xcat-models)))


(def num-points-at-iter (map (fn [xcat]
                               (let [[view-1-name view-1] (first (get xcat :views))]
                                 ;; Count the number of row to cluster assignments.
                                 (count (get-in view-1 [:latents :y]))))
                             xcat-models))
(def num-points-required (map - num-points-at-iter (conj num-points-at-iter 0)))

(defn sample-xcat
  "Samples all targets from an XCat gpm."
  [model sample-count]
  (let [targets (gpm/variables model)]
    (repeatedly sample-count #(gpm/simulate model targets {}))))

(defn add-null-columns [row]
  (let [columns (keys schema)
        null-kvs (zipmap columns (repeat nil))]
    (merge null-kvs row)))

(def iteration-tags (mapcat (fn [iter count]
                              (repeat count {:iter iter}))
                            (range)
                            num-points-required))

(def observed-samples (->> (map #(assoc % :collection "observed") rows)
                           (map add-null-columns)
                           (map merge iteration-tags)))

(def virtual-samples (->> (mapcat sample-xcat xcat-models num-points-required)
                          (map #(assoc % :collection "virtual"))
                          (map add-null-columns)
                          (map merge iteration-tags)))
(def all-samples (concat observed-samples virtual-samples))


(defn xcat-view-id-map
  "Returns map from js-program view-id (int) to xcat view-id (keyword)."
  [xcat]
  (let [view-names (keys (get-in xcat [:latents :counts]))
        view-number (fn [view-name]
                      (-> (re-matches #"view_(\d+)" (name view-name))
                          second
                          edn/read-string))]
    (zipmap range-1 (sort-by view-number view-names))))

(defn xcat-cluster-id-map
  "Returns map from js-program cluster-id (int) to xcat cluster-id (keyword).
  Cluster id is specific to xact view view-id (keyword)."
  [xcat view-name]
  (let [view (get-in xcat [:views view-name])
        cluster-names (keys (get-in view [:latents :counts]))
        cluster-number (fn [cluster-name]
                         (-> (re-matches #"cluster_(\d+)" (name cluster-name))
                             second
                             edn/read-string))]
    (zipmap range-1 (sort-by cluster-number cluster-names))))

(defn columns-in-view [xcat view-id]
  (when view-id
    (let [view-id (get (xcat-view-id-map xcat)
                       view-id)
          view (get-in xcat [:views view-id])]
      (keys (:columns view)))))

(defn columns-in-model [xcat]
  (let [views (-> xcat :views vals)
        columns-in-view (fn [view] (-> view :columns keys))]
    (mapcat columns-in-view views)))

(defn rows-in-view-cluster [xcat view-id cluster-id]
  (let [view-map (xcat-view-id-map xcat)
        ;; View-name-kw used in xcat model.
        view-id (view-map view-id)
        cluster-map (xcat-cluster-id-map xcat view-id)
        ;; Cluster-id used in xcat model.
        cluster-id (cluster-map cluster-id)

        view (get-in xcat [:views view-id])
        cluster-assignments (get-in view [:latents :y])]
   (->> (filter #(= cluster-id (val %)) cluster-assignments)
        (map first))))

(defn all-row-assignments [xcat]
  (let [view-map (xcat-view-id-map xcat)
        inv-view-map (zipmap (vals view-map)
                             (map #(keyword (str "view_" %)) (keys view-map)))

        view-cluster-assignemnts (->> (:views xcat)
                                      ;; Get the cluster assignments.
                                      (medley/map-vals #(get-in % [:latents :y]))
                                      ;; Sort the map of cluster assignments.
                                      (medley/map-vals #(sort-by first %))
                                      ;; Get just the cluster names. Drop row numbers.
                                      (medley/map-vals #(map second %))
                                      ;; Remap view-id and cluster-ids.
                                      (medley/map-kv (fn [view-name cluster-assignments]
                                                       (let [cluster-map (xcat-cluster-id-map xcat view-name)
                                                             inv-cluster-map (zipmap (vals cluster-map)
                                                                                     (keys cluster-map))]
                                                         [(inv-view-map view-name)
                                                          (map inv-cluster-map cluster-assignments)]))))]
    view-cluster-assignemnts
    ;; Expand the lists of cluster assigments into assignments for each row.
    (apply map (fn [& a] (zipmap (keys view-cluster-assignemnts) a))
           (vals view-cluster-assignemnts))))

(def default-cells-fn
  (fn [_ _ _] #js {}))

(defn cells-fn [xcat-model cluster-selected]
  (if-not cluster-selected
    default-cells-fn
    (let [cols-set (set (columns-in-view xcat-model (:view-id cluster-selected)))
          rows-set (set (rows-in-view-cluster xcat-model
                                              (:view-id cluster-selected)
                                              (:cluster-id cluster-selected)))]
      (fn [row _col prop]
        (if (and (rows-set row)
                 (cols-set (keyword prop)))
          #js {:className "blue-highlight"}
          #js {})))))

(def mi-raw-vals
  (flatten
   (for [mi-iter (map :mi mutual-info)]
     (for [[_ inner-vals] mi-iter]
       (for [[_ v1] inner-vals]
         v1)))))

(def mi-min (apply min mi-raw-vals))

(def mi-max (apply max mi-raw-vals))

(defn app
  []
  (let [iteration @(rf/subscribe [:learning/iteration])
        cols @(rf/subscribe [:learning/col-selection])
        plot-type @(rf/subscribe [:learning/plot-type])
        marginal-types @(rf/subscribe [:learning/marginal-types])
        cluster-selected @(rf/subscribe [:learning/cluster-selected])
        mi-threshold @(rf/subscribe [:learning/mi-threshold])

        cgpm-model (nth cgpm-models iteration)
        xcat-model (nth xcat-models iteration)
        mmix-model (nth mmix-models iteration)
        mi-vals (:mi (nth mutual-info iteration))

        all-columns (keys schema)

        columns-in-view (set (columns-in-view xcat-model (:view-id cluster-selected)))

        ;_ (.log js/console :col columns-in-view)
        ;_ (.log js/console :rows rows-in-view-cluster)

        ;_ (.log js/console :cgpm cgpm-model)
        ;_ (.log js/console :xcat xcat-model)
        ;_ (.log js/console :mmix mmix-model)
        ;_ (.log js/console :mutual-info mutual-info)

        js-model-text (render (:js-model-template config)
                              (multimix/template-data mmix-model))

        cols-incorporated (sort (columns-in-model xcat-model))
        edges (filter (fn [[col-1 col-2]]
                        (>= (get-in mi-vals [col-1 col-2])
                            mi-threshold))
                      ;; All potential edges
                      (combinations cols-incorporated 2))
        circle-spec (circle-viz-spec cols-incorporated edges)

        ;; Merge in the view-cluster information only when we have to.
        all-samples (if cluster-selected
                      (let [view-cluster-assignments (concat (all-row-assignments xcat-model)
                                                             (repeat {}))]
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
                              :cells (cells-fn xcat-model cluster-selected)}]
                            [gap :size "60px"]
                            [learning/panel all-columns mi-min mi-max]]]
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


