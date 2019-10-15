(ns inferdb.multimixture.info-theory-queries
  (:require [metaprob.prelude :as mp]
            [inferdb.multimixture :as mmix]
            [inferdb.utils :as utils]
            [inferdb.multimixture.basic-queries :as bq]))

(defn- mi-inner-eq [row-generator joint-target target-0 target-1 condition sample]
  (let [logpdf-0 (bq/logpdf row-generator (select-keys sample target-0) condition)
        logpdf-1 (bq/logpdf row-generator (select-keys sample target-1) condition)
        logpdf-joint (bq/logpdf row-generator (select-keys sample joint-target) condition)]
    (- logpdf-joint (+ logpdf-0 logpdf-1))))

(defn mutual-information [row-generator target-0 target-1 condition num-samples]
    ;; Estimate mutual information. We allow for the condition to be either
    ;; fully specified as a map (i.e. columns and their values) or as a list of
    ;; column names without their values.
    (let [samples (bq/simulate row-generator condition num-samples)
          joint-target (concat target-0 target-1)]
      (if (map? condition)
        (utils/average (map #(mi-inner-eq row-generator joint-target target-0 target-1 condition % ) samples))
        (utils/average (map #(mi-inner-eq row-generator joint-target target-0 target-1 (select-keys % condition) %) samples)))))
