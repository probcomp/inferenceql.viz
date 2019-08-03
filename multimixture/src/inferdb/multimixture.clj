(ns inferdb.multimixture
  (:require [clojure.math.combinatorics :as combo]
            [metaprob.generative-functions :as gfn :refer [apply-at at gen]]
            [metaprob.distributions :as dist]
            [metaprob.prelude :as mp]
            [metaprob.inference :as inference]
            [inferdb.multimixture.dsl-test :as dsl-test]
            [inferdb.multimixture.specification :as spec]
            [zane.vega.repl :as vega]))

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

(defn- uniform-categorical-params
  [n]
  (repeat n (double (/ 1 n))))

(def generate-1col-binary-extension
  (gen [mmix-spec row-count column-key {:keys [alpha beta]}]
    (let [view-idx (at :view dist/categorical (uniform-categorical-params (count mmix-spec)))
          new-spec (-> mmix-spec
                       (assoc-in [view-idx :vars column-key] :binary)
                       (update-in [view-idx :clusters]
                                  (fn [clusters]
                                    (vec (map-indexed (fn [i cluster]
                                                        (update cluster :parameters
                                                                #(assoc % column-key
                                                                        [(at `(:cluster-parameters ~i)
                                                                             dist/beta alpha beta)])))
                                                      clusters)))))
          new-row-generator (optimized-row-generator new-spec)]
      (doseq [i (range row-count)]
        (at `(:rows ~i) new-row-generator))
      new-spec)))

#_(let [rows [{"x" 0, "y" true}
              {"x" 5, "y" false}]
        spec [{:vars {"x" :gaussian}
               :clusters [{:probability 0.75
                           :parameters {"x" [0 1]}}
                          {:probability 0.25
                           :parameters {"x" [5 1]}}]}]]
    #_
    (mp/infer-and-score :procedure (optimized-row-generator spec))
    (mp/infer-and-score :procedure generate-1col-binary-extension
                        :inputs [spec (count rows) "y" {:alpha 0.001 :beta 0.001}]))

(defn with-rows
  "Given a trace for generate-1col, produces a trace with the values in rows
  constrained."
  [trace rows]
  (assoc trace :rows (reduce (fn [acc [i row]]
                               (assoc acc i (with-row-values {} row)))
                             {}
                             (map-indexed vector rows))))

#_(with-rows {} [{"a" 1, "b" 2}])

(defn importance-resampling
  [& {:keys [model inputs observation-trace n-particles]
      :or {inputs [], observation-trace {}, n-particles 1}}]
  (let [particles (mp/replicate n-particles
                                (fn []
                                  (mp/infer-and-score :procedure model
                                                      :inputs inputs
                                                      :observation-trace observation-trace)))]
    (nth particles (dist/log-categorical (map (fn [[_ _ s]] s) particles)))))

(defn insert-column
  "Takes a multimixture specification, views, and a set of rows that have a value
  in the new column that is being added. Returns an updated multimixture
  specification."
  [spec rows column-key beta-params]
  (first
   (importance-resampling :model generate-1col-binary-extension
                          :inputs [spec (count rows) column-key beta-params]
                          :observation-trace (with-rows {} rows)
                          :n-particles 100)))

(defn score-rows
  [spec rows new-column-key]
  (let [new-column-view (spec/view-index-for-variable spec new-column-key)
        row-generator (optimized-row-generator spec)]
    (mapv (fn [row]
            (let [[_ trace _] (mp/infer-and-score :procedure row-generator
                                                  :observation-trace (with-row-values {} row))
                  cluster-idx (get-in trace [:cluster-assignments-for-view new-column-view :value])]
              (get-in spec [new-column-view :clusters cluster-idx :parameters new-column-key 0])))
          rows)))

#_(let [rows [{"x" 0}
              {"x" 5}]
        spec [{:vars {"x" :gaussian
                      "y" :binary}
               :clusters [{:probability 0.75
                           :parameters {"x" [0 1]
                                        "y" [0]}}
                          {:probability 0.25
                           :parameters {"x" [5 1]
                                        "y" [1]}}]}]]
    (score-rows spec rows "y"))

(defn search
  [spec new-column-key known-rows unknown-rows n-models beta-params]
  (let [specs (repeatedly n-models #(insert-column spec known-rows new-column-key beta-params))
        scores (map (fn [spec]
                      (score-rows spec unknown-rows new-column-key))
                    specs)]
    (mapv (fn [i]
            (/ (reduce + (map #(nth % i) scores))
               n-models))
          (range (count unknown-rows)))))

#_(let [unknown-rows [{"x" 0}
                      {"x" 1}
                      {"x" 2}
                      {"x" 3}
                      {"x" 4}
                      {"x" 5}]
        known-rows [{"x" 0 "y" true}
                    {"x" 0 "y" true}
                    {"x" 5 "y" false}]
        spec [{:vars {"x" :gaussian}
               :clusters [{:probability 0.5
                           :parameters {"x" [0 2]}}
                          {:probability 0.5
                           :parameters {"x" [5 2]}}]}]]
    (time (search spec "y" known-rows unknown-rows 100 {:alpha 0.001 :beta 0.001})))

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
       (map (fn [cluster-assignments]
              {:cluster-assignments-for-view
               (->> cluster-assignments
                    (map-indexed (fn [view-i cluster-i]
                                   {view-i {:value cluster-i}}))
                    (into {}))}))))

#_(let [spec dsl-test/multi-mixture]
    (->> (all-latents spec)
         (map (comp last #(mp/infer-and-score :procedure (mmix/row-generator spec)
                                              :observation-trace %)))))

(defn score-probabilities
  [logscores]
  (let [scores (map mp/exp logscores)]
    (if (every? #(== 0 %) scores)
      (uniform-categorical-params (count scores))
      (dist/normalize-numbers scores))))

#_(score-probabilities (map #(Math/log %) [1 2 3 0]))

(defn optimized-row-generator
  [spec]
  (let [row-generator (row-generator spec)]
    (gfn/make-generative-function
     row-generator
     (gen [partial-trace]
       (let [all-latents    (all-latents spec)
             all-traces     (mapv #(merge partial-trace %)
                                  all-latents)
             all-logscores  (mapv #(last (mp/infer-and-score :procedure row-generator
                                                             :observation-trace %))
                                  all-traces)
             all-scores (map mp/exp all-logscores)
             all-zeroes (every? #(== 0 %) all-scores)
             log-normalizer (if all-zeroes ##-Inf (dist/logsumexp all-logscores))
             score          log-normalizer
             categorical-params (if all-zeroes
                                  (uniform-categorical-params (count all-scores))
                                  (dist/normalize-numbers all-scores))]
         (gen []
           (let [i     (dist/categorical categorical-params)
                 trace (nth all-traces i)
                 v     (first (mp/infer-and-score :procedure row-generator
                                                  :observation-trace trace))]
             [v trace score])))))))

#_(dist/logsumexp [##-Inf])
#_(dist/normalize-numbers [0 0 0])

#_(optimized-row-generator dsl-test/multi-mixture)
#_((optimized-row-generator dsl-test/multi-mixture))
#_(repeatedly 100 #(mp/infer-and-score :procedure (optimized-row-generator dsl-test/multi-mixture)))
