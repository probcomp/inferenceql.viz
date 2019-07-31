(ns inferdb.multimixture
  #?(:cljs (:require-macros [metaprob.generative-functions :as gfn :refer [gen]]))
  (:require #?(:clj [metaprob.generative-functions :as gfn :refer [apply-at at gen]]
               :cljs [metaprob.generative-functions :as gfn :refer [apply-at at]])
            [metaprob.distributions :as dist]
            [metaprob.prelude :as mp]
            [inferdb.multimixture.specification :as spec]))

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

#_(assoc-in {} [:x :y :z] 0)

#_(->> (repeatedly 1000 #(let [{:keys [vars clusters]} (second mmix)]
                           (dist/categorical (map :probability clusters))))
       (frequencies))

#_(->> mmix
       (second)
       (:clusters)
       (map :probability))

(defn multimixture
  "Returns a generative function that samples a row from the provided view specification."
  [views]
  (let [view-var-index (zipmap (range) (map spec/view-variables views))
        metadata {:spec views
                  :view-var-index view-var-index}
        f (gen []
            (into {} (map-indexed (fn [i view]
                                    (at i sample-view view))
                                  views)))]
    (with-meta f (merge (meta f) metadata))))

#_(multimixture mmix)
#_((multimixture mmix))
#_(meta (multimixture mmix))
#_(mp/infer-and-score :procedure (multimixture mmix))
#_(->> mmix
       (map (fn [view])))
