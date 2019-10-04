(ns inferdb.spreadsheets.io
  (:require [clojure.java.io :as io]))

(defmacro inline-resource
  "Inlines the contents of the named resource as a string."
  [n]
  (slurp (io/resource n)))
