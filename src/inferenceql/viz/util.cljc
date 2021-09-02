(ns inferenceql.viz.util
  (:require [medley.core :as medley]
            [lambdaisland.uri :refer [query-map]]
            [clojure.string :as str]))

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

(defn coerce-bool [val]
  (case (str/lower-case (str val))
    "true" true
    "t" true
    "false" false
    "f" false
    nil))

(defn keywordize-kv [a-map]
  "Returns `a-map` with both keys and values keywordized."
  (medley/map-kv (fn [col type] [(keyword col) (keyword type)])
                 a-map))
