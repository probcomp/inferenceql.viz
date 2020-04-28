(ns inferenceql.multimixture.gpm.crosscat
  (:require [inferenceql.utils :as utils]
            [inferenceql.multimixture.crosscat :as xcat]
            [inferenceql.multimixture.gpm.proto :as gpm-proto]))

(defrecord CrossCat [model latents]
  gpm-proto/GPM
  (logpdf [this targets constraints inputs]
    (xcat/logpdf-score model latents targets constraints))
  (simulate [this targets constraints n-samples inputs]
    (repeatedly n-samples #(xcat/simulate
                            model
                            latents
                            targets
                            constraints)))
  (mutual-information [this target-a target-b constraints n-samples]
    (xcat/mutual-information model latents target-a target-b constraints n-samples)))
