(ns inferdb.spreadsheets.build-model
  (:require [clojure.pprint :as pprint]
            [inferdb.multimixture.specification :as spec]))

(defn make-model [model]
  ['(ns inferdb.spreadsheets.model)

   `(def ~'spec ~(spec/parse-json model))])

(defn write-model [model fn]
  (with-open [w (clojure.java.io/writer fn)]
    (doseq [form (->> model make-model (interpose nil))]
      (if form
        (pprint/pprint form w)
        (.write w "\n")))))
