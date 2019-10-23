(ns inferenceql.spreadsheets.config
  (:import [java.io FileNotFoundException])
  (:require [clojure.data.csv :as csv]
            [clojure.data.json :as json]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [aero.core :as aero]))

(defmethod aero/reader 'maybe-include
  [_ _ s]
  (try (aero.core/read-config s {:resolver aero/root-resolver})
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

(defmacro read-config
  [s]
  (aero.core/read-config (io/resource s)))

(def config (read-config "config.edn"))
