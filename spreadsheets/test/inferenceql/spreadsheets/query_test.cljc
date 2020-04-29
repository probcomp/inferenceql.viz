(ns inferenceql.spreadsheets.query-test
  (:require [clojure.string :as string]
            [clojure.test :as test :refer [are deftest is testing]]
            [clojure.test.check.clojure-test :refer [defspec]]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [com.gfredericks.test.chuck.generators :as chuck.gen]
            [instaparse.core :as insta]
            [inferenceql.spreadsheets.query :as query]
            [inferenceql.multimixture.search :as search]))

;;; Generators

(defn gen-row
  "Returns a generator that will generate individual \"rows\" (maps). Arguments to
  this function are like `gen/hash-map`, but this function accepts a map instead
  of a sequence of key/value pairs."
  [columns]
  (gen/bind (chuck.gen/sub-map columns)
            #(apply gen/hash-map (mapcat identity %))))

(def string-alpha
  (gen/fmap string/join (gen/vector gen/char-alpha)))

(def gen-column
  (gen/fmap keyword (gen/such-that (comp pos? count) string-alpha)))

(def gen-table
  "Generator for full \"tables\" (vectors of maps). Each row will have keys drawn
  from a consistent subset, and the values for each key will be drawn from a
  fixed generator."
  (let [value-generators [gen/small-integer gen/nat gen/int]]
    (gen/bind (gen/map gen-column (gen/elements value-generators))
              (comp gen/vector gen-row))))

(def gen-table-col
  "Generator for a 2-tuple of table and a column for that table."
  (gen/bind (gen/such-that #(seq (mapcat keys %))
                           gen-table)
            #(gen/tuple (gen/return %)
                        (gen/elements (mapcat keys %)))))

(def gen-table-col-subset
  (gen/bind (gen/such-that #(seq (mapcat keys %))
                           gen-table)
            #(gen/tuple (gen/return %)
                        (gen/not-empty
                         (chuck.gen/subset (mapcat keys %))))))

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


;; Float parsing is a bit different in CLJS. Among other things, whole-numbered
;; floats are printed without a decimal point. Someone should come back and try
;; to make this work in CLJS at some point.
#?(:clj (defspec float-parsing
          (prop/for-all [n (gen/double* {:infinite? false :NaN? false})]
            (let [s (pr-str n)]
              (is (== n (parse-and-transform-literals s :start :float)))))))

;;; Query parsing success/failure

(deftest parsing-success
  (are [start query] (nil? (insta/get-failure (query/parse query :start start)))
    :query "SELECT * FROM data"))

(deftest parsing-failure
  (are [start query] (some? (insta/get-failure (query/parse query :start start)))
    :query "123abc"))

;; Basic selection

(defspec select-star
  (prop/for-all [table gen-table]
    (let [results (query/q "SELECT * FROM data" table)]
      (is (= results table)))))

(defspec select-col
  (prop/for-all [[table ks] gen-table-col-subset]
    (let [cols (->> ks (map name) (string/join ", "))
          results (query/q (str "SELECT " cols " FROM data") table)]
      (is (= results (map #(select-keys % ks)
                          table))))))

;; Order by

(defn ascending?
  "Returns true if the provided collection is sorted in ascending order."
  [coll]
  (->> coll
       (partition 2 1)
       (every? #(<= (apply compare %) 0))))

(defn descending?
  [coll]
  (->> coll (reverse) (ascending?)))

(defspec select-order-by
  (prop/for-all [[table col] gen-table-col]
    (let [query (str "SELECT * FROM data ORDER BY " (name col))]
      (is (->> (query/q query table)
               (map col)
               (ascending?))))))

(defspec select-order-by-asc
  (prop/for-all [[table col] gen-table-col]
    (let [query (str "SELECT * FROM data ORDER BY " (name col) " ASC")]
      (is (->> (query/q query table)
               (map col)
               (ascending?))))))

(defspec select-order-by-desc
  (prop/for-all [[table col] gen-table-col]
    (let [query (str "SELECT * FROM data ORDER BY " (name col) " DESC") ]
      (is (->> (query/q query table)
               (map col)
               (descending?))))))

;; Limit

(def gen-table-limit
  (gen/bind gen-table
            #(gen/tuple (gen/return %)
                        (gen/choose 0 (count %)))))

(defspec select-limit
  (prop/for-all [[table n] gen-table-limit]
    (let [results (query/q (str "SELECT * FROM data LIMIT " n) table)]
      (is (= results (take n table))))))

;; Conditions

(defspec conditions-not-null
  (prop/for-all [[table k] gen-table-col]
    (let [results (query/q (str "SELECT * FROM data WHERE " (name k) " IS NOT NULL") table)]
      (is (= results (remove (comp nil? k) table))))))

(defspec conditions-null
  (prop/for-all [[table k] gen-table-col]
    (let [results (query/q (str "SELECT * FROM data WHERE " (name k) " IS NULL") table)]
      (is (= results (filter (comp nil? k) table))))))

;; Probabilities

(def simple-mmix
  {:vars {:x :categorical
          :y :categorical}
   :views [[{:probability 0.75
             :parameters  {:x {"yes" 1.0 "no" 0.0}
                           :y {"yes" 1.0 "no" 0.0}}}
            {:probability 0.25
             :parameters  {:x {"yes" 0.0 "no" 1.0}
                           :y {"yes" 0.0 "no" 1.0}}}]]})

(deftest probability-of-bindings
  (let [rows [{}]
        models {:model (search/optimized-row-generator simple-mmix)}
        q1 (comp first vals first #(query/q % rows models))]
    (is (= (Math/log 0.25) (q1 "SELECT (PROBABILITY OF x=\"no\"                  UNDER model) FROM data LIMIT 1")))
    (is (= (Math/log 0.75) (q1 "SELECT (PROBABILITY OF x=\"yes\"                 UNDER model) FROM data LIMIT 1")))
    (is (= (Math/log 1.0)  (q1 "SELECT (PROBABILITY OF x=\"yes\" GIVEN y=\"yes\" UNDER model) FROM data LIMIT 1")))
    (is (= (Math/log 1.0)  (q1 "SELECT (PROBABILITY OF x=\"no\"  GIVEN y=\"no\"  UNDER model) FROM data LIMIT 1")))
    (is (= (Math/log 0.0)  (q1 "SELECT (PROBABILITY OF x=\"yes\" GIVEN y=\"no\"  UNDER model) FROM data LIMIT 1")))))

(deftest probability-of-rows
  (let [models {:model (search/optimized-row-generator simple-mmix)}
        q1 (comp first vals first #(query/q %1 %2 models))]
    (are [expected x] (is (= (Math/log expected)
                             (q1 "SELECT (PROBABILITY OF x UNDER model) FROM data"
                                 [{:x x}])))
      0.25 "no"
      0.75 "yes")

    (are [expected x y] (= (Math/log expected)
                           (q1 "SELECT (PROBABILITY OF x GIVEN y UNDER model) FROM data"
                               [{:x x :y y}]))
      1.0 "yes" "yes"
      1.0 "no"  "no"
      0.0 "yes" "no"
      0.0 "no"  "yes")))

(deftest generate-generates-correct-columns
  (testing "Generate"
    (let [model (search/optimized-row-generator simple-mmix)
          q #(query/q % [] {:model model})]
      (testing "with a single variable"
        (doseq [result (q "SELECT * FROM (GENERATE y UNDER model) LIMIT 10")]
          (is (= #{:y} (set (keys result))))))
      (testing "with multiple variables"
        (doseq [result (q "SELECT * FROM (GENERATE x, y UNDER model) LIMIT 10")]
          (is (= #{:x :y} (set (keys result))))))
      (testing "with literal events"
        (testing "but just one"
          (doseq [result (q "SELECT * FROM (GENERATE x GIVEN x=\"yes\" UNDER model) LIMIT 10")]
            (is (= {:x "yes"} (select-keys result [:x])))))
        (testing "and more than one"
          (doseq [result (q "SELECT * FROM (GENERATE x, y GIVEN x=\"yes\", y=\"yes\" UNDER model) LIMIT 10")]
            (is (= {:x "yes", :y "yes"} (select-keys result [:x :y]))))))
      (testing "expressions can have a subset of columns selected from them"
        (doseq [result (q "SELECT y FROM (GENERATE x, y UNDER model) LIMIT 10")]
          (is (= [:y] (keys result)))))
      (testing "can be nested"
        (doseq [result (q "SELECT x, y FROM (GENERATE x, y UNDER (GENERATE x, y UNDER model)) LIMIT 10")]
          (is (= #{:x :y} (set (keys result)))))))))
