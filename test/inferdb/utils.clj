(ns inferdb.utils
  (:require [clojure.java.io :as io]))

;; taken from metaprob/test/distributions test. Felt wrong to import from a
;; metaprob test...
(defn normalize [weights]
  (let [total (apply + weights)]
    (map #(/ % total) weights)))

(defn abs [n]
  (max n (- n)))

(defn all? [l]
  (every? identity l))

(defn relerr [a b]
  (abs (- a b)))

(defn col
  [col-key table]
  (map (fn [row] (get row col-key)) table))

(defn average [column]
  (/ (reduce + column) (count column)))

(defn square [x] (* x x))

(defn std [a]
  (Math/sqrt (/ (reduce + (map square (map - a (repeat (average a)))))
                (- (count a) 1 ))))

(defn save-json
  "Writes the provided Vega-Lite JSON to a file in the charts directory with the
  provided prefix."
  [file-prefix vl-json]
  (let [file-path (str "spreadsheets/out/charts/" file-prefix ".vl.json")]
    (io/make-parents file-path)
    (spit file-path vl-json)))

(defn column-subset [data columns]
  (let [row-subset (fn [row] (select-keys row columns))]
    (map row-subset data)))

(defn almost-equal?
  "Returns true if scalars `a` and `b` are approximately equal. Takes a difference
  metric (presumably from `inferdb.metrics`) as its second argument."
  [a b difference-metric threshold]
  (< (difference-metric a b) threshold))

(defn almost-equal-vectors?
  "Returns true if vectors `a` and `b` are approximately equal. Takes a difference
  metric (presumably from `inferdb.metrics`) as its second argument."
  [a b difference-metric threshold]
  (assert (count a) (count b))
  (let [call-almost-equal
        (fn [i] (almost-equal?  (nth a i) (nth b i) difference-metric threshold))]
  (all? (map call-almost-equal (range (count a))))))

(defn within-factor? [a b factor]
  (<= (/ b factor) a (* b factor)))

(defn probability-for-categories [sample-vector]
  (let [fraction (fn [item] {(first (vals (first item)))
                                 (float (/ (second item)
                                           (count sample-vector)))})
        occurences (frequencies sample-vector)]
    (apply merge (mapv fraction occurences))))

(defn probability-vector [samples possible-values]
  (let [probability-map (probability-for-categories samples)]
    (map #(get probability-map % 0)
         possible-values)))

(defn equal-sample-values [samples-1 samples-2]
  (= (map (comp set vals) samples-1)
     (map (comp set vals) samples-2)))

(defn max-index
  "Returns the index of the maximum value in the provided vector."
  [xs]
  (first (apply max-key second (map-indexed vector xs))))
