(ns inferenceql.multimixture.gpm.proto)

(defprotocol GPM
  "A simple protocol for defining a GPM."
  (logpdf             [this x]
                      [this targets constraints]
                      [this targets constraints inputs])
  (simulate           [this n-samples]
                      [this targets constraints n-samples]
                      [this targets constraints n-samples inputs])
  (mutual-information [this target-a target-b constraints n-samples]))

