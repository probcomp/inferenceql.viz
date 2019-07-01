(ns inferdb.multimixture.data
  (:require [metaprob.distributions :as dist]
            [inferdb.multimixture.dsl :as dsl]
            [inferdb.utils :as utils]))

(defn crosscat-row-generator
  "Creates a crosscat row generator from the provided data representation of a
  crosscat model. For an example of such a data representation see the tests."
  [mmix]
  (apply dsl/multi-mixture
         (map (fn [{:keys [vars clusters]}]
                (dsl/view vars (apply dsl/clusters
                                      (mapcat (fn [{:keys [probability parameters]}]
                                                [probability parameters])
                                              clusters))))
              mmix)))

(defn view-variables
  "Returns the variables assigned to given view."
  [view]
  (into #{}
        (map keyword)
        (keys (:vars view))))

(defn variables
  "Returns the variables in a multi-mixture."
  [mmix]
  (into #{}
        (mapcat view-variables)
        mmix))

(defn- view-index-for-variable
  "Returns the index of the view a given variable was assigned to."
  [mmix variable]
  (some (fn [[i view]]
          (when (contains? (:vars view) (name variable))
            i))
        (map-indexed vector mmix)))

(defn- view-for-variable
  "Returns the view a given variable was assigned to."
  [mmix variable]
  (some (fn [view]
          (when (contains? (:vars view) (name variable))
            view))
        mmix))

(defn stattype
  "Returns the statistical type (distribution from `metaprob.distributions`) of a
  variable."
  [mmix variable]
  (let [view (view-for-variable mmix variable)]
    (get-in view [:vars (name variable)])))

(defn nominal?
  "Returns true if `variable` is a nominal variable in `mmix`."
  [mmix variable]
  (= dist/categorical (stattype mmix variable)))

(defn numerical?
  "Returns true if `variable` is a numerical variable in `multimixture`."
  [mmix variable]
  (= dist/gaussian (stattype mmix variable)))

(defn parameters
  "Returns the parameters of a variable for a cluster."
  [mmix variable cluster-idx]
  (let [view (view-for-variable mmix variable)]
    (get-in view [:clusters cluster-idx :parameters (name variable)])))

(defn mu
  "Returns the mu for the given variable."
  [mmix variable cluster-idx]
  (first (parameters mmix variable cluster-idx)))

(defn sigma
  "Returns the sigma for the given variable."
  [mmix variable cluster-idx]
  (second (parameters mmix variable cluster-idx)))

(defn cluster-probability
  [mmix view-idx cluster-idx]
  (get-in mmix [view-idx :clusters cluster-idx :probability]))

(defn categorical-probabilities
  "Returns the probabilities for the given categorical variable. If multiple
  clusters are provided the weighted (by cluster probability) sum is returned
  instead."
  ([mmix variable cluster-idx]
   (first (parameters mmix variable cluster-idx)))
  ([mmix variable cluster-idx-1 cluster-idx-2 & more]
   (let [clusters (into more [cluster-idx-1 cluster-idx-2])
         view-idx (view-index-for-variable mmix variable)]
     (->> clusters
          (map (fn [cluster]
                 (let [cluster-probs (cluster-probability mmix view-idx cluster)]
                   (map (partial * cluster-probs)
                        (categorical-probabilities mmix variable cluster)))))
          (map (partial apply +))
          (utils/normalize)))))
