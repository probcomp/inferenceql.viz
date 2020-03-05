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
  ([model limit]
   (transform-from {} model limit))
  ([obs model limit]
   [:generate obs limit]))

(defn- transform-select
  [& args]
  (match (vec args)
    [[:star]]
    {:type :display-dataset}

    [[:star] [:generate obs limit]]
    {:type :generate-virtual-row
     :conditions obs
     :num-rows limit}

    ;; Given whole row.
    [[:probability [:prob-column column] [:prob-given [:given-target [:star]]] [:using [:model model]]] [:star]]
    {:type :anomaly-search
     :column column
     :given :row}

    ;; Given another column.
    [[:probability [:prob-column column] [:prob-given [:given-target given-col]] [:using [:model model]]] [:star]]
    {:type :anomaly-search
     :column column
     :given given-col}

    ;; No given clause.
    [[:probability [:prob-column column] [:using [:model model]]] [:star]]
    {:type :anomaly-search
     :column column}

    [[:probability [:prob-binding label value] [:prob-given [:given-target [:star]]] [:using [:model model]]] [:star]]
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
   :symbol edn/read-string
   :nat edn/read-string
   :float edn/read-string
   :int edn/read-string
   :string edn/read-string})

(defn parse
  "Parses a string containing an InferenceQL query and produces a map representing
  the query to be performed."
  [& args]
  (let [ast (apply parser args)]
    (insta/transform transform-map ast)))
