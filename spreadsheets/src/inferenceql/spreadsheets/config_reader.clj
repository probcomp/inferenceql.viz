(ns inferenceql.spreadsheets.config-reader
  (:refer-clojure :exclude [read])
  (:import [java.io FileNotFoundException])
  (:require [clojure.data.csv :as csv]
            [clojure.data.json :as json]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [aero.core :as aero]
            [inferenceql.inference.multimixture.specification :as spec]
            [medley.core :as medley]))

(defn keywordize-columns
  "Keywordizes columns names in a multi-mixture spec, `spec`."
  [spec]
  (let [keywordize (fn [a-map] (medley/map-keys keyword a-map))] ; A non-recursive keywordize.
    (-> spec
        (update :vars keywordize)
        (update :views (fn [views]
                         (vec (for [column-partition views]
                                (vec (for [cluster column-partition]
                                       (update cluster :parameters keywordize))))))))))

(defmethod aero/reader 'maybe-include
  [{:keys [profile]} _ s]
  (try (aero.core/read-config s {:resolver aero/root-resolver
                                 :profile profile})
       (catch FileNotFoundException _
         nil)))

(defmethod aero/reader 'csv
  [_ _ s]
  (-> s (io/resource) (slurp) (csv/read-csv) (vec)))

(defmethod aero/reader 'edn
  [_ _ s]
  (-> s (io/resource) (slurp) (edn/read-string)))

(defmethod aero/reader 'json
  [_ _ s]
  (-> s (io/resource) (slurp) (json/read-str)))

(defmethod aero/reader 'model-edn
  [{:keys [profile]} _ s]
  ;; Only read the model file if the config is read with the :app profile.
  ;; Otherwise leave it as a string.
  (if (= profile :app)
    (-> s (io/resource) (slurp) (json/read-str) (spec/from-json) (keywordize-columns))
    s))

(defmethod aero/reader 'export-json
  [{:keys [profile]} _ s]
  ;; Only read the bayes-db-export file if the config is read with the :model-builder profile.
  (when (= profile :model-builder)
    (-> s (io/resource) (slurp) (json/read-str))))

(defmacro read
  "Loads the app config based on `profile`."
  [profile]
  (aero.core/read-config (io/resource "config.edn") {:profile profile}))