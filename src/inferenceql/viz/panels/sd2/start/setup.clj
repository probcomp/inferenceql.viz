(ns inferenceql.viz.panels.sd2.start.setup
  "Defs for creating supporting files to be compiled into app."
  (:require [clojure.spec.alpha :as s]
            [clojure.edn :as edn]
            [clojure.walk :refer [postwalk]]
            [medley.core :as medley]
            [inferenceql.viz.config :as config]
            [clojure.pprint :refer [pprint]]))

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

(def all-gene-time-series
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
                                   thing))))]
    (zipmap header timepoints)))

(def gene-selection-list-rec
  (let [final-levels (medley/map-vals last all-gene-time-series)
        ret (for [[gene-name value] final-levels]
              (if ((:rec gene-filter) gene-name)
                [value gene-name true]
                nil))]
    (filter some? ret)))

(def gene-selection-list-not-rec
  (let [final-levels (medley/map-vals last all-gene-time-series)
        ret (for [[gene-name value] final-levels]
              (if ((:not-rec gene-filter) gene-name)
                [value gene-name false]
                nil))]
    (filter some? ret)))

(def plot-data-rec
  (let [rec-gene-time-series (medley/filter-keys (:rec gene-filter) all-gene-time-series)
        rec-flattened (flatten-time-series rec-gene-time-series)
        ret  (map #(conj % :rec) rec-flattened)]
    (map (fn [[gene time expr-level status]]
           ;; Time is actually invervals of 12 min.
           (let [time (* time 12)]
             {:gene gene :time time :expr-level expr-level :status status}))
         ret)))

(def plot-data-not-rec
  (let [not-rec-gene-time-series (medley/filter-keys (:not-rec gene-filter) all-gene-time-series)
        not-rec-flattened (flatten-time-series not-rec-gene-time-series)
        ret (map #(conj % :not-rec) not-rec-flattened)]
    (map (fn [[gene time expr-level status]]
           ;; Time is actually invervals of 12 min.
           (let [time (* time 12)]
             {:gene gene :time time :expr-level expr-level :status status}))
         ret)))

(defn write-files []
  (spit "resources/gene-extra-info/gene-selection-list-rec.edn"
        (with-out-str (pprint (vec gene-selection-list-rec))))
  (spit "resources/gene-extra-info/gene-selection-list-not-rec.edn"
        (with-out-str (pprint (vec gene-selection-list-not-rec))))
  (spit "resources/gene-extra-info/plot-data-rec.edn"
        (with-out-str (pprint (vec plot-data-rec))))
  (spit "resources/gene-extra-info/plot-data-not-rec.edn"
        (with-out-str (pprint (vec plot-data-not-rec)))))
