(ns inferenceql.spreadsheets.query
  #?(:clj (:require [inferenceql.spreadsheets.io :as sio])
     :cljs (:require-macros [inferenceql.spreadsheets.io :as sio]))
  (:require [clojure.edn :as edn]
            [clojure.pprint :as pprint]
            [datascript.core :as d]
            [instaparse.core :as insta]
            [net.cgrand.enlive-html :as enlive]))

(def parser (insta/parser (sio/inline-resource "query.bnf")
                          :output-format :enlive))

(def ^:private transform-map
  {:star        (constantly '*)
   :column-name keyword
   :predicate   symbol

   :presence-condition  (fn [c]     ['?e c '_])
   :absence-condition   (fn [c]     [(list 'missing? '$ '?e c)])
   :and-condition       (fn [c1 c2] (list 'and c1 c2))
   :or-condition        (fn [c1 c2] (list 'or c1 c2))
   :equality-condition  (fn [c v]   ['?e c v])
   :predicate-condition (fn [c p v]
                          (let [sym (symbol (str "?" (gensym)))]
                            [['?e c sym]
                             [(list p sym v)]]))

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

(defn subquery
  [ast]
  (first (enlive/select ast [enlive/root :> :source :> :query])))

(defn selector
  [ast]
  (enlive/select ast [enlive/root :> :selection enlive/text]))

(defn conditions
  [ast]
  (enlive/select ast [enlive/root :> :conditions enlive/text]))

(defn iql-db
  "Converts a vector of maps into a IQL database."
  [table]
  (->> table
       (map #(assoc % :iql/type :iql.type/row))
       (d/db-with (d/empty-db))))

(defn execute
  [ast rows]
  (let [selector (selector ast)
        db (iql-db (if-let [subquery (subquery ast)]
                     (execute subquery rows)
                     rows))
        eid-query {:find '[[?e ...]]
                   :in '[$]
                   :where (into [['?e :iql/type  :iql.type/row]]
                                (conditions ast))}
        eids (d/q eid-query db)
        columns (if (some #{'*} selector)
                  (into []
                        (comp (mapcat keys)
                              (distinct))
                        rows)
                  (vec selector))
        metadata {:iql/columns columns}
        rows (->> (d/pull-many db (conj selector :db/id) eids)
                  (sort-by :db/id)
                  (map #(dissoc % :db/id :iql/type)))]
    (with-meta rows metadata)))

(defn q
  [query & args]
  (let [parse-tree (parse query)]
    (if-not (insta/failure? parse-tree)
      (apply execute parse-tree args)
      parse-tree)))

(defn pq
  [& args]
  (let [rows (apply q args)
        columns (:iql/columns (meta rows))]
    (pprint/print-table
     (map name columns)
     (for [row rows]
       (reduce-kv (fn [m k v]
                    (assoc m (name k) v))
                  {}
                  row)))))

(comment

  (def db
    [{:a "cat", :b "feline"}
     {:a "dog", :b "canine" :c 2}
     {:a "bird"             :c 19}])

  (pq "SELECT *" db)
  (pq "SELECT * FROM data" db)
  (pq "SELECT a, b FROM data WHERE b IS NOT NULL AND c IS NOT NULL" db)
  (pq "SELECT a, b FROM data WHERE b IS NULL" db)
  (pq "SELECT a FROM data WHERE code=19" db)
  (pq "SELECT b FROM data WHERE a=\"cat\"" db)
  (pq "SELECT * FROM (SELECT * FROM data)" db)
  (pq "SELECT * FROM (SELECT a, b FROM data)" db)
  (pq "SELECT * FROM (SELECT * FROM data)" db)
  (pq "SELECT b FROM (SELECT a FROM (SELECT * FROM data))" db)

  )
