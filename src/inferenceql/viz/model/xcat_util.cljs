(ns inferenceql.viz.model.xcat-util
  (:require [medley.core :as medley]
            [clojure.edn :as edn]
            [inferenceql.inference.gpm :as gpm]))

(def range-1 (drop 1 (range)))

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

(defn sample-xcat
  "Samples all targets from an XCat gpm."
  [model sample-count]
  (let [targets (gpm/variables model)]
    (repeatedly sample-count #(gpm/simulate model targets {}))))
