(ns inferenceql.multimixture.stattypes.primitives
  (:require [inferenceql.multimixture.stattypes.primitives.bernoulli]
            [inferenceql.multimixture.stattypes.primitives.categorical]
            [inferenceql.multimixture.stattypes.primitives.gaussian])
  (:import inferenceql.multimixture.stattypes.primitives.bernoulli.Bernoulli
           inferenceql.multimixture.stattypes.primitives.categorical.Categorical
           inferenceql.multimixture.stattypes.primitives.gaussian.Gaussian))

(defn bernoulli?
  [stattype]
  (and (record? stattype)
       (instance? Bernoulli stattype)))

(defn categorical?
  [stattype]
  (and (record? stattype)
       (instance? Categorical stattype)))

(defn gaussian?
  [stattype]
  (and (record? stattype)
       (instance? Gaussian stattype)))

(defn primitive?
  "Checks whether the given stattype is primtive."
  [stattype]
  (and (record? stattype)
       (or (bernoulli? stattype)
           (categorical? stattype)
           (gaussian? stattype))))
