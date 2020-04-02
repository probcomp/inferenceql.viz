(ns inferenceql.spreadsheets.data
  (:require [clojure.string :as str]
            [medley.core :as medley]
            [inferenceql.spreadsheets.config :as config]))


(defn- float-string?
  "Returns true if `s` is a string containing only a number with a decimal, false
  otherwise."
  [s]
  (some? (re-matches #"^\d+\.\d+$" s)))

(defn parse-float
  "Returns `s` as a float if it can be parsed, otherwise `nil`."
  [s]
  #?(:clj (try (Float/parseFloat s)
               (catch NumberFormatException _
                 nil))
     :cljs (let [n (js/parseFloat s)]
             (when-not (js/Number.isNaN n)
               n))))

(defn- cast-items-in-row [row]
  "Casts vals in a map `row` based on their type as defined by `column-types`."
  (let [column-types (get (:model config/config) :vars)]
    (medley/map-kv (fn [k v]
                     (let [type (get column-types k)]
                       (if (and (= type :gaussian) (float-string? v))
                         [k (parse-float v)]
                         [k v])))
                   row)))

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

(def nyt-data
  (->> (:data config/config)
       (mapv clean-items-in-row-vec)
       (csv-data->maps)
       (mapv cast-items-in-row)))
