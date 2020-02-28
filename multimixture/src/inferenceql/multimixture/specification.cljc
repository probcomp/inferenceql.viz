(ns inferenceql.multimixture.specification
  (:require [clojure.spec.alpha :as s]
            [clojure.java.io :as io]
            [clojure.data.csv :as csv]
            [inferenceql.spreadsheets.data :as data]
            [metaprob.distributions :as mpdist]))

(s/def ::alpha pos?)

(s/def ::beta pos?)

(s/def ::beta-parameters (s/keys :req-un [::alpha ::beta]))

(s/def ::mu number?)

(s/def ::sigma
  (s/and number? pos?))

(s/def ::gaussian-parameters
  (s/keys :req-un [::mu ::sigma]))

(defn normalized?
  [xs]
  (== 1 (apply + xs)))

(s/def ::probability #(<= 0 % 1))

(s/def ::probability-vector
  (s/and (s/+ ::probability)
         normalized?))

(s/def ::binary-paramters
  (s/and number?
         #(<= 0 % 1)))

(s/def ::categorical-parameters
  (s/map-of string? float?))

(s/def ::distribution-paremeters
  (s/or ::binary-parameters      ::binary-paramters
        ::categorical-parameters ::categorical-parameters
        ::gaussian-parameters    ::gaussian-parameters))

(s/def ::column string?)

(s/def ::row (s/map-of ::column any?))

(s/def ::rows (s/coll-of ::row))

(s/def ::parameters (s/map-of ::column ::distribution-paremeters))

(def distribution? #{:binary :gaussian :categorical})

(s/def ::distribution distribution?)

(s/def ::vars (s/and #(> (count %) 0)
                     (s/map-of ::column ::distribution)))

(s/def ::cluster (s/keys :req-un [::probability ::parameters]))

(s/def ::clusters (s/coll-of ::cluster))

(s/def ::view ::clusters)

(s/def ::views (s/coll-of ::view))

(s/def ::multi-mixture
  (s/keys :req-un [::vars ::views]))

(s/fdef from-json
  :args (s/cat :json (s/map-of string? any?))
  :ret ::multi-mixture)

;; Old from-json.
(defn from-json
  [{:strs [columns views]}]
  (let [vars (reduce-kv (fn [m k v]
                          (assoc m k (keyword v)))
                        {}
                        columns)
        views (mapv (fn [view]
                      (mapv (fn [cluster]
                              (let [column-parameters (dissoc cluster "p")]
                                {:probability (get cluster "p")
                                 :parameters (reduce-kv (fn [m column parameters]
                                                          (let [stattype (get vars column)]
                                                            (assoc m
                                                                   column
                                                                   (case stattype
                                                                     :gaussian (zipmap [:mu :sigma] parameters)
                                                                     :categorical parameters))))
                                                        {}
                                                        column-parameters)}))
                            view))
                    views)]
    {:vars vars
     :views views}))

#_(from-json (json/read-str (slurp "/home/ulli/git_repos/inferenceql/spreadsheets/resources/model.json")))

(require '[clojure.data.json :as json])

(defn raise-not-implemented-error
  []
  (let [msg "Not implemented yet"]
  (println msg)
  (ex-info msg {})))


#_(defn from-json2
  [models]
  (nth (get models "models") 0))

(def jms (json/read-str (slurp "/home/ulli/git_repos/inferenceql/multimixture/test/inferenceql/multimixture/test_models.json")))

(defn infql-type
  [stattype]
  (case
    "nominal"   :categorical
    "numerical" :gaussian))

(defn get-col-types
  [json-models]
  {:vars
   (reduce-kv
     (fn [m k v] (assoc m k (infql-type v)))
     {}
     (get json-models "column-statistical-types"))})

(defn generate-spec-from-json
  [json-model stattypes]
    0)


(def dp "/home/ulli/git_repos/inferenceql/multimixture/test/inferenceql/multimixture/synthetic-data.csv")
(def d (->> dp
     (slurp)
     (csv/read-csv)
     (mapv data/fix-row)
     (data/csv-data->maps)))



;; XXX: this has the probability for the empty table.
(defn cluster-probabilities
  [cluster-assignemts alpha]
    (mpdist/normalize-numbers (concat (map count
                                           cluster-assignemts)
                                      [alpha])))

(defn get-view-cluster-assignments
  [json-model view-idx]
  (nth (get json-model "clusters") view-idx))

(defn get-view-crp-params
  [json-model view-idx]
  (nth (get json-model "cluster-crp-hyperparameters") view-idx))

(defn col-subset
  [data col rowids]
    (map #(get % col)
         (map #(nth data %) rowids)))

;; XXX: I am SURE that that's not idiomatic... or efficient.. calling assoc
;; recursively to get null counts in...
;; XXX: number 2: can use this recursive definition within a let.
(defn rec-assoc
  [m cats]
  (if (empty? cats)
    m
    (if (contains? m (first cats))
      (rec-assoc  m                          (rest cats))
      (rec-assoc (assoc m (first cats) 0) (rest cats)))))
(defn get-cat-counts
  [col-vec col-cats]
  ; take a column vector, i.e. a subvec of a columm and return a count for
  ; categories.
  (rec-assoc (frequencies col-vec) col-cats))

(get-cat-counts ["a" "b" "a"] ["a" "b" "c" "d"])


(assoc {:a 2} :b 0)
(reduce #(assoc {} )


(def vector-of-maps [{:a 1 :b 2} {:a 3 :b 4}])

(defn update-map [m f]
  (reduce-kv (fn [m k v]
    (assoc m k (f v))) {} m))
(map #(update-map % inc) vector-of-maps)


(defn generate-view-from-json
  [json-model data view-idx stattypes categories]
  (let [clustering (get-view-cluster-assignments json-model view-idx)
        p-clusters (cluster-probabilities clustering (get-view-crp-params json-model view-idx))]
    (map (fn [p] {:probability p}) p-clusters)))

;; operating ing model assemble.
(defn generate-specs-from-json
  [json-models data]
        ;; get the col stattypes. Need to do this here because they live
        ;; outside the individual models in the ensemble.
  (let [stat-types (get-col-types json-models)
        ;; Same for categories.
        categories  (get json-models "categories")
        ;; A function for getting each view in a given model.
        get-views (fn [json-model] ;; XXX: What's the right way to not use fn here? %({ sseesm to not do the right thing
                    {:views
                     (into []
                           (map-indexed (fn [view-idx cols-in-view ] (generate-view-from-json json-model data view-idx stat-types categories))
                                        (get json-model "column-partition")))})] ;; TODO: this last line is wrong. I really want an index here -- so that I can get the right params
    ;; n models.
    (map #(merge stat-types (get-views %))
         (get json-models "models"))))


(first  (generate-specs-from-json jms d))
(second (generate-specs-from-json jms d))


(get (first (get jms  "models")) "column-partition")
;; TODO: remove old from-json and rename stuff accordingly.
(defn get-col-categories [json-models col] (raise-not-implemented-error))
(defn get-col-type [json-models col] (raise-not-implemented-error))
(defn get-col-partition [json-model] (raise-not-implemented-error))
(defn get-col-hypers     [json-model col] (raise-not-implemented-error))




(s/fdef cluster-variables
  :args (s/cat :cluster ::cluster)
  :ret (s/coll-of ::column))

(defn- cluster-variables
  [cluster]
  (set (keys (:parameters cluster))))

(s/fdef view-variables
  :args (s/cat :view ::view)
  :ret (s/coll-of ::column))

(defn view-variables
  "Returns the variables assigned to given view."
  [view]
  (cluster-variables (first view)))

(s/fdef variables
  :args (s/cat :mmix ::multi-mixture)
  :ret (s/coll-of ::column))

(defn variables
  "Returns the variables in a multi-mixture."
  [mmix]
  (set (keys (:vars mmix))))

(s/fdef view-index-for-variable
  :args (s/cat :mmix ::multi-mixture
               :variable ::column))

(defn view-index-for-variable
  "Returns the index of the view a given variable was assigned to."
  [mmix variable]
  (some (fn [[i view]]
          (when (contains? (view-variables view) (name variable))
            i))
        (map-indexed vector (:views mmix))))

(defn view-for-variable
  "Returns the view a given variable was assigned to."
  [mmix variable]
  (some (fn [view]
          (when (contains? (:parameters (first view))
                           variable)
            view))
        (:views mmix)))

(defn stattype
  "Returns the statistical type (distribution from `metaprob.distributions`) of a
  variable."
  [mmix variable]
  (get-in mmix [:vars variable]))

(defn nominal?
  "Returns true if `variable` is a nominal variable in `mmix`."
  [mmix variable]
  (= :categorical (stattype mmix variable)))

(defn numerical?
  "Returns true if `variable` is a numerical variable in `multimixture`."
  [mmix variable]
  (= :gaussian (stattype mmix variable)))

(defn parameters
  "Returns the parameters of a variable for a cluster."
  [mmix variable cluster-idx]
  (let [view (view-for-variable mmix variable)]
    (get-in view [cluster-idx :parameters variable])))

(defn mu
  "Returns the mu for the given variable."
  [mmix variable cluster-idx]
  (:mu (parameters mmix variable cluster-idx)))

(defn sigma
  "Returns the sigma for the given variable."
  [mmix variable cluster-idx]
  (:sigma (parameters mmix variable cluster-idx)))

(defn cluster-probability
  [mmix view-idx cluster-idx]
  (get-in mmix [:views view-idx cluster-idx :probability]))

(defn categories
  [mmix variable]
  (-> (view-for-variable mmix variable)
      (get-in [0 :parameters variable])
      keys
      set))

(s/fdef categorical-probabilities
  :args (s/cat :mmix ::multi-mixture
               :variable ::variable
               :cluster-idxs (s/+ nat-int?)))

(defn categorical-probabilities
  "Returns the probabilities for the given categorical variable. If multiple
  clusters are provided the weighted (by cluster probability) sum is returned
  instead."
  ([mmix variable cluster-idx]
   (parameters mmix variable cluster-idx))
  ([mmix variable cluster-idx-1 cluster-idx-2 & more]
   (let [cluster-idxs (into more [cluster-idx-1 cluster-idx-2])
         view-idx (view-index-for-variable mmix variable)
         view (get-in mmix [:views view-idx])]
     (->> cluster-idxs
          (map #(nth view %))
          (map (fn [{:keys [probability parameters]}]
                 (reduce-kv (fn [m k v]
                              (assoc m k (* v probability)))
                            {}
                            (get parameters variable))))
          (apply merge-with +)))))
