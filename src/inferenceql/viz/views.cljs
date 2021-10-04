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

(def col-ordering
  (reduce (fn [ordering xcat]
            (let [new-columns (clojure.set/difference (set (columns-in-model xcat))
                                                      (set ordering))]
              (concat ordering new-columns)))
          []
          xcat-models))

(def mutual-info-bounds
  (if (seq mutual-info)
    (let [mi-vals (flatten
                   (for [mi-crosscat-sample mutual-info]
                     (for [mi-model-iter mi-crosscat-sample]
                       (for [[_col-1 inner-map] (:mi mi-model-iter)]
                         (for [[_col-2 mi-val] inner-map]
                           mi-val)))))]
      {:min (apply min mi-vals)
       :max (apply max mi-vals)})
    {:min 0
     :max 1}))

(defn mi-plot
  "Reagent component for circle viz for mutual info."
  [mi-data iteration]
  (when mi-data
    (let [mi-threshold @(rf/subscribe [:learning/mi-threshold])
          mi-data (-> mi-data (nth iteration) :mi)
          nodes (-> (set (keys mi-data))
                    ;; Get nodes in consistent order by picking from col-ordering.
                    (keep col-ordering))
          edges (filter (fn [[col-1 col-2]]
                          (>= (get-in mi-data [col-1 col-2])
                              mi-threshold))
                        ;; All potential edges
                        (combinations nodes 2))
          circle-spec (circle-viz-spec nodes edges)]
      ;; TODO: make this faster by passing in nodes and edges as datasets.
      [vega-lite circle-spec {:actions false :mode "vega"} nil nil nil nil])))

(defn js-model
  "Reagent component for js-model."
  [iteration cluster-selected]
  (let [mmix-model (nth mmix-models iteration)
        js-model-text (render (:js-model-template config)
                              (multimix/template-data mmix-model))]
    [js-code-block js-model-text cluster-selected]))

(defn select-vs-simulate-plot
  "Reagent component for select-vs-simulate plot."
  [cluster-selected iteration]
  (let [viz-cols @(rf/subscribe [:learning/col-selection])
        marginal-types @(rf/subscribe [:learning/marginal-types])

        xcat-model (nth xcat-models iteration)
        ;; Merge in the view-cluster information only when we have to.
        all-samples (if cluster-selected
                      (let [view-cluster-assignments (concat (all-row-assignments xcat-model)
                                                             (repeat {}))]
                        (concat (map merge observed-samples view-cluster-assignments)
                                virtual-samples))
                      all-samples)
        qc-spec (dashboard/spec all-samples schema nil viz-cols 10 marginal-types)
        cols-in-view (set (columns-in-view xcat-model (:view-id cluster-selected)))]
    [vega-lite qc-spec {:actions false} nil nil all-samples
     {:iter iteration
      :cluster (:cluster-id cluster-selected)
      :view_columns (clj->js (map name cols-in-view))
      :view (some->> (:view-id cluster-selected) (str "view_"))}]))

(defn data-table
  "Reagent component for data table."
  [rows iteration cluster-selected]
  (let [xcat-model (nth xcat-models iteration)
        num-points (nth num-points-at-iter iteration)
        modeled-cols (-> (set (columns-in-model xcat-model))
                         ;; Get modeled columns in the correct order by picking items in order
                         ;; from col-ordering.
                         (keep col-ordering))]

    [handsontable (take num-points rows)
     {:height "400px"
      :width (str 1390 "px")
      :cols (map name modeled-cols)
      :cells (cells-fn xcat-model cluster-selected)}]))

(defn app
  []
  (let [iteration @(rf/subscribe [:learning/iteration])
        plot-type @(rf/subscribe [:learning/plot-type])
        cluster-selected @(rf/subscribe [:learning/cluster-selected])
        all-columns (keep (set (keys schema)) col-ordering)]
    [v-box
     :children [[learning/panel all-columns (:min mutual-info-bounds) (:max mutual-info-bounds)]
                [v-box
                 :margin "20px"
                 :children [[data-table rows iteration cluster-selected]
                            [gap :size "30px"]
                            ;; TODO: get rid of params that bind to controls in spec.
                            [:div {:id "controls" :style {:display "none"}}]
                            [h-box
                             :children [[js-model iteration cluster-selected]
                                        [gap :size "20px"]
                                        (case plot-type
                                          :mutual-information
                                          [v-box
                                           ;; Create a mi-plot for mi-info from each CrossCat sample.
                                           :children (for [mi mutual-info]
                                                        [mi-plot mi iteration])]

                                          :select-vs-simulate
                                          [select-vs-simulate-plot
                                           cluster-selected iteration])]]]]]]))


