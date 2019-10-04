(ns inferdb.spreadsheets.data
  (:require [clojure.string :as str]
            #?(:clj [inferdb.spreadsheets.io :as sio])
            #?(:clj [clojure.data.csv :as csv]
               :cljs [goog.labs.format.csv :as csv]))
  #?(:cljs (:require-macros [inferdb.spreadsheets.io :as sio])))

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
  (->> (sio/inline-resource "data.csv")
       #?(:clj (csv/read-csv) :cljs (csv/parse))
       (mapv fix-row)
       (csv-data->maps)))
