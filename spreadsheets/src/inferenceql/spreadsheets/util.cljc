(ns inferenceql.spreadsheets.util
  (:require [medley.core :as medley]))

(defn filter-nil-kvs [a-map]
  (into {} (remove (comp nil? val) a-map)))

(defn abs
  "Helper function for calling the math absolute value function"
  [n]
  #?(:clj  (Math/abs (double n))
     :cljs (js/Math.abs n)))

(defn assoc-or-dissoc-in [m ks v]
  "Takes a map `m` and associates key-sequence `ks` with `v`.
  If `v` in nil however, this instead dissocates the keysequence `ks` in `m`."
  (if (some? v)
    (assoc-in m ks v)
    (medley/dissoc-in m ks)))
