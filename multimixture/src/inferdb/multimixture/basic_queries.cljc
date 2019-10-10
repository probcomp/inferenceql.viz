(ns inferdb.multimixture.basic-queries
  (:require [metaprob.prelude :as mp]
            [inferdb.multimixture :as mmix]))

(defn simulate
  [generative-model constraints num-rows]
  (let [constraint-addrs-vals (mmix/with-row-values {} constraints)
        gen-fn #(first (mp/infer-and-score :procedure generative-model
                                           :observation-trace constraint-addrs-vals))]
    (take num-rows (repeatedly gen-fn))))
