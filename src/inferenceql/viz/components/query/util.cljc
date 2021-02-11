(ns inferenceql.viz.components.query.util
  "Utility functions for manipulating and extracting info from iql.query parse trees."
  (:require [instaparse.core :as insta]
            [inferenceql.query :as query]
            [inferenceql.query.parse-tree :as tree]
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

;;; Functions related to (column-details)

(defn- get-selection-list
  "Gets the selection-list portion of the parse `tree`."
  [tree]
  (when-not (insta/failure? tree)
    (as-> tree $
      (tree/get-node-in $ [:select-expr :select-clause :select-list])
      (tree/child-nodes $))))

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
       :stat-type :gaussian})))

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
  (keep column-details-for-selection (get-selection-list (query/parse query))))

;;; Functions related to (virtual-data?)

(defn- virtual-data-table-expr?
  "Returns whether this `table-expr` portion of the parse tree generates virtual data."
  [table-expr]
  (let [table-expr-contents (first (tree/child-nodes table-expr))
        table-expr-type (tree/tag table-expr-contents)]
    (case table-expr-type
      :table-expr (virtual-data-table-expr? table-expr-contents)
      :generated-table-expr true
      false)))

(defn virtual-data?
  "Returns whether the resultset of `query` represents virtual data."
  [query]
  (let [node-or-failure (query/parse query)]
    (when-not (insta/failure? node-or-failure)
      (let [table-expr (tree/get-node-in node-or-failure [:from-clause :table-expr])]
        (virtual-data-table-expr? table-expr)))))
