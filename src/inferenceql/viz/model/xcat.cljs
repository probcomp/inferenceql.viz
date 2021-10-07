(ns inferenceql.viz.model.xcat
  "Defs for importing an XCat record from a CGPM json."
  (:refer-clojure :exclude [import])
  (:require [clojure.edn :as edn]
            [clojure.pprint :refer [pprint]]
            [medley.core :as medley]
            [inferenceql.inference.gpm.crosscat :as crosscat]
            [clojure.walk :refer [keywordize-keys]]))

(defn ^:private view-name
  "Returns a cluster name for view index n."
  [n]
  (keyword (str "view_" n)))

(defn ^:private cluster-name
  "Returns a cluster name for cluster index n."
  [n]
  (keyword (str "cluster_" n)))

(defn ^:private map-invert
  "Returns m with its vals as keys and its keys grouped into a vector as vals."
  [m]
  (->> (group-by second m)
       (medley/map-vals #(mapv key %))))

(defn fix-cgpm-maps
  "Converts lists of pairs in CGPM model model to maps."
  [cgpm]
  (let [->map #(into {} %)]
    (-> cgpm
        (update :view_alphas ->map)
        (update :Zv ->map)
        (update :Zrv ->map))))

(defn ^:private views
  [columns {:keys [hypers Zv]}]
  (let [column->hypers (zipmap columns hypers)]
    (->> Zv
         (medley/map-keys columns)
         (medley/map-vals view-name)
         (map-invert)
         (medley/map-vals (fn [columns]
                            {:hypers (zipmap columns
                                             (map column->hypers columns))})))))

(defn ^:private col-names
  [numericalized cgpm-model]
  (mapv keyword (get cgpm-model :col_names (first numericalized))))

(defn spec
  [numericalized schema cgpm-model]
  (let [columns (col-names numericalized cgpm-model)
        views (views columns cgpm-model)
        types (->> schema
                   (medley/map-keys keyword)
                   (medley/map-vals (comp {:nominal :categorical
                                           :numerical :gaussian}
                                          keyword)))]
    {:views views
     :types types}))

(defn latents
  [{:keys [alpha Zrv] view-alphas :view_alphas}]
  (let [local (merge-with merge
                          (->> view-alphas
                               (medley/map-keys view-name)
                               (medley/map-vals (fn [alpha] {:alpha alpha})))
                          (->> Zrv
                               (medley/map-keys view-name)
                               (medley/map-vals (fn [clusters]
                                                  (let [y (zipmap (range)
                                                                  (map cluster-name clusters))
                                                        counts (->> y
                                                                    (group-by second)
                                                                    (medley/map-vals count))]
                                                    {:counts counts
                                                     :y y})))))]
    {:global {:alpha alpha}
     :local local}))

(defn options
  [mapping-table]
  (->> mapping-table
       (medley/map-keys keyword)
       (medley/map-vals #(->> % (sort-by val) (map key) (into [])))))

(defn import
  [cgpm-json data mapping-table schema]
  (let [schema (js->clj schema) ; KVs do not have to be keywords.
        mapping-table (js->clj mapping-table) ; Ks do not have to be keywords.
        ;; Ks do not have to be keywords.
        cgpm-model (-> cgpm-json js->clj keywordize-keys fix-cgpm-maps)

        ;; Expects a collection of maps with values appropriately cast.
        data (->> (js->clj data)
               (map #(medley/map-keys keyword %))
               (zipmap (range)))

        ;; Dummy version of numericalized. This should not ever get used because
        ;; model-model contains our column names.
        numericalized [[]]
        spec (spec numericalized schema cgpm-model)
        latents (latents cgpm-model)
        options (options mapping-table)]
    (crosscat/construct-xcat-from-latents spec latents data {:options options})))
