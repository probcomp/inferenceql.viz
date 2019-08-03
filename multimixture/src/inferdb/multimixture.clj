(ns inferdb.multimixture
  (:require [clojure.math.combinatorics :as combo]
            [metaprob.generative-functions :as gfn :refer [apply-at at gen]]
            [metaprob.distributions :as dist]))

#_(def mmix
    [{:vars {"x" :gaussian
             "y" :categorical}
      :clusters [{:probability 1
                  :parameters {"x" [2 3]
                               "y" [[0.1 0.2 0.3 0.4]]}}]}
     {:vars {"a" :gaussian
             "b" :categorical}
      :clusters [{:probability 0.4
                  :parameters {"a" [4 5]
                               "b" [[0.9 0.01 0.02 0.03 0.04]]}}
                 {:probability 0.6
                  :parameters {"a" [6 7]
                               "b" [[0.99 0.001 0.002 0.003 0.004]]}}]}])

;; This is a duplicate of inferdb.multimixture.specification/view-variables,
;; only it doesn't keywordize the results before returning them. Probably the
;; right thing to do is to change that version to not keywordize variables
;; either, delete this one, and then have the callers who depend on the results
;; being keywords do that conversion themselves.
(defn view-variables
  "Returns the variables assigned to given view."
  [view]
  (into #{}
        (keys (:vars view))))

(defn row-generator
  "Returns a generative function that samples a row from the provided view
  specification."
  [spec]
  (let [view-var-index (zipmap (range) (map view-variables spec))
        var-view-index (reduce-kv (fn [m view variables]
                                    (merge m (zipmap variables (repeat view))))
                                  {}
                                  view-var-index)
        metadata {:spec spec
                  :view-var-index view-var-index
                  :var-view-index var-view-index}
        f (gen []
            (into {} (doall (map-indexed (fn [i view]
                                           (let [{:keys [vars clusters]} view
                                                 cluster-idx (at `(:cluster-assignments-for-view ~i) dist/categorical (map :probability clusters))
                                                 cluster (nth clusters cluster-idx)]
                                             (reduce-kv (fn [m variable params]
                                                          (let [primitive (case (get vars variable)
                                                                            :binary dist/flip
                                                                            :gaussian dist/gaussian
                                                                            :categorical dist/categorical)]
                                                            (assoc m variable (apply-at `(:columns ~variable) primitive params))))
                                                        {}
                                                        (:parameters cluster))))
                                         spec))))]
    (with-meta f (merge (meta f) metadata))))

#_(row-generator mmix)
#_((row-generator mmix))
#_(meta (row-generator mmix))
#_(mp/infer-and-score :procedure (row-generator mmix))

(defn with-cluster-assignment
  "Sets the cluster assignment in trace for view index view-i to cluster index
  cluster-i."
  [trace view-i cluster-i]
  (assoc-in trace [:cluster-assignments-for-view view-i :value] cluster-i))

#_(-> {}
      (with-cluster-assignment 0 0)
      (with-cluster-assignment 1 0))

(defn with-cell-value
  "Sets the cell value in trace for variable var to value v."
  [trace var v]
  (assoc-in trace [:columns var :value] v))

#_(-> {}
      (with-cell-value "x" 27)
      (with-cell-value "a" 99)
      (with-cell-value "y" 3))

(defn with-row-values
  "Sets the values in the trace for cells in row to their values."
  [trace row]
  (reduce-kv (fn [trace var v]
               (with-cell-value trace var v))
             trace
             row))

#_(with-row-values {} {"x" 27
                       "a" 99
                       "y" 3})

(declare optimized-row-generator)

(defn uniform-categorical-params
  [n]
  (repeat n (double (/ 1 n))))

(defn with-rows
  "Given a trace for generate-1col, produces a trace with the values in rows
  constrained."
  [trace rows]
  (assoc trace :rows (reduce (fn [acc [i row]]
                               (assoc acc i (with-row-values {} row)))
                             {}
                             (map-indexed vector rows))))

#_(with-rows {} [{"a" 1, "b" 2}])

(defn- valueify
  [m]
  (reduce-kv (fn [m k v]
               (assoc m k {:value v}))
             {}
             m))

(defn all-latents
  "Returns a lazy sequence of all the possible traces of latents."
  [spec]
  (->> spec
       (map (comp range count :clusters))
       (apply combo/cartesian-product)
       (map (fn [assignments]
              {:cluster-assignments-for-view
               (->> assignments
                    (map-indexed (fn [view-i cluster-i]
                                   {view-i {:value cluster-i}}))
                    (into {}))}))))

#_(let [spec dsl-test/multi-mixture]
    (->> (all-latents spec)
         (map (comp last #(mp/infer-and-score :procedure (mmix/row-generator spec)
                                              :observation-trace %)))))
