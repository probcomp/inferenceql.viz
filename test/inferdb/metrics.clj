(ns inferdb.metrics
  (:require [inferdb.utils :as utils]))

(defn relerr
  "Relative error, also known as the euclidean distance."
  [a b]
  (utils/abs (- a b)))
