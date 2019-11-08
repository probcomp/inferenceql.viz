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

(defn- transform-qualified-variable
  [model variable]
  ;; TODO: Assume only one model for now
  variable)

(defn- transform-select
  [& args]
  (fn [env]
    (let [[_ table] (find-node :from args)
          [_ what-f] (find-node :what args)
          what-xf (what-f env)
          where-xf (if-let [[_ where-xf] (find-node :where args)]
                     where-xf
                     (map identity))
          limit-xf (if-let [[_ limit] (find-node :limit args)]
                     (take limit)
                     (map identity))
          xf (comp where-xf what-xf limit-xf)
          rows (get env table)]
      (into [] xf rows))))

(defn transform-result-column
  ([column]
   {:name column
    :func (fn [env]
            #(get % column))})
  ([table column]
   {:name column
    :func (fn [env]
            #(get % column))}))

(defn- transform-probability
  [& form]
  (match form
    ([[:of of]
      [:given given]
      [:using model-sym]] :seq)
    {:name (str (gensym)) ; TODO What/how to name this?
     :func (fn [env]
             (let [model (get env model-sym)]
               (assert (some? model))
               (fn probability [row]
                 ;; TODO: Allow `given` to refer to row variables
                 (mp/exp (mmix.basic-queries/logpdf model of given)))))}))

(defn transform-what [& result-column-transformers]
  [:what
   (fn [env]
     (let [f (reduce (fn [acc {:keys [name func]}]
                       (let [f (func env)]
                         (fn [row]
                           (assoc (acc row)
                                  name
                                  (f row)))))
                     (constantly {})
                     result-column-transformers)]
       (map f)))])

(defn transform-where
  [& form]
  (match (vec form)
    [[:predicate {:name column} [:comparator c] value]]
    (let [comp-f (case c
                   "=" =
                   "!=" not=)]
      [:where (filter #(comp-f (get % column) value))])))

(def ^:private transform-map
  {:select transform-select
   :where transform-where

   :what transform-what
   :result-column transform-result-column
   :probability transform-probability

   :events merge
   :event hash-map
   :qualified-variable transform-qualified-variable

   :model str
   :table str

   :column str
   :variable str

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
