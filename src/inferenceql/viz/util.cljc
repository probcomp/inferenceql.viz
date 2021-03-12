(ns inferenceql.viz.util
  (:require [medley.core :as medley]
            [lambdaisland.uri :refer [query-map]]
            [clojure.edn :as edn]
            [inferenceql.inference.gpm]
            [inferenceql.inference.gpm.crosscat :as xcat]
            [inferenceql.inference.gpm.view :as view]
            [inferenceql.inference.gpm.column :as column]
            [inferenceql.inference.gpm.primitive-gpms.categorical :as categorical]
            [inferenceql.inference.gpm.primitive-gpms.gaussian :as gaussian]))

(defn filter-nil-kvs [a-map]
  (into {} (remove (comp nil? val) a-map)))

(defn abs
  "Helper function for calling the math absolute value function"
  [n]
  #?(:clj  (Math/abs (double n))
     :cljs (js/Math.abs n)))

(defn assoc-or-dissoc-in [m ks v]
  "Takes a map `m` and associates key-sequence `ks` with `v`.
  If `v` in nil however, this instead dissocates the keysequence `ks` in `m`."
  (if (some? v)
    (assoc-in m ks v)
    (medley/dissoc-in m ks)))

(defn query-string-params
  "Returns a map of the app's query parameters as specified in the app's URL."
  []
  (let [app-url #?(:cljs (.-location js/window)
                   :clj nil)]
    (query-map app-url {:multikeys :never})))

(defn read-xcat-string
  "Returns an XCat record given an edn string export of an XCat record."
  [edn-string]
  (edn/read-string {:readers {'inferenceql.inference.gpm.primitive_gpms.gaussian.Gaussian
                              gaussian/map->Gaussian

                              'inferenceql.inference.gpm.column.Column
                              column/map->Column

                              'inferenceql.inference.gpm.view.View
                              view/map->View

                              'inferenceql.inference.gpm.primitive_gpms.categorical.Categorical
                              categorical/map->Categorical

                              'inferenceql.inference.gpm.crosscat.XCat
                              xcat/map->XCat}}
                   edn-string))
