(ns inferdb.multimixture.data
  (:require [metaprob.distributions :as dist]
            [inferdb.multimixture.dsl :as dsl]))

(defn crosscat-row-generator
  "Creates a crosscat row generator from the provided data representation of a
  crosscat model. For an example of such a data representation see the tests."
  [mmix]
  (apply dsl/multi-mixture
         (map (fn [{:keys [vars clusters]}]
                (dsl/view vars (apply dsl/clusters
                                      (mapcat (fn [{:keys [probability args]}]
                                                [probability args])
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
    (get-in view [:clusters cluster-idx :args (name variable)])))

(defn mu
  "Returns the mu for the given variable."
  [mmix variable cluster-idx]
  (first (parameters mmix variable cluster-idx)))

(defn sigma
  "Returns the sigma for the given variable."
  [mmix variable cluster-idx]
  (second (parameters mmix variable cluster-idx)))

(defn categorical-probabilities
  "Returns the probabilities for the given categorical variable"
  [mmix variable cluster-idx]
  (first (parameters mmix variable cluster-idx)))
