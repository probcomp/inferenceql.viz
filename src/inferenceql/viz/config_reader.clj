(ns inferenceql.viz.config-reader
  (:refer-clojure :exclude [read])
  (:require [clojure.data.csv :as csv]
            [clojure.data.json :as json]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [aero.core :as aero]
            [inferenceql.inference.gpm]
            [inferenceql.inference.gpm.column :as column]
            [inferenceql.inference.gpm.crosscat :as xcat]
            [inferenceql.inference.gpm.primitive-gpms.categorical :as categorical]
            [inferenceql.inference.gpm.primitive-gpms.gaussian :as gaussian]
            [inferenceql.inference.gpm.view :as view]))

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

(defmethod aero/reader 'xcat-model
  [_ _ s]
  (->> s
       (io/resource)
       (slurp)
       (edn/read-string {:readers {'inferenceql.inference.gpm.primitive_gpms.gaussian.Gaussian
                                   gaussian/map->Gaussian

                                   'inferenceql.inference.gpm.column.Column
                                   column/map->Column

                                   'inferenceql.inference.gpm.view.View
                                   view/map->View

                                   'inferenceql.inference.gpm.primitive_gpms.categorical.Categorical
                                   categorical/map->Categorical

                                   'inferenceql.inference.gpm.crosscat.XCat
                                   xcat/map->XCat}})))

(defmacro read
  "Loads the app config."
  []
  (aero.core/read-config (io/resource "config.edn")))
