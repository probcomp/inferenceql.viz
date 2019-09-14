(ns inferdb.spreadsheets.query
  (:require [clojure.core.match :refer [match]]
            [clojure.edn :as edn]
            [instaparse.core :as insta]
            [metaprob.prelude :as mp]
            [inferdb.multimixture :as mmix]
            [inferdb.spreadsheets.model :as model]))

(def bnf
  "select = <\"SELECT\"> <ws> what (<ws> from)?
   <what> = paren-selectable (<\",\"> <ws> paren-selectable)*
   <paren-selectable> = selectable | <\"(\"> selectable <\")\">
   <selectable> = probability | star
   <generate> = <\"GENERATE *\"> (<ws> gen-given)? <ws> <\"FROM model\">
   probability = <\"PROBABILITY OF\"> <ws> (prob-column | prob-binding) (<ws> prob-given)? <ws> <\"FROM model\">
   prob-column = column
   prob-binding = binding
   star = <\"*\">
   from = <\"FROM\"> <ws> <\"(\"> generate <\")\"> <ws> limit
   gen-given = <\"GIVEN\"> <ws> bindings
   prob-given = <\"GIVEN\"> <ws> <star>
   <bindings> = binding (<ws> <\"AND\"> <ws> binding)*
   <binding> = column <\"=\"> value
   <column> = #'[a-zA-Z_]+'
   value = #'\\\"([^\\\"]+)\\\"'
   <limit> = <\"LIMIT\"> <ws> nat
   nat = #'[0-9]+'
   ws = #'\\s+'\n")

(def parser (insta/parser bnf))

(def row-generator (mmix/row-generator model/spec))

(defn parse-unsigned-int
  [s]
  #?(:clj (Integer/parseUnsignedInt s)
     :cljs (js/parseInt s)))

(defn- transform-from
  ([limit]
   (transform-from {} limit))
  ([obs limit]
   (let [trace (mmix/with-row-values {} obs)
         sample #(first (mp/infer-and-score :procedure row-generator
                                            :observation-trace trace))]
     (repeatedly limit sample))))

(defn transform-select
  [& args]
  (match (vec args)
    [[:star] s]
    {:type :generated
     :values s}

    [[:probability [:prob-column column] [:prob-given]] [:star]]
    {:type :anomaly-search
     :column column
     :given true}

    [[:probability [:prob-column column]] [:star]]
    {:type :anomaly-search
     :column column}

    [[:probability [:prob-binding label value] [:prob-given]] [:star]]
    {:type :search-by-labeled
     :given true
     :binding {label value}}

    :else (let [error-msg (str "Unmatched parse tree: " (pr-str (vec args)))]
            #?(:clj (println error-msg)
               :cljs (js/alert error-msg)))))

(def transform-map
  {:select transform-select
   :from transform-from
   :gen-given hash-map
   :value edn/read-string
   :nat parse-unsigned-int})

(defn issue
  [q]
  (let [ast (parser q)]
    (insta/transform transform-map ast)))

#_(issue "SELECT * FROM (GENERATE * FROM model) LIMIT 1")
#_(issue "SELECT * FROM (GENERATE * GIVEN java=\"False\" AND linux=\"True\" FROM model) LIMIT 3")

#_(issue  "SELECT (PROBABILITY OF salary_usd GIVEN * FROM model), *")

#_(issue "SELECT (PROBABILITY OF salary_usd FROM model), *" [{}])
#_(issue "SELECT (PROBABILITY OF salary_usd GIVEN * FROM model), *" [{"salary_usd" 80000 "linux" "True"}])
#_(issue "SELECT (PROBABILITY OF label=\"True\" GIVEN * FROM model), *")

#_(insta/transform transform-map (parser "\"False\"" :start :value))

#_(parser "SELECT * FROM (GENERATE * FROM model) LIMIT 1")
#_(parser "SELECT * FROM (GENERATE * GIVEN java=\"False\" FROM model) LIMIT 1")
                   #_(parser "SELECT (PROBABILITY OF salary_usd GIVEN * FROM model), *")
