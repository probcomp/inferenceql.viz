(ns inferenceql.spreadsheets.query
  #?(:clj (:require [inferenceql.spreadsheets.io :as sio])
     :cljs (:require-macros [inferenceql.spreadsheets.io :as sio]))
  (:require [clojure.core.match :refer [match]]
            [clojure.edn :as edn]
            [clojure.walk :as walk]
            [datascript.core :as d]
            [instaparse.core :as insta]
            [meander.epsilon :as meander]
            [net.cgrand.enlive-html :as enlive]))

(def parser (insta/parser (sio/inline-resource "query.bnf")
                          #_#_
                          :output-format :enlive))

(def ^:private transform-map
  {:star        (constantly '*)
   :column-name keyword
   :predicate   symbol

   ;; :presence-condition  (fn [c]     ['?e c '_])
   ;; :absence-condition   (fn [c]     (list 'missing? '$ '?e c))
   ;; :and-condition       (fn [c1 c2] (list 'and c1 c2))
   ;; :or-condition        (fn [c1 c2] (list 'or c1 c2))
   ;; :equality-condition  (fn [c v]   ['?e c v])

   ;; :predicate-condition (fn [c p v] (let [sym (symbol (str "?" (gensym)))]
   ;;                                    [['?e c sym]
   ;;                                     [(list p sym v)]]))

   :symbol edn/read-string
   :nat    edn/read-string
   :float  edn/read-string
   :int    edn/read-string
   :string edn/read-string})

(defn parse
  "Parses a string containing an InferenceQL query and produces a map representing
  the query to be performed."
  [& args]
  (let [ast (apply parser args)]
    (insta/transform transform-map ast)))

(def select-first (comp first enlive/select))

(meander/find [1 2 3 4 2 5]
  (meander/scan 2 ?e) ?e)

(defn execute
  [query db]
  (let [selector (into [:db/id] (enlive/select query [enlive/root :> :selection enlive/text]))
        db (if-let [subquery (first (enlive/select query [enlive/root :> :source :> :query]))]
             (d/db-with (d/empty-db) (execute subquery db))
             db)
        where-clauses (into '[[?e _ _]]
                            (-> query
                                (select-first [enlive/root :> :conditions enlive/text])))
        eid-query {:find '[[?e ...]]
                   :in '[$]
                   :where where-clauses}
        eids (d/q eid-query db)]
    (->> (d/pull-many db selector eids)
         (sort-by :db/id)
         (map #(dissoc % :db/id)))))

(defn q
  [query & args]
  (let [parse-tree (parse query)]
    (if-not (insta/failure? parse-tree)
      (apply execute parse-tree args)
      (throw (ex-info "Parse failure" parse-tree)))))

(parse "SELECT * FROM data")
(parse "SELECT * FROM (SELECT * FROM data)")

(parse "SELECT * FROM data WHERE a IS NULL AND b IS NULL")
(parse "SELECT * FROM data WHERE a IS NULL AND b IS NULL OR c IS NULL AND d IS NULL")

(parse "SELECT a, b FROM data WHERE a = \"cat\"")
(parse "SELECT b FROM (SELECT a FROM (SELECT * FROM data))")

(-> (parse "SELECT b FROM (SELECT a FROM (SELECT b FROM data))")
    (enlive/select [enlive/root :> :selection enlive/text]))

(def db
  (d/db-with (d/empty-db)
             [{:a "cat", :b "feline"}
              {:a "dog", :b "canine" :code 2}
              {:a "bird"             :code 19}]))

(enlive/select (parse "SELECT b FROM data WHERE b IS NOT NULL AND code IS NOT NULL")
               [enlive/root :> :conditions enlive/text])

#_ (q "SELECT b FROM data WHERE b IS NOT NULL AND code IS NOT NULL" db)

#_(q "SELECT b FROM (SELECT b FROM (SELECT * FROM data) WHERE b IS NOT NULL AND code IS NOT NULL)" db)

#_(q "WAFFLE" db)

(q "SELECT code FROM data" db)

(q "SELECT a, b FROM data WHERE b IS NOT NULL" db)

(q "SELECT a, b FROM data WHERE b IS NULL" db)

(q "SELECT a FROM data WHERE code = 19" db)

(q "SELECT b FROM data WHERE a = \"cat\"" db)

(q "SELECT * FROM data" db)

(q "SELECT * FROM (SELECT a, b FROM data)" db)

(q "SELECT * FROM (SELECT * FROM data)" db)

(q "SELECT b FROM (SELECT a FROM (SELECT * FROM data))" db)
