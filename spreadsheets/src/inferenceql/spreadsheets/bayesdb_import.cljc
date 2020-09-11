(ns inferenceql.spreadsheets.bayesdb-import
  (:require [inferenceql.spreadsheets.csv :as csv-utils]
            [inferenceql.inference.utils :as utils]
            [metaprob.distributions :as mpdist]
            [medley.core :as medley]))

(defn gamma-simulate
  "Generates a sample from a gamma distribution with shape parameter `k` and scale parameter `theta`.
  Based on Section 3 of 'Generating Gamma and Beta Random Variables with Non-Integral Shape Parameters'
  by J Whittaker, found at https://www.jstor.org/stable/pdf/2347003.pdf?seq=1 .
  Generates `n` samples, if specified."
  ([{:keys [k theta]}]
   (if (< k 1)
     (let [u1 (rand)
           u2 (rand)
           u3 (rand)
           s1 (Math/pow u1 k)
           s2 (Math/pow u2 (- 1 k))
           theta (if-not theta 1 theta)]
       (if (<= (+ s1 s2) 1)
         (let [y (/ s1
                    (+ s1 s2))]
           (* theta
              (- (- 1 y))  ; If just -y, then returns Gamma(1 - p) variable, contrary to literature.
              (Math/log u3)))
         (gamma-simulate {:k k :theta theta})))
     (let [theta         (if-not theta 1 theta)
           frac-k        (- k (int k))
           gamma-floor-k (- (reduce + (repeatedly
                                        (int k)
                                        #(Math/log (rand)))))
           gamma-frac-k  (if (zero? frac-k) 0 (gamma-simulate {:k frac-k}))]
       (* theta (+ gamma-floor-k gamma-frac-k)))))
  ([n parameters]
   (repeatedly n #(gamma-simulate parameters))))

(defn infql-type
  "Converts from the column types in a BayesDB export and the types in an InferenceQL model.edn"
  [stattype]
  (case stattype
    "nominal"   :categorical
    "numerical" :gaussian))

(defn column-types
  "Returns a map of column names to InferenceQL stat types given a BayesDB export, `bdb-models`"
  [bdb-models]
  (medley/map-vals #(infql-type %)
                   (get bdb-models "column-statistical-types")))

(defn cluster-probabilities
  "Returns a sequence of probabilities for each cluster.
  `cluster-assignments` is a sequence for row-ids vectors.
  `alpha` is a hyperparameter.
  NOTE: This returns the probability for the empty table as well."
  [cluster-assignemts alpha]
  (mpdist/normalize-numbers (concat (map count cluster-assignemts)
                                    [alpha])))

(defn col-subset
  "Returns a seq of data from a single column `col` and from rows, `rowids`."
  [data col rowids]
  (let [subset (map data rowids)]
    (map #(get % col) subset)))

(defn suff-stats-cat
  "Get sufficient statistics for a nominal data vector `col-vec`.
  `col-categories` is a sequence of possible values that can be seen in `col-vec`.
  Essentialy this function returns frequencies for values encountered in `col-vec`."
  [col-vec col-categories]
  (let [default-freqs (zipmap col-categories (repeat 0))]
    (merge default-freqs (frequencies col-vec))))

(defn cat-params
  "Returns parameters for a categorical variable.
  `counts` are the frequencies of values the variable takes on.
  `alpha` is a hyperparameter."
  [counts alpha]
  (let [denom (+ (reduce + (vals counts)) (* alpha (count counts)))]
    ;; XXX: should be in log space.
    (medley/map-vals (fn [v] (/ (+ v alpha) denom))
                     counts)))

(defn suff-stats-num
  "Get sufficient statistics for a numerical data vector, `col-vec-in`"
  [col-vec-in]
  (let [col-vec (filter some? col-vec-in)]
    (if (empty? col-vec)
      {:n 0 :sum_x 0 :sum_x_squared 0}
      {:n (count col-vec)
       :sum_x (reduce + col-vec)
       :sum_x_squared (reduce + (map #(utils/square %) col-vec))})))

(defn posterior-hypers
  "Conditions column hyper-parameters on data."
  [{n :n sum_x :sum_x sum_x_squared :sum_x_squared} ;; Sufficient stats.
   {m "m" r "r" s "s" nu "nu"}] ;; Column hyper-parameters from BayesDB export.
  (if (= n 0)
    {:m m :r r :s s :nu nu} ;; No data is observed, return the prior.
    (let [rn  (+ r n)
          nun (+ nu n)
          mn  (/ (+ (* r m) sum_x) rn)
          sn  (- (+ s sum_x_squared (* r m m )) (* rn mn mn))]
      {:m mn :r rn :s (if (= sn 0.) s sn) :nu nun})))

(defn nig-normal-sampler
  "Generates samples given column hyperparameters."
  [{m :m r :r s :s nu :nu}]
  (let [gamma #?(:cljs (fn [k theta] (gamma-simulate {:k k :theta theta}))
                 :clj mpdist/gamma)
        rho (gamma (/ nu 2.0) (/ 2.0 s))
        sigma (Math/pow (* rho r) -0.5)
        mu (mpdist/gaussian m (Math/pow (* rho r) -0.5))]
    (mpdist/gaussian mu (Math/pow rho -0.5))))

(defn num-params
  "Returns parameters for a numerical variable.
  `suff-stats` is the output of sufficient stats for a numerical variable.
  `hypers` are column-hyperparameters for a numerical variable. "
  [suff-stats hypers]
  (let [n-samples 1000 ;; Number of samples used to estimate parameters for components.
        sampler #(nig-normal-sampler (posterior-hypers suff-stats hypers))
        samples (repeatedly n-samples sampler)]
    {:mu (utils/average samples) :sigma (utils/std samples)}))

(defn params-for-cluster
  "Returns the parameters for a cluster made up of rows with `row-ids` and columns `col-in-view`.
  Returns a map where keys are column-names and vals are parameter maps."
  [json-model data row-ids cols-in-view stat-types categories]
  (let [param-maps (for [c cols-in-view]
                     (let [col-type (get stat-types c)
                           col-vec  (col-subset data c row-ids)]
                       (case col-type
                         :gaussian
                         (num-params (suff-stats-num col-vec)
                                     (get-in json-model ["column-hypers" c]))
                         :categorical
                         (cat-params (suff-stats-cat col-vec (categories c))
                                     (get-in json-model ["column-hypers" c "alpha"])))))]
    (zipmap cols-in-view param-maps)))

(defn view-spec-from-json
  "Returns a spec for a particular view in `json-model` indexed by `view-idx`."
  [json-model data view-idx stat-types categories]
  (let [cols-in-view (get-in json-model ["column-partition" view-idx])
        clustering (get-in json-model ["clusters" view-idx])
        cluster-hypers (get-in json-model ["cluster-crp-hyperparameters" view-idx])

        ;; Includes a probability for the empty table as well.
        p-clusters (cluster-probabilities clustering cluster-hypers)
        clustering+ (conj clustering [])] ;; Including the empty table.

    (vec (for [[p row-ids] (map vector p-clusters clustering+)]
           {:probability p
            :parameters (params-for-cluster json-model
                                            data
                                            row-ids
                                            cols-in-view
                                            stat-types
                                            categories)}))))

(defn generate-specs-from-json
  "Returns a sequence of model-specs.
  `bdb-models` is json from a BayesDB export.
  `data` is a sequence of data maps."
  [bdb-models data]
  (let [stat-types (column-types bdb-models)
        categories  (get bdb-models "categories")
        json-models (get bdb-models "models")

        ;; Generates a view-spec for each view (column partition) in the model.
        view-specs (fn [json-model]
                     (let [num-views (count (get json-model "column-partition"))]
                       (mapv #(view-spec-from-json json-model data % stat-types categories)
                             (range num-views))))]
    ;; For n models.
    (for [json-model json-models]
      {:vars stat-types
       :views (view-specs json-model)})))

(defn keywordize-columns
  "Keywordizes columns names in a multi-mixture spec, `spec`."
  [spec]
  (let [keywordize (fn [a-map] (medley/map-keys keyword a-map))] ; A non-recursive keywordize.
    (-> spec
        (update :vars keywordize)
        (update :views (fn [views]
                         (vec (for [column-partition views]
                                (vec (for [cluster column-partition]
                                       (update cluster :parameters keywordize))))))))))

(defn multimix-spec [bdb-models data]
  (let [rows (csv-utils/csv-data->clean-maps (column-types bdb-models) data)
        specs (generate-specs-from-json bdb-models rows)]
   (first (keywordize-columns specs))))