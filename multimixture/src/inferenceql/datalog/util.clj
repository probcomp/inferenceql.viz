(ns inferenceql.datalog.util)

(defn map-keys
  [m f]
  (reduce-kv (fn [acc k v]
               (assoc acc (f k) v))
             {}
             m))

(defn map-vals
  [m f]
  (reduce-kv (fn [acc k v]
               (assoc acc k (f v)))
             {}
             m))

(defn filter-vals
  [m f]
  (reduce-kv (fn [acc k v]
               (cond-> acc
                 (f v) (assoc k v)))
             {}
             m))

(defn remove-vals
  [m f]
  (reduce-kv (fn [acc k v]
               (cond-> acc
                 (not (f v)) (assoc k v)))
             {}
             m))

(defn parse-double
  [s]
  (when s
    (Double/parseDouble s)))
