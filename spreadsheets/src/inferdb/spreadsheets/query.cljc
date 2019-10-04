(ns inferdb.spreadsheets.query
  #?(:clj (:require [inferdb.spreadsheets.io :as sio])
     :cljs (:require-macros [inferdb.spreadsheets.io :as sio]))
  (:require [clojure.core.match :refer [match]]
            [clojure.edn :as edn]
            [instaparse.core :as insta]
            [metaprob.prelude :as mp]
            [inferdb.multimixture :as mmix]
            [inferdb.spreadsheets.model :as model]))

(def parser (insta/parser (sio/inline-resource "query.bnf")))
(def ^:private row-generator (mmix/row-generator model/spec))

(defn- parse-unsigned-int
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
     (->> (repeatedly sample)
          ;; TODO: This is a hack for Strange Loop 2019
          (remove #(some (every-pred number? neg?) (vals %)))
          (take limit)))))

(defn- transform-select
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

    :else
    (let [logged-msg (str "Unmatched parse tree: " (pr-str (vec args)))
          alerted-msg "Invalid query syntax."]
      #?(:clj (binding [*out* *err*]
                (println logged-msg))
         :cljs (js/console.error logged-msg))
      #?(:clj (binding [*out* *err*]
                (println alerted-msg))
         :cljs (js/alert alerted-msg))
      {})))

(def ^:private transform-map
  {:select transform-select
   :from transform-from
   :gen-given hash-map
   :value edn/read-string
   :nat parse-unsigned-int})

(defn parse
  "Parses a string containing an InferenceQL query and produces a map representing
  the query to be performed."
  [q]
  (let [ast (parser q)]
    (insta/transform transform-map ast)))
