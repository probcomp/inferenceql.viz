(ns inferenceql.search-by-example.main
  (:require [inferenceql.multimixture.dsl :as mmix]
            [metaprob.prelude :as prelude]
            [metaprob.trace :as trace]))

(defn- kli [p-in q-in]
  (let [p (max 1E-300 p-in)
        q (max 1E-300 q-in)]
    (* p (Math/log (/ p q)))))

(defn kl
  "K-L divergence between two vectors of floating point numbers."
  [ps qs]
  (apply + (map kli ps qs)))

(defn symmetrized-kl
  [ps qs]
  (+ (kl ps qs)
     (kl qs ps)))

(defn constrain-by-row
  "Constrains the given trace such that the values chosen for each
  column are the ones in the provided row."
  [trace row]
  (reduce (fn [trace [column value]]
            (trace/trace-set-value trace column value))
          trace
          row))

(defn constrain-by-view
  "Constrains the given trace with such that the view chosen is the
  one provided."
  [trace cluster view]
  (trace/trace-set-value trace (mmix/view-cluster-address view) cluster))

(defn constrain-by-cluster
  "Constrains the given trace such that the cluster chosen is the one
  provided."
  [trace cluster columns]
  (reduce (fn [trace column]
            (trace/trace-set-value trace
                                   (mmix/column-cluster-address column)
                                   cluster))
          trace
          columns))

(defn normalize
  "Normalizes a vector of numbers such that they sum to 1."
  [ns]
  (let [sum (apply + ns)]
    (mapv #(/ % sum)
          ns)))

(defn probability-distribution-on-cluster
  [model clusters row]
  ;; WARNING: Does not handle views yet. Assumes a single view.
  (let [columns (-> clusters second keys)
        cluster-addresses (range (mmix/cluster-count clusters))]
    (->> cluster-addresses
         (map (fn [cluster-address]
                (let [trace (-> {}
                                (constrain-by-cluster cluster-address columns)
                                (constrain-by-row row))
                      [_ _ score] (prelude/infer-and-score :procedure model
                                                           :observation-trace trace)]
                  (prelude/exp score))))
         (normalize))))

(defn rowwise-similarity
  [cgpm clusters example-pfca row]
  ;; WARNING: Does not handle views yet. Assumes a single view
  (kl example-pfca (probability-distribution-on-cluster cgpm clusters row)))

(defn search
  [cgpm clusters rows example]
  (let [example-pfca (probability-distribution-on-cluster (:proc cgpm) clusters example)]
    (->> rows
         (map-indexed (fn [index row]
                        [index (rowwise-similarity (:proc cgpm) clusters example-pfca row)]))
         (sort-by second))))

(defn cached-search
  [cgpm clusters pfcas example]
  (let [example-pfca (probability-distribution-on-cluster (:proc cgpm) clusters example)]
    (->> pfcas
         (map-indexed (fn [index pfca]
                        [index (symmetrized-kl example-pfca pfca)]))
         (sort-by second))))

#?(:clj (defn save-pfcas
          [filename ns model clusters rows]
          (let [data (mapv #(probability-distribution-on-cluster model clusters %)
                           rows)

                sexp (with-out-str
                       (pr `(~'ns ~ns))
                       (pr `(~'def ~'pfcas ~data)))]
            (spit filename sexp))))
