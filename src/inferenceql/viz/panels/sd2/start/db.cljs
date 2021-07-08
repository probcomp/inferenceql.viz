(ns inferenceql.viz.panels.sd2.start.db
  (:require [clojure.spec.alpha :as s]
            [clojure.edn :as edn]
            [clojure.walk :refer [postwalk]]
            [medley.core :as medley]
            [inferenceql.viz.config :as config]))

(defn transpose [m]
  (apply mapv vector m))

(defn flatten-time-series [a-map]
  (mapcat (fn [[gene-name datapoints]]
            (map-indexed (fn [i d]
                           [gene-name i d])
                         datapoints))
          a-map))

(def gene-filter
  (let [header (first (:gene-recs config/config))
        rows (rest (:gene-recs config/config))
        rec (set (map (comp keyword first) rows))
        not-rec (set (map (comp keyword second) rows))]
    {:rec rec :not-rec not-rec}))

(def plot-data
  (let [header (map keyword (first (:gene-growth-curves config/config)))
        rows (rest (:gene-growth-curves config/config))
        timepoints (->> rows
                     transpose
                     ;; Take first 60 datapoints.
                     (mapv #(take 60 %))
                     ;; Convert all datapoints to numbers.
                     (postwalk (fn [thing]
                                 (if (string? thing)
                                   (edn/read-string thing)
                                   thing))))
        all-gene-time-series (zipmap header timepoints)

        rec-gene-time-series (medley/filter-keys (:rec gene-filter) all-gene-time-series)
        not-rec-gene-time-series (medley/filter-keys (:not-rec gene-filter) all-gene-time-series)

        rec-flattened (flatten-time-series rec-gene-time-series)
        not-rec-flattened (flatten-time-series not-rec-gene-time-series)

        ret (concat (map #(conj % :not-rec) not-rec-flattened)
                    (map #(conj % :rec) rec-flattened))]
    (map (fn [[gene time expr-level status]]
           {:gene gene :time time :expr-level expr-level :status status})
         ret)))

(def default-db
  {}
  #_{:sd2-start-panel {:gene-recs (:gene-recs config/config)
                       :gene-growth-curves (:gene-growth-curves config/config)}})

(s/def ::sd2-start-panel (s/keys :req-un []))
