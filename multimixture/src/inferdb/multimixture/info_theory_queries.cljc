(ns inferdb.multimixture.info-theory-queries
  (:require [metaprob.prelude :as mp]
            [inferdb.multimixture :as mmix]
            [inferdb.multimixture.search :as search]
            [inferdb.utils :as utils]
            [inferdb.multimixture.basic-queries :as bq]))

(defn mutual-information [row-generator target-a target-b condition num-samples]
    "Estimate mutual information. We allow for the condition to be either
     fully specified as a map (i.e. columns and their values) or as a list of
     column names without their values."
    (let [samples (bq/simulate row-generator condition num-samples)
          joint-target (concat target-a target-b)
          constraint (if (map? condition)
                         (repeat num-samples condition)
                         (map #(select-keys % condition) samples))
          logpdf-estimate (fn [target]
                            (utils/average (map-indexed (fn [i sample]
                                                          (bq/logpdf row-generator
                                                                     (select-keys sample target)
                                                                     (nth constraint i)))
                                                samples)))
          ;; TODO: will we get perf improvements if the run one map for all of the below?
          logpdf-a  (logpdf-estimate target-a)
          logpdf-b  (logpdf-estimate target-b)
          logpdf-ab (logpdf-estimate joint-target)]
      (- logpdf-ab (+ logpdf-a logpdf-b))))

