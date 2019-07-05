(ns inferdb.multimixture.dsl
  (:refer-clojure :exclude [map replicate apply])
  #?(:cljs (:require-macros [metaprob.generative-functions :as gfn :refer [gen]]))
  (:require [metaprob.distributions :as dist]
            #?(:clj [metaprob.generative-functions :as gfn :refer [gen]])
            [metaprob.prelude :refer [map apply infer-and-score map-xform]]
            [metaprob.trace :as trace :refer [trace-set-value trace-has-value?]]))

;;; Multi-mixture model

;; TODO: This appears to be used for both views and columns? Get that confirmed
;;       by Alex or maybe Feras.
(defn view-cluster-address
  [v]
  (str "cluster-for-" v))

(defn column-cluster-address
  [column]
  ;; (str "column-cluster-for-" column)
  (str "cluster-for-" column))

(defn view-for-column
  [_column]
  ;; TODO: Implement this for real.
  0)

(defn make-view
  [[vars-and-dists [cluster-probs cluster-params]]]
  (let [view-name (str "view" (gensym))
        var-names (keys vars-and-dists)
        cluster-addr (view-cluster-address view-name)
        ;; Generative model
        sampler (gen []
                  (let [cluster-idx (at cluster-addr dist/categorical cluster-probs)
                        params      (nth cluster-params cluster-idx)]
                    (doseq [v var-names]
                      (at (column-cluster-address v)
                          dist/exactly cluster-idx))

                    (mapv (fn [v]
                            (apply-at v
                                      (get vars-and-dists v)
                                      (get params v)))
                          var-names)))]
    (gfn/make-generative-function
     ;; To run in Clojure, use the same method as before:
     sampler
     (gen [observations]
       (let [score-cluster
             (fn [idx]
               (let [new-obs (trace-set-value observations cluster-addr idx)
                     ;; Score should not depend on any of the stochastic
                     ;; choices made by infer-and-score, so we leave this
                     ;; untraced.
                     [_ t s]
                     (infer-and-score
                      :procedure sampler
                      :observation-trace new-obs)]
                 s))
             cluster-scores
             (map score-cluster (range (count cluster-probs)))
             chosen-cluster
             (at cluster-addr dist/log-categorical cluster-scores)]
         (gen [& args]
           (let [[v t s] (infer-and-score
                          :procedure sampler
                          :inputs args
                          :observation-trace (trace-set-value observations
                                                              cluster-addr
                                                              chosen-cluster))]
             [v t (dist/logsumexp cluster-scores)])))))))

(defn make-multi-mixture
  [views]
  (gen []
    (into []
          (comp (map-xform (fn [view] (at '() view)))
                cat)
          views)))

(defn cluster-count [clusters]
  (/ (count clusters) 2))

;;; Domain specific language

(defn multi-mixture
  [& viewspecs]
  (make-multi-mixture (map make-view viewspecs)))

(defn view
  "View specification constructor."
  [vars [probs params]]
  [vars [probs params]])

(defn clusters
  "Cluster specification constructor."
  [& args]
  [(take-nth 2 args)
   (take-nth 2 (rest args))])
