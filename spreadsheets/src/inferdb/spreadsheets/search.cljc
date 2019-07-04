(ns inferdb.spreadsheets.search
  (:require [inferdb.search-by-example.main :as sbe]
            [inferdb.cgpm.main :as cgpm]
            [inferdb.spreadsheets.data :as data]
            [inferdb.spreadsheets.model :as model]
            [inferdb.spreadsheets.pfcas :as pfcas]))

(defn rank
  "Returns the rank of `x` in the collection of values `ys`."
  [x ys]
  (or (->> ys
           (sort <)
           (map-indexed vector)
           (some (fn [[index y]]
                   (when (> y x)
                     index))))
      (count ys)))

(def anomalousness-threshold 0.05)

(defn very-anomalous?
  "Returns `true` if the provided example is anomalous given the values in
  `inferdb.spreadsheets.data`."
  ([example]
   (very-anomalous? example anomalousness-threshold))
  ([example threshold]
   (let [example-logpdf (cgpm/cgpm-logpdf model/model-cgpm
                                          ;; target - probability of this target
                                          example
                                          ;; constraint - given that we know these things
                                          {}
                                          ;; input-address variables
                                          {})
         data-logpdfs (->> data/nyt-data
                           ;; Remove rows where any of the keys in the example map
                           ;; to `nil`.
                           (remove (fn [row]
                                     (some (fn [key]
                                             (and (contains? row key)
                                                  (nil? (get row key))))
                                           (keys example))))
                           ;; Include from the row only the keys that are present
                           ;; in the example.
                           (map (fn [row]
                                  (->> (keys example)
                                       (map name)
                                       (select-keys row)
                                       (reduce-kv (fn [row k v]
                                                    (assoc row (keyword k) v))
                                                  {}))))
                           (map #(cgpm/cgpm-logpdf model/model-cgpm % {} {})))
         percent-rank (/ (rank example-logpdf data-logpdfs)
                         (count data-logpdfs))]
     (< percent-rank threshold))))

(defn search-by-example [example emphasis _]
  (sbe/cached-search model/model-cgpm
                     model/cluster-data
                     pfcas/pfcas
                     example
                     emphasis))
