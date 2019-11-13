(ns inferenceql.spreadsheets.query
  #?(:clj (:require [inferenceql.spreadsheets.io :as sio])
     :cljs (:require-macros [inferenceql.spreadsheets.io :as sio]))
  (:require [clojure.core.match :refer [match]]
            [clojure.edn :as edn]
            [instaparse.core :as insta]
            [metaprob.prelude :as mp]
            [inferenceql.multimixture :as mmix]
            [inferenceql.multimixture.basic-queries :as mmix.basic-queries]
            [inferenceql.multimixture.search  :as search]
            [inferenceql.spreadsheets.model :as model]))

(def parser (insta/parser (sio/inline-resource "query.bnf")))
(def ^:private row-generator (mmix/row-generator model/spec))

(defn- find-node [xs k]
  (some #(when (and (coll? %)
                    (= k (first %)))
           %)
        xs))

(defn- transform-qualified-variable
  [model variable]
  ;; TODO: Assume only one model for now
  variable)

(defn- transform-select
  [& args]
  (fn [env]
    (let [[_ from-f] (find-node args :from)
          [_ what-f] (find-node args :what)
          what-xf (what-f env)
          where-xf (if-let [[_ where-f] (find-node args :where)]
                     (where-f env)
                     (map identity))
          limit-xf (if-let [[_ limit] (find-node args :limit)]
                     (take limit)
                     (map identity))
          xf (comp where-xf what-xf limit-xf)
          from (from-f env)]
      (into [] xf from))))

(defn- transform-star
  []
  (constantly identity))

(defn- transform-result-column
  ([column]
   (fn [_env]
     (fn [row]
       {column (get row column)})))
  ([_table column] ; TODO: joins
   (fn [_env]
     (fn [row]
       {column (get row column)}))))

(defn- transform-probability
  [& form]
  (match form
    ([[:of of]
      [:given given]
      [:using model-sym]] :seq)
    (fn [env]
      (let [model (get env model-sym)]
        (assert (some? model))
        (fn probability [_row] ; TODO: joins
          {(str (gensym))
           (mp/exp (mmix.basic-queries/logpdf model of given))})))))

(defn- transform-from-table
  [table]
  (fn [env]
    (get env table)))

(defn- transform-generate
  [& form]
  (let [[_ & qualified-variables] (find-node form :qualified-variables)
        [_ given] (find-node form :given)
        [_ model-sym] (find-node form :using)]
    (fn [env]
      (let [model (get env model-sym)]
        (assert (some? model))
        (repeatedly #(-> (mmix.basic-queries/simulate model given 1)
                         (first)
                         (select-keys qualified-variables)))))))

(defn transform-what [& result-column-transformers]
  [:what
   (fn [env]
     (let [f (reduce (fn [acc make-f]
                       (let [f (make-f env)]
                         (fn [row]
                           (merge (acc row)
                                  (f row)))))
                     (constantly {})
                     result-column-transformers)]
       (map f)))])

(defn transform-where
  [& form]
  (match (vec form)
    [[:predicate make-f [:comparator c] value]]
    (let [comp-f (case c
                   "=" =
                   "!=" not=)]
      [:where (fn [env]
                (let [f (make-f env)]
                  (filter #(comp-f (-> % f vals first) value))))])))

(def ^:private transform-map
  {:select transform-select
   :what transform-what
   :where transform-where

   ;; what
   :star transform-star
   :probability transform-probability

   ;; from
   :from-table transform-from-table
   :generate transform-generate

   ;; where
   :events merge
   :event hash-map

   :result-column transform-result-column
   :column str

   :qualified-variable transform-qualified-variable
   :variable str

   :model str
   :table str

   :string edn/read-string
   :symbol edn/read-string
   :nat edn/read-string
   :int edn/read-string
   :float edn/read-string
   :null (constantly nil)})

(defn parse
  "Parses a string containing an InferenceQL query and produces a map representing
  the query to be performed."
  [& args]
  (let [ast (apply parser args)]
    (insta/transform transform-map ast)))
