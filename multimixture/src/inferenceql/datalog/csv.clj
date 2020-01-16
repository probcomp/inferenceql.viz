(ns inferenceql.datalog.csv
  (:require [clojure.data.csv :as csv]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [jsonista.core :as json]
            [inferenceql.datalog.table :as table]
            [inferenceql.multimixture.specification :as spec]))

(defn mappify
  [vs ks]
  (zipmap ks vs))

(defn nullify
  [m & xs]
  (into (empty m)
        (remove #(contains? (set xs) (val %)))
        m))

(defn key-rename
  [m kmap]
  (clojure.set/rename-keys m kmap))

(defn coerce
  [m coercers]
  (into (empty m)
        (map (juxt key #(let [f (get coercers (key %) identity)]
                          (f (val %)))))
        m))

(defn spec-coercers
  [mmix]
  (let [stattype-coercers {:iql.stattype/gaussian edn/read-string
                           :iql.stattype/categorical str}]
    (reduce (fn [acc variable]
              (let [stattype (spec/stattype mmix variable)]
                (if-let [coercer (get stattype-coercers stattype)]
                  (assoc acc variable coercer)
                  acc)))
            {}
            (spec/variables mmix))))

(defn spec-file-coercers
  [path]
  (-> (slurp path)
      (json/read-value)
      (spec/from-json)
      (spec-coercers)))

(defn facts
  [csv-path spec-path column-idents]
  {:pre [(.isFile (io/file csv-path))
         (.isFile (io/file spec-path))]}
  (with-open [reader (io/reader csv-path)]
    (let [[headers & rows] (csv/read-csv reader)]
      (assert (= (set headers) (set (keys column-idents))))
      (into []
            (map #(-> %
                      (mappify headers)
                      (nullify "NaN" "null" nil)
                      (coerce (spec-file-coercers spec-path))
                      (table/row-fact)))
            rows))))

(comment
  (require '[clojure.spec.alpha :as spec])

  (facts "/Users/zane/Desktop/satellites.csv"
         "/Users/zane/Downloads/satellites/multimix-0.json"
         )

  (set! *print-length* 10)




  )


#_(set! *print-length* 10)
