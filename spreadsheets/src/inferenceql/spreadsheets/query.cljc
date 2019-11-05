(ns inferenceql.spreadsheets.query
  #?(:clj (:require [inferenceql.spreadsheets.io :as sio])
     :cljs (:require-macros [inferenceql.spreadsheets.io :as sio]))
  (:require [clojure.core.match :refer [match]]
            [clojure.edn :as edn]
            [instaparse.core :as insta]
            [metaprob.prelude :as mp]
            [inferenceql.multimixture :as mmix]
            [inferenceql.spreadsheets.model :as model]))

(def parser (insta/parser (sio/inline-resource "query.bnf")))
(def ^:private row-generator (mmix/row-generator model/spec))

(defn- transform-from
  ([limit]
   (transform-from {} limit))
  ([obs limit]
   [:generate obs limit]))

(defn- find-node [k xs]
  (some #(when (and (coll? %)
                    (= k (first %)))
           %)
        xs))

(defn- transform-select
  [& args]
  (let [[_ table] (find-node :from args)
        [_ what-xf] (find-node :what args)
        where-xf (if-let [[_ where-xf] (find-node :where args)]
                   where-xf
                   (map identity))
        limit-xf (if-let [[_ limit] (find-node :limit args)]
                   (take limit)
                   (map identity))]
    {:table table
     :xform (comp where-xf what-xf limit-xf)}))

(defn transform-result-column
  ([column]
   (let [col-symbol (symbol (str column))]
     {:name col-symbol :func #(get % col-symbol)}))
  ([table column]
   (let [col-symbol (symbol (str table) (str column))]
     {:name col-symbol :func #(get % col-symbol)})))

(defn transform-what [& result-column-transformers]
  (let [f (reduce (fn [next-f {:keys [name func]}]
                    (fn [row]
                      (assoc (next-f row)
                             name
                             (func row))))
                  (constantly {})
                  result-column-transformers)]
    [:what (map f)]))

(defn transform-where
  [& stuff]
  (match (vec stuff)
    [[:predicate {:name column} [:comparator c] value]]
    (let [comp-f (case c
                   "=" =
                   "!=" not=)]
      [:where (filter #(comp-f (get % column) value))])))

(def ^:private transform-map
  {:select transform-select
   :what transform-what
   :where transform-where
   :result-column transform-result-column
   :column str

   :null (constantly nil)

   :string edn/read-string
   :symbol edn/read-string
   :nat edn/read-string
   :int edn/read-string
   :float edn/read-string})

(defn parse
  "Parses a string containing an InferenceQL query and produces a map representing
  the query to be performed."
  [& args]
  (let [ast (apply parser args)]
    (insta/transform transform-map ast)))

(defn execute
  [{:keys [xform table]} env]
  (into [] xform (get env table)))
