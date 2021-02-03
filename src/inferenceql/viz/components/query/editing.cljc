(ns inferenceql.viz.components.query.editing
  "Defs related to editing inferenceql.query queries."
  (:require [instaparse.core :as insta]
            [inferenceql.query :as query]
            [inferenceql.query.parse-tree :as tree]
            [clojure.zip :as z]
            [clojure.string :as str]
            [medley.core :as medley]
            #?(:cljs [goog.string :refer [format]])))

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

;;; UPDATE column.

(defn coerce-bool [val]
  (case (str/lower-case val)
    "true" true
    "t" true
    "false" false
    "f" false
    nil))

(defn add-update-labels-expr-help [qs updates]
  (let [label-updates (medley/map-vals #(coerce-bool (:label %)) updates)

        ;; Indent original query.
        qs-indented (->> (str/split-lines qs)
                         #_(map #(str "  " %))
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
                   (when-not (str/blank? pos-cond-string)
                     (format "     (UPDATE data SET label=true WHERE %s) AS data" pos-cond-string))
                   (when-not (str/blank? neg-cond-string)
                     (format "     (UPDATE data SET label=false WHERE %s) AS data" neg-cond-string))]

        map-exprs-str (->> map-exprs
                           (remove nil?)
                           (str/join ",\n"))]

    (if (or (seq pos-cond-string) (seq neg-cond-string))
      (format "WITH %s:\n%s" map-exprs-str qs-indented)
      qs)))

;---------------------------

(defn with-sub-query-node [qs]
  (let [qz (-> qs query/parse zipper)
        qz-with (seek-tag qz :with-expr)]
    (if qz-with
      (-> (z/node qz-with)
          (tree/get-node-in [:with-sub-expr :query-expr]))
      (z/node qz))))

(defn has-label-alter? [node]
  (let [entry-exprs (->> (tree/get-node node :with-map-expr)
                         (tree/child-nodes))
        match (for [e entry-exprs]
                (let [val-expr (tree/get-node e :with-map-value-expr)
                      alter-expr (some-> (zipper val-expr)
                                         (seek-tag :alter-expr)
                                         (z/node))
                      table-name (some-> alter-expr
                                         (tree/get-node-in [:table-expr :ref])
                                         (query/unparse))
                      column-name (some-> alter-expr
                                          (tree/get-node-in [:column-expr])
                                          (query/unparse))

                      binding-name (-> (tree/get-node e :name)
                                       (query/unparse))]
                  (and (= table-name "data")
                       (= column-name "label")
                       (= binding-name "data"))))]
    ;; Any of the entry-exprs match.
    (or match)))

(defn add-label-alter [node]
  (let [map-entry-expr (query/parse "(ALTER data ADD label) AS data" :start :with-map-entry-expr)
        entry-exprs (->> (tree/get-node node :with-map-expr)
                         (tree/child-nodes))]
    (doall (map println entry-exprs)))
  node)

(defn add-label-update-pos [node updates]
  node)

(defn add-label-update-neg [node updates]
  node)

(defn add-updates-to-with [node updates]
  (-> node
      (add-label-alter)
      (add-label-update-pos updates)
      (add-label-update-neg updates)))

(defn add-update-labels-expr [new-qs prev-qs updates]
  (let [new-sub-query-node (with-sub-query-node new-qs)
        qz (-> prev-qs query/parse zipper)
        qz-with (seek-tag qz :with-expr)]
    (if qz-with
      (-> (z/edit qz-with add-updates-to-with updates)
          (seek-tag :query-expr)
          (z/replace new-sub-query-node)
          (z/root)
          (query/unparse))
      (add-update-labels-expr-help new-qs updates))))

(def updates {3 {:label "True"}, 6 {:label "True"}, 7 {:label "False"}, 10 {:label "False"}})

(add-update-labels-expr "SELECT age, gender FROM data;"
                        "WITH (ALTER data ADD label) AS data,
                              (UPDATE data SET label=false WHERE rowid=1 OR rowid=2 OR rowid=3) AS data:
                         SELECT * FROM data;"
                        updates)

;-------------------------------------------------------

;;; INCORPORATE column.

(defn incorp-node [updates model-name]
  (let [label-updates (medley/map-vals #(coerce-bool (:label %)) updates)

        bindings-str (->> (seq label-updates)
                          (sort-by first)
                          (map (fn [[k v]] (format "%s=%s" k v)))
                          (str/join ", "))

        incorporate-str (format "(INCORPORATE COLUMN (%s) INTO %s)", bindings-str model-name)]
    (query/parse incorporate-str :start :model-expr)))

(defn no-incorp-node? [loc]
  (nil? (seek-tag loc :incorporate-expr)))

(defn add-incorp-node [node updates]
  (let [loc (zipper node)
        model-name-loc (seek-tag loc :simple-symbol)
        model-name (-> model-name-loc z/node tree/only-child)
        incorp-node (incorp-node updates model-name)]
    (-> model-name-loc
        (z/up) ; [:name ...]
        (z/up) ; [:ref ...]
        (z/up) ; [:model-expr ...]
        (z/replace incorp-node)
        (z/root))))

(defn add-incorp-labels-expr [query-string updates]
  (loop [qz (-> query-string query/parse zipper)]
    (if (z/end? qz)
      (-> qz z/node query/unparse)
      (let [qz-under (seek-tag qz :under-clause)]
        (if (some-> qz-under no-incorp-node?)
          (recur (z/edit qz-under add-incorp-node updates))
          (recur (z/next qz)))))))

;--------------------------------------------

;;; Testing

(def updates {3 {:label "True"}, 6 {:label "True"}, 7 {:label "False"}, 10 {:label "False"}})
(def updates {3 {:label "True"}, 6 {:label "True"}})

(defn long-str [& strings] (clojure.string/join "\n" strings))

(def prob-of-query
  (long-str
   "SELECT (PROBABILITY OF height"
   "        GIVEN age, gender"
   "        UNDER model1),"
   "       (PROBABILITY OF age"
   "        UNDER model2),"
   "       height,"
   "       age,"
   "       gender"
   "FROM data;"))

(comment
  (print (add-update-labels-expr-help "SELECT * FROM data;" updates))
  (print (add-update-labels-expr-help prob-of-query updates))

  (add-incorp-labels-expr "SELECT * FROM data;" updates)
  (add-incorp-labels-expr prob-of-query updates))




(print (add-update-labels-expr-help prob-of-query updates))
