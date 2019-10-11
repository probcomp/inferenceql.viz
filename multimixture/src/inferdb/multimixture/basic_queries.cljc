(ns inferdb.multimixture.basic-queries
  (:require [metaprob.prelude :as mp]
            [inferdb.multimixture :as mmix]))

(defn simulate
  [generative-model constraints num-rows]
  (let [constraint-addrs-vals (mmix/with-row-values {} constraints)
        gen-fn #(first (mp/infer-and-score :procedure generative-model
                                           :observation-trace constraint-addrs-vals))]
    (take num-rows (repeatedly gen-fn))))

(defn logpdf
  [row-generator target constraints]
  (let [target-addrs-vals (mmix/with-row-values {} target)
        constraint-addrs-vals (mmix/with-row-values {} constraints)
        target-constraint-addrs-vals (mmix/with-row-values {}
                                                           (merge target
                                                                  constraints))
        ;; Run infer to obtain probabilities.
        [retval trace log-weight-numer] (mp/infer-and-score
                                         :procedure row-generator
                                         :observation-trace target-constraint-addrs-vals)

        log-weight-denom (if (empty? constraint-addrs-vals)
                           ;; There are no constraints: log weight is zero.
                           0
                           ;; There are constraints: find marginal probability of constraints.
                           (let [[retval trace weight] (mp/infer-and-score
                                                        :procedure row-generator
                                                        :observation-trace constraint-addrs-vals)]
                             weight))]
    (- log-weight-numer log-weight-denom)))

