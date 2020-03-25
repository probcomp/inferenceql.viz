(ns inferenceql.spreadsheets.query-test
  (:require [clojure.string :as string]
            [clojure.test :as test :refer [are deftest is]]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [com.gfredericks.test.chuck.generators :as chuck.gen]
            [instaparse.core :as insta]
            [inferenceql.spreadsheets.query :as query]))

;;; Generators

(defn gen-row
  "Returns a generator that will generate individual \"rows\" (maps). Arguments to
  this function are like `gen/hash-map`, but this function accepts a map instead
  of a sequence of key/value pairs."
  [columns]
  (gen/bind (chuck.gen/sub-map columns)
            #(apply gen/hash-map (mapcat identity %))))

(def gen-table
  "Generator for full \"tables\" (vectors of maps). Each row will have keys drawn
  from a consistent subset, and the values for each key will be drawn from a
  fixed generator."
  (let [gen-column (gen/fmap keyword (chuck.gen/string-from-regex #"[a-zA-Z]\w*"))
        value-generators [gen/small-integer gen/nat gen/int]]
    (gen/bind (gen/map gen-column (gen/elements value-generators))
              (comp gen/vector gen-row))))

;;; Literals

(defn parse-and-transform-literals
  [& args]
  (->> (apply query/parse args)
       (insta/transform query/literal-transformations)))

(defspec nat-parsing
  (prop/for-all [n gen/nat]
    (let [s (pr-str n)]
      (is (= n (parse-and-transform-literals s :start :nat))))))

(defspec int-parsing
  (prop/for-all [n gen/int]
    (let [s (pr-str n)]
      (is (= n (parse-and-transform-literals s :start :int))))))

(defspec float-parsing
  (prop/for-all [n (gen/double* {:infinite? false :NaN? false})]
    (let [s (pr-str n)]
      (is (== n (parse-and-transform-literals s :start :float))))))

;; Parsing success/failure

(deftest parsing-success
  (are [start query] (nil? (insta/get-failure (query/parse query :start start)))
    :query "SELECT * FROM data"))

(deftest parsing-failure
  (are [start query] (some? (insta/get-failure (query/parse query :start start)))
    :query "123abc"))

;; Features

(defspec select-star
  (prop/for-all [table gen-table]
    (let [results (query/q "SELECT * FROM data" table)]
      (is (= results table)))))

(def gen-table-limit
  (gen/bind gen-table
            #(gen/tuple (gen/return %)
                        (gen/choose 0 (count %)))))

(defspec select-limit
  (prop/for-all [[table n] gen-table-limit]
    (let [results (query/q (str "SELECT * FROM data LIMIT " n) table)]
      (is (= results (take n table))))))

(def gen-table-col-subset
  (gen/bind (gen/such-that #(seq (mapcat keys %))
                           gen-table)
            #(gen/tuple (gen/return %)
                        (gen/not-empty
                         (chuck.gen/subset (mapcat keys %))))))

(defspec select-col
  (prop/for-all [[table ks] gen-table-col-subset]
    (let [cols (->> ks (map name) (string/join ", "))
          results (query/q (str "SELECT " cols " FROM data") table)]
      (is (= results (map #(select-keys % ks)
                          table))))))

(def gen-table-col
  (gen/bind (gen/such-that #(seq (mapcat keys %))
                           gen-table)
            #(gen/tuple (gen/return %)
                        (gen/elements (mapcat keys %)))))

(defspec conditions-not-null
  (prop/for-all [[table k] gen-table-col]
    (let [results (query/q (str "SELECT * FROM data WHERE " (name k) " IS NOT NULL") table)]
      (is (= results (remove (comp nil? k) table))))))

(defspec conditions-null
  (prop/for-all [[table k] gen-table-col]
    (let [results (query/q (str "SELECT * FROM data WHERE " (name k) " IS NULL") table)]
      (is (= results (filter (comp nil? k) table))))))
