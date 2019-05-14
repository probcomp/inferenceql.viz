(ns inferdb.utils)

(defn abs [n] (max n (- n)))
(defn relerr [a b] (abs (- a b)))
(defn almost-equal [a b threshold]
  (< (relerr a b) threshold))
