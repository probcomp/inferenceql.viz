(ns inferdb.utils
  (:require [clojure.java.io :as io]))

;; taken from metaprob/test/distributions test. Felt wrong to import from a
;; metaprob test...
(defn normalize [weights]
  (let [total (apply + weights)]
    (map (fn [x] (/ x total)) weights)))

(defn abs [n]
  (max n (- n)))

(defn all? [l]
  (every? (fn [x] x) l))

(def relerr (fn [a b] (abs (- a b))))

(defn get-col
  [col-key table]
  (map (fn [row] (get row col-key)) table))

(defn average [column]
  (/ (reduce + column) (count column)))

(defn square [x] (* x x))

(defn std [a]
      (Math/sqrt (/
                    (reduce + (map square (map - a (repeat (average a)))))
                    (- (count a) 1 ))))


(defn save-json [file-name file-str]
  (io/make-parents file-name)
  (spit file-name file-str))

(defn column-subset [data columns]
  (let [row-subset (fn [row] (select-keys row columns))]
    (map row-subset data)))

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

(defn within-factor [a b factor]
  (and (< a (* b factor))
       (> a (/ b factor))))

(defn get-probability-for-categories [sample-vector]
  (let [get-fraction (fn [item] {(first (vals (first item)))
                                 (float (/ (second item)
                                           (count sample-vector)))})
        occurences (frequencies sample-vector)]
    (apply merge (mapv get-fraction occurences))))

(defn probability-vector [samples possible-values]
  (let [probability-map (get-probability-for-categories samples)]
    (map (fn [i] (if (contains? probability-map i) (get probability-map i) 0 ))
         possible-values)))
