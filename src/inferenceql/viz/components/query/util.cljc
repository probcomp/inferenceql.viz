(ns inferenceql.viz.components.query.util
  "Utility functions for manipulating and extracting info from iql.query parse trees."
  (:require [instaparse.core :as insta]
            [inferenceql.query.parser :as parser]
            [inferenceql.query.parser.tree :as tree]
            [clojure.zip :as z]))

;;; Various utility functions related to queries.

(defn long-str [& strings] (clojure.string/join "\n" strings))

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

;;; Functions related to extracting details about columns from a query.
;;; e.g. column-renames, new columns (form PROB OF), etc.

(defn- get-selection-list
  "Gets the selection-list portion of the parse `tree`."
  [tree]
  (when-not (insta/failure? tree)
    (-> tree
        (zipper)
        (seek-tag :select-list)
        (z/node)
        (tree/child-nodes))))

(defn- column-selection-rename
  "Returns a column-detail map for this type of selection node that renames a column."
  [node]
  (let [column-name (tree/get-node-in node [:column-expr :name :simple-symbol])
        new-name (tree/get-node-in node [:label-clause :name :simple-symbol])]
    (when new-name
      {:detail-type :rename
       :old-name (keyword (tree/only-child column-name))
       :new-name (keyword (tree/only-child new-name))})))

(defn- new-column-schema
  "Returns a column-detail map for this type of selection node that creates a new column."
  [node]
  (let [new-name (tree/get-node-in node [:label-clause :name :simple-symbol])]
    (when new-name
      {:detail-type :new-column-schema
       :name (keyword (tree/only-child new-name))
       :stat-type :numerical})))

(defn- column-details-for-selection
  "Returns a column-detail map for this `selection` portion of the parse tree."
  [selection]
  (let [selection-block (first (tree/child-nodes selection))
        selection-type (tree/tag selection-block)]
    (when selection-type
      (case selection-type
        :selection (column-details-for-selection selection-block)
        :rowid-selection nil ; We don't care about the rowid column.
        :column-selection (column-selection-rename selection-block)
        :probability-clause (new-column-schema selection-block)
        :density-clause (new-column-schema selection-block)))))

(defn column-details
  "Extracts details about column renames and new columns as a result of `query`.

  Returns a sequences of column-detail maps, each map representing either a column rename or
  schema info for a new column."
  [query]
  (keep column-details-for-selection (get-selection-list (parser/parse query))))

(defn column-renames
  "Returns a map of columns that have been renamed as a result of executing the last query.

  Columns can be renames in queries using the AS keyword.
  Returns: {:old-column-name :new-column-name, ...}"
  [column-details]
  (->> column-details
    (filter #(= (:detail-type %) :rename))
    (map (juxt :old-name :new-name))
    (into {})))

(defn new-columns-schema
  "Returns a schema for new columns that were created as a result of executing the last query.

  Returns: {:new-column-name :numerical, ...}"
  [column-details]
  (->> column-details
    (filter #(= (:detail-type %) :new-column-schema))
    (map (juxt :name :stat-type))
    (into {})))

;;; Functions for checking whether a query returned virtual-data.

(defn virtual-data?
  "Returns whether the resultset of `query` represents virtual data."
  [query]
  (let [node-or-failure (parser/parse query)]
    (when-not (insta/failure? node-or-failure)
      (some? (some-> node-or-failure
                     zipper
                     (seek-tag :generated-table-expr)
                     (z/node))))))
