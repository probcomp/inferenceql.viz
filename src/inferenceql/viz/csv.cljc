(ns inferenceql.viz.csv
  "Functions for processing csv data files."
  (:require [clojure.string :as str]
            [clojure.walk :as walk]
            [medley.core :as medley]))

(defn- numerical-string?
  "Returns true if `s` is a string containing a number, false otherwise."
  [s]
  (when s
    (some? (re-matches #"^\d+(\.\d+)?$" s))))

(defn parse-float
  "Returns `s` as a float if it can be parsed, otherwise `nil`."
  [s]
  #?(:clj (try (Float/parseFloat s)
               (catch NumberFormatException _
                 nil))
     :cljs (let [n (js/parseFloat s)]
             (when-not (js/Number.isNaN n)
               n))))

(defn- cast-items-in-row [column-types row]
  "Casts vals in a map `row` based on their type as defined by `column-types`."
  (medley/map-kv (fn [k v]
                   (let [type (get column-types k)]
                     (if (and (= type :gaussian) (numerical-string? v))
                       [k (parse-float v)]
                       [k v])))
                 row))

(defn- clean-items-in-row-vec [row]
  "Cleans string vals in a vector `row`."
  (for [v row]
    (if (str/blank? v)
      nil ; Empty strings.
      v))) ; Anything else is identity.)))

(defn csv-data->maps
  [csv-data]
  (mapv #(zipmap (map str (first csv-data))
                 %)
        (rest csv-data)))

(defn csv-data->clean-maps
  "Takes `csv-data`, a vector of row vectors, and returns a seq of cleaned, casted data in maps.
  Casts data according to types in `column-types`.
  It can also take an options map that can keywordize column names."
  ([column-types csv-data]
   (csv-data->clean-maps column-types csv-data {}))
  ([column-types csv-data {:keys [keywordize-cols] :or {keywordize-cols false}}]
   (let [column-types (cond->> column-types
                               keywordize-cols (walk/keywordize-keys))]
     (cond->> csv-data
       :always (mapv clean-items-in-row-vec)
       :always (csv-data->maps)
       keywordize-cols (mapv walk/keywordize-keys)
       :always (mapv #(cast-items-in-row column-types %))))))
