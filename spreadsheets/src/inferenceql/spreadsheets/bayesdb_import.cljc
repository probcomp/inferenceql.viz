(ns inferenceql.spreadsheets.bayesdb-import
  (:require [inferenceql.spreadsheets.csv :as csv-utils]
            [medley.core :as medley]
            [clojure.walk :as walk]
            [inferenceql.inference.gpm.crosscat :as crosscat]
            [inferenceql.inference.gpm :as gpm]
            [clojure.pprint :as pprint]))

(defn infql-type
  "Converts from the column types in a BayesDB export and the types in an InferenceQL model.edn"
  [stattype]
  (case stattype
    "nominal"   :categorical
    "numerical" :gaussian))

(defn column-types
  "Returns a map of column names to InferenceQL stat types given a BayesDB export, `bdb-models`"
  [bdb-models]
  (->> (get bdb-models :column-statistical-types)
       (medley/map-vals #(infql-type %))))

(defn latents [cluster-alpha clusters]
  (let [counts (map count clusters)
        counts-map (zipmap (repeatedly gensym)
                           counts)

        cluster-names (keys counts-map)
        y (->> (for [[cn row-ids] (map vector cluster-names clusters)]
                 (zipmap row-ids (repeat cn)))
               (apply merge))]
    {:alpha cluster-alpha
     :counts counts-map
     :y y}))

(defn xcat-model
  [data stat-types categories model]
  (let [{column-partitions :column-partition
         clusters-by-view :clusters
         cluster-hypers :cluster-crp-hyperparameters
         column-hypers :column-hypers} model

         vs-info (for [[columns clusters cluster-alpha] (map vector column-partitions clusters-by-view cluster-hypers)]
                   {:view-spec {:hypers (select-keys column-hypers columns)}
                    :latents (latents cluster-alpha clusters)
                    :data (medley/map-vals #(select-keys % columns) data)})

         #_view-objs #_(for [v vs-info]
                         (gpm/dpmm (:view-spec v) (:latents v) stat-types data {:options categories :crosscat true}))

         view-names (repeatedly (count vs-info) gensym)
         view-specs (zipmap view-names (map :view-spec vs-info))
         local-latents (zipmap view-names (map :latents vs-info))

         spec {:views view-specs
               :types stat-types}
         latents {:global {:alpha 0.6}
                  :local local-latents}]
    (crosscat/construct-xcat-from-latents spec latents data {:options categories})))

(defn keywordize-column-partition-names [models]
  (let [keywordize #(if (string? %) (keyword %) %)]
    (for [model models]
      (update model :column-partition #(walk/postwalk keywordize %)))))

(defn xcat-gpms
  [bdb-export data]
  (let [bdb-models (-> bdb-export
                       (walk/keywordize-keys)
                       (update :models keywordize-column-partition-names))
        data (into {} (map-indexed vector data)) ;; GPM: data

        stat-types (column-types bdb-models) ;; GPM: types
        categories  (get bdb-models :categories) ;; GPM: options
        json-models (get bdb-models :models)]
    (map #(xcat-model data stat-types categories %) json-models)))

(defn xcat
  "`bdb-export` is a clojure map"
  [bdb-export rows]
  (first (xcat-gpms bdb-export rows)))

;; ------------------------------------

;; Testing code

(comment
  (require '[clojure.data.json :as json])
  (require '[clojure.data.csv :as csv])

  (def bdb-export (-> "/Users/harishtella/Desktop/nyc covid files/data_models.json"
                      (slurp)
                      (json/read-str)
                      (walk/keywordize-keys)))

  (def data (let [cols (column-types bdb-export)
                  raw-str (csv/read-csv (slurp "/Users/harishtella/Desktop/nyc covid files/data.csv"))]
              (csv-utils/csv-data->clean-maps cols raw-str {:keywordize-cols true})))

  (def my-gpm (xcat bdb-export data))
  (def cols (set (keys (get-in my-gpm [:latents :z]))))
  (gpm/simulate my-gpm cols {}))




