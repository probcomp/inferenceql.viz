(ns inferenceql.viz.model.xcat
  (:refer-clojure :exclude [import])
  (:require #?(:clj [cheshire.core :as json])
            #?(:clj [clojure.data.csv :as csv])
            #?(:clj [inferenceql.auto-modeling.csv :as am.csv])
            [clojure.edn :as edn]
            [clojure.pprint :refer [pprint]]
            [medley.core :as medley]
            [inferenceql.inference.gpm.crosscat :as crosscat]))

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

#?(:clj (defn ^:private data
          [data-cells schema]
          (let [headers (map keyword (first data-cells))
                column->f (comp {:numerical am.csv/parse-number
                                 :nominal am.csv/parse-str}
                                schema
                                name)]
            (zipmap (range)
                    (map #(-> (zipmap headers %)
                              (am.csv/update-by-key column->f))
                         (rest data-cells))))))

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

#?(:clj (defn import
          [{:keys [cgpm-json data-csv mapping-table numericalized-csv schema-edn]}]
          (let [schema        (-> schema-edn        (str) (slurp) (edn/read-string))
                mapping-table (-> mapping-table     (str) (slurp) (edn/read-string))
                csv-data      (-> data-csv          (str) (slurp) (csv/read-csv))
                numericalized (-> numericalized-csv (str) (slurp) (csv/read-csv))
                cgpm-model    (-> cgpm-json         (str) (slurp) (json/parse-string true) (fix-cgpm-maps))

                data (data csv-data schema)
                spec (spec numericalized schema cgpm-model)
                latents (latents cgpm-model)
                options (options mapping-table)
                model (crosscat/construct-xcat-from-latents spec latents data {:options options})]
            (prn model))))
