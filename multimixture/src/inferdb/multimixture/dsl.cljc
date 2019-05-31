(ns inferdb.multimixture.dsl
  (:refer-clojure :exclude [map replicate apply])
  #?(:cljs (:require-macros [metaprob.generative-functions :refer [gen]]))
  (:require [metaprob.trace :refer [trace-set-value trace-has-value?]]
            #?(:clj [metaprob.generative-functions :refer [gen]])
            [metaprob.prelude :refer [map apply infer-and-score map-xform]]
            [metaprob.distributions :refer [categorical log-categorical exactly]]
            [metaprob.inference :refer [with-custom-proposal-attached]]))

;; -------------------
;; MULTI-MIXTURE MODEL
;; -------------------

;; TODO: This appears to be used for both views and columns? Get that confirmed
;;       by Alex or maybe Feras.
(defn view-cluster-address
  [v]
  ;; (str "view-cluster-for-" v)
  (str "cluster-for-" v)
  )

(defn column-cluster-address
  [column]
  ;; (str "column-cluster-for-" column)
  (str "cluster-for-" column))

(defn view-for-column
  [column]
  ;; TODO: Implement this for real.
  0)

(defn make-view
  [[vars-and-dists [cluster-probs cluster-params]]]
  (let [view-name (str "view" (gensym))
        var-names (keys vars-and-dists)
        cluster-addr (view-cluster-address view-name)

        ;; Generative model
        sampler   (gen []
                       (let [cluster-idx (at cluster-addr categorical cluster-probs)
                             params      (nth cluster-params cluster-idx)]

                         (doseq [v var-names]
                           (at (column-cluster-address v)
                               exactly cluster-idx))

                         (mapv (fn [v]
                                 (apply-at v
                                           (get vars-and-dists v)
                                           (get params v)))
                               var-names)))]

    (with-custom-proposal-attached
      sampler
      (fn [observations]
        (gen []
             (let [score-cluster
                   (fn [idx]
                     (let [new-obs (trace-set-value observations cluster-addr idx)
                           ;; Score should not depend on any of the stochastic
                           ;; choices made by infer-and-score, so we leave this
                           ;; untraced.
                           [_ _ s]
                           (infer-and-score
                            :procedure sampler
                            :observation-trace new-obs)]
                       s))

                   cluster-scores
                   (map score-cluster (range (count cluster-probs)))

                   chosen-cluster
                   (at cluster-addr log-categorical cluster-scores)]

               ;; Fill in the rest of the choices
               (at '() infer-and-score
                   :procedure sampler
                   :observation-trace
                   (trace-set-value observations cluster-addr chosen-cluster)))))

      ;; Only use the custom proposal when we don't already know the cluster ID
      (fn [tr] (not (trace-has-value? tr cluster-addr))))))

(defn make-multi-mixture
  [views]
  (gen []
       (into []
             (comp (map-xform (fn [view] (at '() view)))
                   cat)
             views)))

(defn cluster-count [clusters]
  (/ (count clusters) 2))

;; ------------------------
;; DOMAIN SPECIFIC LANGUAGE
;; ------------------------

(defn multi-mixture
  [& viewspecs]
  (make-multi-mixture (map make-view viewspecs)))

;; View specification constructor.
(defn view [vars [probs params]] [vars [probs params]])

;; Cluster specification constructor.
(defn clusters
  [& args]
  [(take-nth 2 args)
   (take-nth 2 (rest args))])
