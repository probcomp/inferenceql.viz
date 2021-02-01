(ns inferenceql.viz.components.query.editing
  "Defs related to editing inferenceql.query queries."
  (:require [instaparse.core :as insta]
            [inferenceql.query :as query]
            [inferenceql.query.parse-tree :as tree]
            [clojure.zip :as z]
            [clojure.string :as str]
            [medley.core :as medley]))

(defn make-node
  "Updates the children in an existing node."
  [node children]
  (tree/node (tree/tag node) children))

(defn children
  "Gets the children of a node.
  Unlike iql.query.parse-tree/children this does not remove whitespace."
  [node]
  (rest node))

(defn zipper
  "Given an iql.query parse tree, returns a zipper."
  [node]
  (z/zipper tree/branch? children make-node node))

(defn seek-tag
  "Navigates to the first location in a iql.query parse tree with `tag`.

  Args:
    loc: A zipper of an iql.query parse tree.
    tag: A tag to search for.
  Returns:
    A zipper navigated to a node tagged with `tag`. If such a tagged node can not be found,
      then returns nil."
  [loc tag]
  (cond
    (z/end? loc)
    nil

    (and (z/branch? loc) (= tag (tree/tag (z/node loc))))
    loc

    :else
    (recur (z/next loc) tag)))

(defn- add-rowid-helper
  "Performs the heavy lifting for `add-row-id`.

  Args:
    query: A query string.
  Returns:
    A query string."
  [query]
  (let [add-rowid (fn [zip]
                    ;; Takes zip, a zipper that points to a :select-list node.
                    (let [select-list-str (-> zip
                                              (z/node)
                                              (query/unparse))
                          select-list-node (query/parse (str "rowid, " select-list-str) :start :select-list)]
                      (z/replace zip select-list-node)))]
    (-> (query/parse query)
        (zipper)
        (seek-tag :select-list)
        (add-rowid)
        (z/root)
        (query/unparse))))

(defn add-rowid
  "Adds rowid to the start of the select-list in a `query` string.
  If the query can not be parsed, the original query will be returned.
  If rowid is already present in the select-list, this is a no-op.

  Args:
    query: A query string.
  Returns:
    A query string."
  [query]
  (let [node-or-failure (query/parse query)]
    (if (insta/failure? node-or-failure)
      query
      (if (seek-tag (zipper node-or-failure) :rowid-selection)
        query
        (add-rowid-helper query)))))

;-------------------------------------------------------


(def updates {3 {:label "True"}, 6 {:label "True"}, 7 {:label "False"}, 10 {:label "False"}})

(defn coerce-bool [val]
  (case (str/lower-case val)
    "true" true
    "t" true
    "false" false
    "f" false
    nil))

(defn add-label-update [qs updates]
  (let [label-updates (medley/map-vals #(coerce-bool (:label %)) updates)

        ;; Indent original query.
        qs-indented (->> (str/split-lines qs)
                         (map #(str "  " %))
                         (str/join "\n"))

        pos-rowids (-> (medley/filter-vals true? label-updates)
                       (keys))
        neg-rowids (-> (medley/filter-vals false? label-updates)
                       (keys))

        pos-cond-string (->> pos-rowids
                             (map #(format "rowid=%d" %))
                             (str/join " OR "))
        neg-cond-string (->> neg-rowids
                             (map #(format "rowid=%d" %))
                             (str/join " OR "))

        map-exprs ["(ALTER data ADD label) AS data"
                   (when pos-cond-string
                     (format "     (UPDATE data SET label=true WHERE %s)" pos-cond-string))
                   (when neg-cond-string
                     (format "     (UPDATE data SET label=false WHERE %s)" neg-cond-string))]

        map-exprs-str (->> map-exprs
                           (remove nil?)
                           (str/join ",\n"))]

    (if (or (seq pos-cond-string) (seq neg-cond-string))
      (format "WITH %s:\n%s" map-exprs-str qs-indented)
      qs)))

(defn long-str [& strings] (clojure.string/join "\n" strings))

(def prob-of-query
  (long-str
    "SELECT (PROBABILITY OF height"
    "        GIVEN age, gender"
    "        UNDER model),"
    "       (PROBABILITY OF age"
    "        UNDER model),"
    "       height,"
    "       age,"
    "       gender"
    "FROM data;"))

(print (add-label-update "SELECT * FROM data;" updates))
(print (add-label-update prob-of-query updates))

(defn incorporate-node [updates]
  (let [label-updates (medley/map-vals #(coerce-bool (:label %)) updates)

        bindings-str (->> (seq label-updates)
                          (sort-by first)
                          (map (fn [[k v]] (format "%s=%s" k v)))
                          (str/join ", "))

        incorporate-str (format "(INCORPORATE COLUMN (%s) INTO model)", bindings-str)]
    (query/parse incorporate-str :start :model-expr)))

;; Hmm this might be right.

(defn skip [loc]
  (when loc
    (let [r (z/right loc)]
      (if r r (recur (-> loc z/up))))))

(defn add-label-incorporate [qs updates]
  (let [new-model-node (incorporate-node updates)
        query-tree (query/parse qs)]
    (-> (zipper query-tree)
        (seek-tag :model-expr)
        (z/replace new-model-node)
        ;; NOTE: not sure how to navigate to the node after this to
        ;; begin searching for :model-expr again.
        ;; maybe this will help?
        ;; http://www.exampler.com/blog/2010/09/01/editing-trees-in-clojure-with-clojurezip/
        (z/root)
        #_(query/unparse))))



(add-label-incorporate "SELECT * FROM data;" updates)
(add-label-incorporate prob-of-query updates)

(def node (add-label-incorporate prob-of-query updates))

(z/node node)
(z/right node)

(-> prob-of-query
    (query/parse)
    (zipper)
    (seek-tag :model-expr)
    (z/replace "foo")
    (skip)
    (seek-tag :model-expr)
    (z/replace "bar")
    (z/root)
    (query/unparse))


(-> prob-of-query
    (query/parse))

;------------------------------------------




(def node (z/vector-zip [[1 2] [3 4]]))
(-> node
    (z/down)
    (z/down)
    (z/right)
    (z/prev)
    (z/node))


