(ns inferenceql.spreadsheets.config-reader
  (:refer-clojure :exclude [read])
  (:import [java.io FileNotFoundException])
  (:require [clojure.data.csv :as csv]
            [clojure.data.json :as json]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [aero.core :as aero]))

(defmethod aero/reader 'txt
  [_ _ s]
  (-> s (io/resource) (slurp)))

(defmethod aero/reader 'csv
  [_ _ s]
  (-> s (io/resource) (slurp) (csv/read-csv) (vec)))

(defmethod aero/reader 'edn
  [_ _ s]
  (-> s (io/resource) (slurp) (edn/read-string)))

(defmethod aero/reader 'json
  [_ _ s]
  (-> s (io/resource) (slurp) (json/read-str)))

(defmethod aero/reader 'export-json
  [_ _ s]
  (-> s (io/resource) (slurp) (json/read-str)))

(defmacro read
  "Loads the app config."
  []
  (aero.core/read-config (io/resource "config.edn")))
