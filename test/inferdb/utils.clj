(ns inferdb.utils)

;; taken from metaprob/test/distributions test. Felt wrong to import from a
;; metaprob test...
(defn normalize [weights]
  (let [total (apply + weights)]
    (map (fn [x] (/ x total)) weights)))

(defn abs [n]
  (max n (- n)))

(defn all? [l]
  (every? (fn [x] x) l))

(defn almost-equal
  "Returns true if scalars `a` and `b` are approximately equal. Takes a difference
  metric (presumably from `inferdb.metrics`) as its second argument."
  [a b difference-metric threshold]
  (< (difference-metric a b) threshold))

(defn almost-equal-vectors
  "Returns true if vectors `a` and `b` are approximately equal. Takes a difference
  metric (presumably from `inferdb.metrics`) as its second argument."
  [a b difference-metric threshold]
  (assert (count a) (count b))
  (let [call-almost-equal
        (fn [i] (almost-equal  (nth a i) (nth b i) difference-metric threshold))]
  (all? (map call-almost-equal (range (count a))))))
