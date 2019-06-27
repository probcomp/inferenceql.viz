(ns inferdb.multimixture.data
  (:require [metaprob.distributions :as dist]
            [inferdb.multimixture.dsl :as dsl]))

(defn crosscat-row-generator
  [mmix]
  (apply dsl/multi-mixture
         (map (fn [{:keys [vars clusters]}]
                (dsl/view vars (apply dsl/clusters
                                      (mapcat (fn [{:keys [probability args]}]
                                                [probability args])
                                              clusters))))
              mmix)))

(defn view-variables
  [view]
  (into #{}
        (map keyword)
        (keys (:vars view))))

(defn variables
  [mmix]
  (into #{}
        (mapcat view-variables)
        mmix))

(defn- view-for-variable
  [mmix variable]
  (some (fn [view]
          (when (contains? (:vars view) (name variable))
            view))
        mmix))

(defn stattype
  [mmix variable]
  (let [view (view-for-variable mmix variable)]
    (get-in view [:vars (name variable)])))

(defn categorical?
  "Returns true if `variable` is a categorical variable in `mmix`."
  [mmix variable]
  (= dist/categorical (stattype mmix variable)))

(defn nominal?
  "Returns true if `variable` is a nominal variable in `multimixture`."
  [mmix variable]
  (= dist/gaussian (stattype mmix variable)))

(defn parameters
  [mmix variable cluster]
  (let [view (view-for-variable mmix variable)]
    (get-in view [:clusters cluster :args (name variable)])))

(defn mu
  [mmix variable cluster]
  (first (parameters mmix variable cluster)))

(defn sigma
  [mmix variable cluster]
  (second (parameters mmix variable cluster)))

(defn categorical-probabilities
  [mmix variable cluster]
  (first (parameters mmix variable cluster)))
