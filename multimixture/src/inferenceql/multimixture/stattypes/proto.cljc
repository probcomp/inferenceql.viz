(ns inferenceql.multimixture.stattypes.proto)

(defprotocol StatisticalType
  "Base protocol for a statistical type for a column."
  (incorporate   "Incorporates a datum `x` into the type category by
                 updating sufficient statistics accordingly.
                 Assumes row-id of the datum is maintained elsewhere." [this x])
  (unincorporate "Uncorporates a datum `x` into the type category by
                 updating sufficient statistics accordingly.
                 Assumes row-id of the datum is maintained elsewhere."[this x]))

