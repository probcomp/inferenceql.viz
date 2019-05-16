(ns inferdb.utils)


(defn abs
  [n]
  (max n (- n)))

(defn almost-equal
  "Returns true if `a` and `b` are approximately equal. Takes a difference
  metric (presumably from `inferdb.metrics`) as its second argument."
  [a b difference-metric threshold]
  (< (difference-metric a b) threshold))
