(ns inferenceql.multimixture.utils)

(defn loglinspace
  "Log linear space from start to end with stepsize n over a log scale."
  [start end n]
  (let [lstart (Math/log start)
        lend   (Math/log end)
        step   (/ (- lend lstart)
                  n)]
  (map #(Math/exp %) (range lstart lend step))))

(defn logsumexp
  "Log-sum-exp operation for summing log probabilities without
  leaving the log domain."
  [log-ps]
  (if (= 1 (count log-ps))
    (first log-ps)
    (let [log-ps-sorted (sort > log-ps)
          a0            (first log-ps-sorted)
          tail          (drop 1 log-ps-sorted)
          res           (+ a0 (Math/log
                                (+ 1 (reduce + (map #(Math/exp (- % a0))
                                                tail)))))]
      (if (Double/isNaN res) ; A zero-probability event has occurred.
        ##-Inf
        res))))

(defn normalize
  "Normalizes a collection of numbers."
  [coll]
  (mapv #(double (/ % ( reduce + coll))) coll))

(defn prun [n f]
  "Runs `n` parallel calls to function `f`, that is assumed to have
  no arguments."
  #?(:clj (apply pcalls (repeat n f))
     :cljs (repeatedly n f)))

(defn transpose
  "Applies the standard tranpose operation to a collection. Assumes that
  `coll` is an object capable of having a transpose."
  [coll]
  (apply map vector coll))

(defn vec-remove
  "Removes element from coll by index."
  [idx coll]
  (vec (concat (subvec coll 0 idx) (subvec coll (inc idx)))))
