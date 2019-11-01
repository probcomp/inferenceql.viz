(ns inferenceql.spreadsheets.data
  (:require [clojure.string :as str]
            [inferenceql.spreadsheets.config :as config]))

(defn- float-string?
  "Returns true if `s` is a string containing only a number with a decimal, false
  otherwise."
  [s]
  (some? (re-matches #"\d+\.\d+" s)))

(defn parse-float
  "Returns `s` as a float if it can be parsed, otherwise `nil`."
  [s]
  #?(:clj (try (Float/parseFloat s)
               (catch NumberFormatException _
                 nil))
     :cljs (let [n (js/parseFloat s)]
             (when-not (js/Number.isNaN n)
               n))))

(defn- clean-items [v]
  (cond
    ;; real vals
    (float-string? v)
    (parse-float v)

    ;; empty strings
    (str/blank? v) nil

    ;; anything else is identity
    :else v))

(defn fix-row [r]
  (mapv clean-items r))

(defn csv-data->maps
  [csv-data]
  (mapv #(zipmap (map str (first csv-data))
                 %)
        (rest csv-data)))

(def nyt-data
  (->> (:data config/config)
       (mapv fix-row)
       (csv-data->maps)))

(comment
  (def stack-overflow-data
    (->> (:stack-overflow config/config)
         (mapv fix-row)
         (csv-data->maps)))

  #?(:cljs (.log js/console (first (:data config/config))))
  #?(:cljs (.log js/console (first (:stack-overflow config/config))))
  #?(:cljs (.log js/console (keys config/config))))

  ;(print (first (:data config/config)))
  ;(print (first (:stack-overflow config/config)))
