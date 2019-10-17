(ns inferenceql.spreadsheets.model
  #?(:cljs (:require-macros [inferenceql.spreadsheets.io :as sio]))
  (:require [inferenceql.multimixture.specification :as spec]
            #?(:clj [clojure.data.json :as json])
            #?(:clj [inferenceql.spreadsheets.io :as sio])))

(defn parse-json
  [s]
  #?(:clj (json/read-str s)
     :cljs (-> s
               (js/JSON.parse)
               (js->clj))))

(def spec
  (-> (sio/inline-resource "model.json")
      (parse-json)
      (spec/from-json)))
