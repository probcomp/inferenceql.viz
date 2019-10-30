(ns inferenceql.multimixture.search.deterministic
  (:require [clojure.spec.alpha :as s]
            [metaprob.generative-functions :as g :refer [at gen]]
            [metaprob.prelude :as mp]
            [inferenceql.multimixture :as mmix]
            [inferenceql.multimixture.search :as sch]
            [inferenceql.multimixture.specification :as spec]))

#?(:cljs (enable-console-print!))

(defn transpose
  [coll]
  (apply map vector coll))

(defn normalize-row-probability
  "Normalizes a collection of non-negative numbers."
  [coll]
  (let [z (reduce + coll)]
    (mapv #(/ % z) coll)))

(defn cluster-row-probability
  "Determines the probability that a specific cluster component generated the given row."
  [spec cluster-idx view-idx row]
   ;; Prior probability * likelihood that cluster generated row, given the row and spec.
  (let [cluster (get-in spec [:views view-idx cluster-idx])]
    (* (:probability cluster)
       (mp/exp (last (mp/infer-and-score :procedure (mmix/cluster-row-generator cluster (get spec :vars))
                                         :observation-trace (mmix/with-row-values {} row)))))))

(defn view-row-probabilities
  "Returns a probability table P, where 
   P[row][cluster-component] = normalized probability (within the specified view)
                               that `cluster-component` generated `row`."
  [spec rows]
  (map-indexed (fn [view-idx view]
                 (let [view (get-in spec [:views view-idx])]
                 ;; For each cluster in a view, for each row in cluster, determine the
                 ;; probability that a cluster generated a row.
                   (->> view
                        (map-indexed (fn [cluster-idx _]
                                       (map #(cluster-row-probability
                                              spec
                                              cluster-idx
                                              view-idx
                                              %) rows)))
                      ;; Group probabilities by row, rather than by cluster.
                        (transpose)
                        (map normalize-row-probability))))
               (get spec :views)))

(defn generate-cluster-row-probability-table
  "Returns table P where, P[known/unknown][view][row][component] is equivalent
   to the probability that a known/unknown row within a view by a specific component."
  [spec known-rows unknown-rows]
  (map (fn [rows]
         (view-row-probabilities spec rows)) [known-rows unknown-rows]))

(defn update-beta-params
  "Updates beta params for each component, within each view."
  [beta-params known-rows new-column-key num-clusters known-probs]
  (let [n               (count known-probs)
        obs-probs-pairs (map vector known-probs (map #(get % new-column-key) known-rows))
        true-obs        (filter #(second %) obs-probs-pairs)
        false-obs       (filter #(not (second %)) obs-probs-pairs)
        obs-func        (fn [obs param]
                           ;; If no observations, alpha and beta do not get updated.
                          (if (empty? obs)
                            (repeat num-clusters {param (param beta-params)})
                            (->> obs
                                  ;; Ignore indices and group by cluster, not row, to compute
                                  ;; the necessary sum.
                                 (map #(first %))
                                 (transpose)
                                 (map #(apply + %))
                                 (map (fn [param-instance]
                                        {param (+ (param beta-params) param-instance)})))))
        alphas (obs-func true-obs :alpha)
        betas (obs-func false-obs :beta)]
    (map merge alphas betas)))

(defn search
  "Mimicks the behavior of search, but without sampling!"
  [spec new-column-key known-rows unknown-rows beta-params]
  (let [[known-probs unknown-probs] (generate-cluster-row-probability-table spec known-rows unknown-rows)
        num-clusters               (count (get-in spec [:views 0]))
        beta-primes                (map #(update-beta-params
                                          beta-params
                                          known-rows
                                          new-column-key
                                          num-clusters
                                          %)
                                        known-probs)]
    (vec (flatten (map-indexed
                   (fn [row-idx _]
                     (map-indexed
                      (fn [view-idx beta-prime]
                        (reduce +
                                (map-indexed
                                 (fn [idx-cluster cluster-params]
                                   (let [row-cluster-prob (-> unknown-probs
                                                              (nth view-idx)
                                                              (nth row-idx)
                                                              (nth idx-cluster))]
                                     (* row-cluster-prob
                                        (/ (:alpha cluster-params)
                                           (+ (:alpha cluster-params)
                                              (:beta cluster-params))))))
                                 beta-prime)))
                      beta-primes))
                   unknown-rows)))))

;; Simpler example for deterministic search, single view.
(let [unknown-rows [{"x" 0}
                    {"x" 5}]
      known-rows [{"x" 0 "y" true}
                  {"x" 5 "y" false}]
      spec {:vars {"x" :gaussian}
            :views [[{:probability 0.5
                      :parameters {"x" {:mu 2 :sigma 1}}}
                     {:probability 0.5
                      :parameters {"x" {:mu 3 :sigma 1}}}]]}]
  #_(sch/search spec "y" known-rows unknown-rows 100 {:alpha 0.001 :beta 0.001})
  #_(search spec "y" known-rows unknown-rows {:alpha 0.001 :beta 0.001}))

;; Simpler example for deterministic search, multiple views.
(let [unknown-rows [{"x" 0}
                    {"x" 5}]
       ; known-rows [{"x" 0 "y" true}
       ;             {"x" 5 "y" false}]
      known-rows [{"x" 5 "y" false}]
      spec {:vars {"x" :gaussian}
            :views [[{:probability 0.5
                      :parameters {"x" {:mu 0 :sigma 1}}}
                     {:probability 0.5
                      :parameters {"x" {:mu 5 :sigma 1}}}]
                    [{:probability 0.75
                      :parameters {"x" {:mu 1 :sigma 1}}}
                     {:probability 0.25
                      :parameters {"x" {:mu 3 :sigma 2}}}]]}]
  #_(search spec "y" known-rows unknown-rows 1000 {:alpha 0.001 :beta 0.001})
  #_(generate-cluster-row-probability-table spec known-rows unknown-rows)
  #_(deterministic-search spec "y" known-rows unknown-rows {:alpha 0.001 :beta 0.001}))
