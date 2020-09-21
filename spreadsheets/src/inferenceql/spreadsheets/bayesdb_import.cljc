(ns inferenceql.spreadsheets.bayesdb-import
  (:require [inferenceql.spreadsheets.csv :as csv-utils]
            [medley.core :as medley]
            [clojure.walk :as walk]
            [inferenceql.inference.gpm.crosscat :as gpm.crosscat]
            [inferenceql.inference.gpm.view :as gpm.view]
            [inferenceql.inference.gpm.column :as gpm.column]
            [inferenceql.inference.gpm.primitive-gpms.categorical :as gpm.categorical]
            [inferenceql.inference.gpm.primitive-gpms.gaussian :as gpm.gaussian]
            [inferenceql.inference.gpm.primitive-gpms.bernoulli :as gpm.bernoulli]
            [inferenceql.inference.gpm :as gpm]
            [clojure.pprint :as pprint]))

(defn stat-types
  "Returns a map of column-name to InferenceQL datatype.
   Args:
     `bdb-models`: a BayesDB export`"
  [bdb-models]
  (let [iql-type (fn [bdb-type]
                   (case bdb-type
                     "nominal"   :categorical
                     "numerical" :gaussian))
        bdb-types (get bdb-models :column-statistical-types)]
     (medley/map-vals iql-type bdb-types)))

(defn view-latents
  "Returns the latents map required by inferenceql.inference for constructing a view dpmm.
  Args:
    `cluster-alpha` - The alpha parameter for clustering in this view.
    `clusters` - A vector of vectors containing row-ids. Each of the nested vectors represents a
      cluster."
  [cluster-alpha clusters]
  (let [counts (map count clusters)
        counts-map (zipmap (repeatedly gensym) counts)
        cluster-ids (keys counts-map)

        ;; Mapping of row-id to cluster-id
        y (->> (for [[cid row-ids] (map vector cluster-ids clusters)]
                 (zipmap row-ids (repeat cid)))
            (apply merge))]
    {:alpha cluster-alpha
     :counts counts-map
     :y y}))

(defn xcat-model
  "Returns a XCat record given a model from a BayesDB export.
  Args:
    `data` - A vector of maps representing the data the model was trained on.
    `stat-types` - A map of column name to InferenceQL datatype
    `categories` - A map of categorical column name to category options.
    `model` - A map representing a single model from a BayesDB export."
  [data stat-types categories model]
  (let [{column-partition :column-partition
         clusters-per-view :clusters
         cluster-alphas :cluster-crp-hyperparameters
         column-hypers :column-hypers} model

         views (for [[columns clusters cluster-alpha] (map vector column-partition clusters-per-view cluster-alphas)]
                 {:spec {:hypers (select-keys column-hypers columns)}
                  :latents (view-latents cluster-alpha clusters)})

         view-names (repeatedly gensym)
         view-specs (zipmap view-names (map :spec views))
         xcat-spec {:views view-specs
                    :types stat-types}

         view-latents (zipmap view-names (map :latents views))
         ;; Global alpha not included in BayseDB export. Using a default value instead.
         xcat-latents {:global {:alpha 0.5}
                       :local view-latents}]
    (gpm.crosscat/construct-xcat-from-latents xcat-spec xcat-latents data {:options categories})))

(defn keywordize-bdb-export
  "Returns a bdb-export with all keys and column name references keywordized.
  Args:
    `bdb-export` - A map representing a BaysedDB export."
  [bdb-export]
  (let [keywordize (fn [column-partition]
                     (walk/postwalk #(if (string? %) (keyword %) %) column-partition))
        keywordize-column-partitions (fn [models] (for [model models]
                                                    (update model :column-partition keywordize)))]
    (-> bdb-export
        (walk/keywordize-keys)
        ;; We keywordize the nested vectors of column names present
        ;; at the paths [:models model-num :column-partition]
        (update :models keywordize-column-partitions))))

(defn xcat-gpms
  "Returns a sequence of XCat records given a BayesDB export.
  A BayesDB export might contain multiple models. Therefore, we return a sequence of XCat records.

  Args:
    `bdb-export` - A map representing the BayesDB export. The columns names referenced by the export
      need not reference keywordized column names.
    `rows` - A vector of maps representing the data the BayesDB export was trained on. Keys
      in `rows` should be keywordized. And values in `rows` should be cast according to the
      datasets' schema.
  Returns:
    A sequence of XCat records. These records will reference keywordized column names even if the
      original `bdb-export` did not."
  [bdb-export data]
  (let [bdb-export (keywordize-bdb-export bdb-export)
        data (into {} (map-indexed vector data)) ; Map of row-id to row;
        stat-types (stat-types bdb-export)
        categories  (get bdb-export :categories)]
    (for [model (get bdb-export :models)]
      (xcat-model data stat-types categories model))))

(defn xcat
  [bdb-export rows]
  (first (xcat-gpms bdb-export rows)))

(defn xcat-from-file
  "`data` is raw file data"
  [file-data]
  (clojure.edn/read-string {:readers {'inferenceql.inference.gpm.crosscat.XCat gpm.crosscat/map->XCat
                                      'inferenceql.inference.gpm.view.View gpm.view/map->View
                                      'inferenceql.inference.gpm.column.Column gpm.column/map->Column
                                      'inferenceql.inference.gpm.primitive_gpms.categorical.Categorical gpm.categorical/map->Categorical
                                      'inferenceql.inference.gpm.primitive_gpms.gaussian.Gaussian gpm.gaussian/map->Gaussian
                                      'inferenceql.inference.gpm.primitive_gpms.bernoulli.Bernoulli gpm.bernoulli/map->Bernoulli}}
                           file-data))

(defn xcat-spec-from-file
  "`data` is raw file data"
  [file-data]
  (clojure.edn/read-string {:default (fn [tag value] [tag value])}
                           file-data))
;; ------------------------------------

;; Testing simple model as edn

(comment
  (require '[clojure.data.json :as json])
  (require '[clojure.data.csv :as csv])

  (def bdb-export (-> "/Users/harishtella/Desktop/toy dataset/data_models.json"
                      (slurp)
                      (json/read-str)
                      (walk/keywordize-keys)))

  (def data (let [cols (stat-types bdb-export)
                  raw-str (csv/read-csv (slurp "/Users/harishtella/Desktop/toy dataset/data.csv"))]
              (csv-utils/csv-data->clean-maps cols raw-str {:keywordize-cols true})))

  (def my-gpm (xcat bdb-export data))
  (def cols (set (keys (get-in my-gpm [:latents :z]))))
  (gpm/simulate my-gpm cols {})
  (spit "/Users/harishtella/Desktop/model.xcat.edn" (gpm.crosscat/xcat->mmix my-gpm)))


;; Generating data from model-0 gives an error.
;; model-1 and  model-2 seem fine though.

(comment
  ;; Original folder.
  (def beat19-xcat-model (xcat-from-file
                          (slurp "/Users/harishtella/Repos/probcomp/clojurecat-models/models/beat19/xcat/model-1.edn")))
  (:latents beat19-xcat-model)
  (def beat19-cols (set (keys (get-in beat19-xcat-model [:latents :z]))))
  (gpm/simulate beat19-xcat-model beat19-cols {}))

;;(.printStackTrace *e)

;; Testing code

(comment
  (require '[clojure.data.json :as json])
  (require '[clojure.data.csv :as csv])

  (def bdb-export (-> "/Users/harishtella/Desktop/nyc covid files/data_models.json"
                      (slurp)
                      (json/read-str)
                      (walk/keywordize-keys)))

  (def data (let [cols (stat-types bdb-export)
                  raw-str (csv/read-csv (slurp "/Users/harishtella/Desktop/nyc covid files/data.csv"))]
              (csv-utils/csv-data->clean-maps cols raw-str {:keywordize-cols true})))

  (def my-gpm (xcat bdb-export data))
  (def cols (set (keys (get-in my-gpm [:latents :z]))))
  (gpm/simulate my-gpm cols {}))





