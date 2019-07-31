(ns inferdb.multimixture
  #?(:cljs (:require-macros [metaprob.generative-functions :as gfn :refer [gen]]))
  (:require #?(:clj [metaprob.generative-functions :as gfn :refer [apply-at at gen]]
               :cljs [metaprob.generative-functions :as gfn :refer [apply-at at]])
            [metaprob.distributions :as dist]))

#_(require '[metaprob.prelude :as mp])

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

;; Takes a view specification as per inferdb.multimixture.specification and
;; samples a partial row from that view.
(def sample-view
  (gen [{:keys [vars clusters]}]
    (let [cluster-idx (at :cluster dist/categorical (map :probability clusters))
          cluster (nth clusters cluster-idx)]
      (reduce-kv (fn [m variable params]
                   (let [primitive (case (get vars variable)
                                     :gaussian dist/gaussian
                                     :categorical dist/categorical)]
                     (assoc m variable (apply-at `(:values ~variable) primitive params))))
                 {}
                 (:parameters cluster)))))

#_(sample-view (second mmix))
#_(mp/infer-and-score :procedure sample-view
                      :inputs [(second mmix)])

(defn view-variables
  "Returns the variables assigned to given view."
  [view]
  (into #{}
        (keys (:vars view))))

(defn multimixture
  "Returns a generative function that samples a row from the provided view
  specification."
  [views]
  (let [view-var-index (zipmap (range) (map view-variables views))
        var-view-index (reduce-kv (fn [m view variables]
                                    (merge m (zipmap variables (repeat view))))
                                  {}
                                  view-var-index)
        metadata {:spec views
                  :view-var-index view-var-index
                  :var-view-index var-view-index}
        f (gen []
            (into {} (map-indexed (fn [i view]
                                    (at i sample-view view))
                                  views)))]
    (with-meta f (merge (meta f) metadata))))

#_(multimixture mmix)
#_((multimixture mmix))
#_(meta (multimixture mmix))
#_(mp/infer-and-score :procedure (multimixture mmix))

(defn with-cluster-assignment
  "Sets the cluster assignment in `trace` for view index `view-i` to cluster index
  `cluster-i`."
  [trace view-i cluster-i]
  (assoc-in trace [view-i :cluster :value] cluster-i))

#_(-> {}
      (with-cluster-assignment 0 0)
      (with-cluster-assignment 1 0))

(defn with-cell-value
  "Sets the cell value in `trace` for variable `var` to value `v`."
  [trace gf var v]
  (let [{:keys [var-view-index]} (meta gf)
        view-i (get var-view-index var)]
    (assoc-in trace [view-i :values var :value] v)))

#_(let [mm (multimixture mmix)]
    (-> {}
        (with-cell-value mm "x" 27)
        (with-cell-value mm "a" 99)
        (with-cell-value mm "y" 3)))

#_(require '[zane.vega.repl :as vega])

#_(let [spec [{:vars {"x" :gaussian
                      "y" :gaussian
                      "a" :categorical
                      "b" :categorical}
               :clusters [{:probability 0.166666666
                           :parameters {"x" [3 1]
                                        "y" [4 0.1]
                                        "a" [[1 0 0 0 0 0]]
                                        "b" [[0.95 0.01 0.01 0.01 0.01 0.01]]}}
                          {:probability 0.166666666
                           :parameters {"x" [3 0.1]
                                        "y" [4 1]
                                        "a" [[0 1 0 0 0 0]]
                                        "b" [[0.01 0.95 0.01 0.01 0.01 0.01]]}}
                          {:probability 0.166666667
                           :parameters {"x" [8 0.5]
                                        "y" [10 1]
                                        "a" [[0 0 1 0 0 0]]
                                        "b" [[0.01 0.01 0.95 0.01 0.01 0.01]]}}
                          {:probability 0.166666666
                           :parameters {"x" [14 0.5]
                                        "y" [7 0.5]
                                        "a" [[0 0 0 1 0 0]]
                                        "b" [[0.01 0.01 0.01 0.95 0.01 0.01]]}}
                          {:probability 0.166666666
                           :parameters {"x" [16 0.5]
                                        "y" [9 0.5]
                                        "a" [[0 0 0 0 1 0]]
                                        "b" [[0.01 0.01 0.01 0.01 0.95 0.01]]}}
                          {:probability 0.166666666
                           :parameters {"x" [9  2.5]
                                        "y" [16 0.1]
                                        "a" [[0 0 0 0 0 1]]
                                        "b" [[0.01 0.01 0.01 0.01 0.01 0.95]]}}]}
              {:vars {"z" :gaussian
                      "c" :categorical}
               :clusters [{:probability 0.25
                           :parameters {"z" [0 1]
                                        "c" [[1 0 0 0]]}}
                          {:probability 0.25
                           :parameters {"z" [15 1]
                                        "c" [[0 1 0 0]]}}
                          {:probability 0.25
                           :parameters {"z" [30 1]
                                        "c" [[0 0 1 0]]}}
                          {:probability 0.25
                           :parameters {"z" [15 8]
                                        "c" [[0 0 0 1]]}}]}]
        trace (-> {}
                  #_
                  (with-cluster-assignment 0 2))
        data (->> (repeatedly 1000 #(mp/infer-and-score :procedure (multimixture spec) :observation-trace trace))
                  (mapv first))]
    (vega/vega-lite {:data {:values data}
                     :width 400
                     :height 400
                     :mark "circle"
                     :encoding {:x {:field "x"
                                    :type "quantitative"}
                                :y {:field "y"
                                    :type "quantitative"}
                                :color {:field "a"}}}))
