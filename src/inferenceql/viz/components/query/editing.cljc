(ns inferenceql.viz.components.query.editing
  "Defs related to editing inferenceql.query queries."
  (:require [instaparse.core :as insta]
            [inferenceql.query :as query]
            [inferenceql.query.parse-tree :as tree]
            [clojure.zip :as z]
            [clojure.string :as str]
            [medley.core :as medley]
            #?(:cljs [goog.string :refer [format]])))

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

(defn- add-rowid-helper
  "Performs the heavy lifting for `add-row-id`.

  Args:
    query: A query string.
  Returns:
    A query string."
  [query]
  #?(:cljs (.log js/console :here2))
  (let [add-rowid (fn [zip]
                    ;; Takes zip, a zipper that points to a :select-list node.
                    #?(:cljs (.log js/console :zip (z/node zip)))
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
  #?(:cljs (.log js/console :here))
  (let [node-or-failure (query/parse query)]
    (if (insta/failure? node-or-failure)
      query
      (if (seek-tag (zipper node-or-failure) :rowid-selection)
        query
        (add-rowid-helper query)))))

;-------------------------------------------------------

;;; ALTER column.
;;; UPDATE column.

(def whitespace (query/parse "\n     " :start :ws))

(def label-alter-node (query/parse "(ALTER data ADD label) AS data" :start :with-map-entry-expr))

(defn coerce-bool [val]
  (case (str/lower-case val)
    "true" true
    "t" true
    "false" false
    "f" false
    nil))

(defn with-sub-query-node [qs]
  (let [qz (-> qs query/parse zipper)
        qz-with (seek-tag qz :with-expr)]
    (if qz-with
      (-> (z/node qz-with)
          (tree/get-node-in [:with-sub-expr :query-expr]))
      (z/node qz))))

(defn is-label-alter? [node]
  (let [val-expr (tree/get-node node :with-map-value-expr)
        alter-expr (some-> (zipper val-expr)
                           (seek-tag :alter-expr)
                           (z/node))
        table-name (some-> alter-expr
                           (tree/get-node-in [:table-expr :ref])
                           (query/unparse))
        column-name (some-> alter-expr
                            (tree/get-node-in [:column-expr])
                            (query/unparse))

        binding-name (-> (tree/get-node node :name)
                         (query/unparse))]
    (and (= table-name "data")
         (= column-name "label")
         (= binding-name "data"))))

(defn add-label-alter [node]
  (let [whitespace (query/parse "\n     " :start :ws)
        entry-exprs (as-> node $
                          (tree/get-node $ :with-map-expr)
                          (tree/child-nodes $)
                          (remove is-label-alter? $)
                          (concat $ [label-alter-node])
                          ;; Is there a better way?
                          (interleave $ (repeat ",") (repeat whitespace))
                          (drop-last 2 $))]
    (-> (zipper node)
        (seek-tag :with-map-expr)
        (z/replace (tree/node :with-map-expr entry-exprs))
        (z/root))))

(defn reduce-or-node [node]
  (let [reducer (fn reducer [node]
                  (case (tree/tag node)
                    :or-condition (let [child-nodes (tree/child-nodes node)]
                                    (case (count child-nodes)
                                      1 (reducer (tree/only-child node))
                                      2 (let [where-pairs (map reducer child-nodes)]
                                          (if (every? some? where-pairs)
                                            (apply concat where-pairs)
                                            nil))))
                    :condition (reducer (tree/only-child node))
                    :equality-condition (let [selection (some-> node
                                                                (tree/get-node :selection)
                                                                (query/eval {}))
                                              value (some-> node
                                                            (tree/get-node :value)
                                                            (query/eval {}))]
                                          [[selection value]])
                    nil))]
    (reducer node)))

(defn update-node-map [node]
  (let [val-expr (tree/get-node node :with-map-value-expr)
        update-expr (some-> (zipper val-expr)
                            (seek-tag :update-expr)
                            (z/node))
        table-name (some-> update-expr
                           (tree/get-node-in [:table-expr :ref])
                           (query/unparse))

        eval #(query/eval % {})
        set-map-exprs (some-> update-expr
                              (tree/get-node-in [:set-clause :map-expr])
                              (tree/child-nodes))
        set-map (apply merge (map eval set-map-exprs))

        where-pairs  (some-> update-expr
                             (tree/get-node-in [:where-clause :or-condition])
                             (reduce-or-node))
        binding-name (-> (tree/get-node node :name)
                         (query/unparse))

        is-label-update (and (= table-name "data")
                             (every? #(and (= "rowid" (first %)) (integer? (second %)))
                                     where-pairs)
                             (or (= set-map {:label true})
                                 (= set-map {:label false}))
                             (= binding-name "data"))]
    (when is-label-update
      (zipmap (map second where-pairs)
              (repeat (:label set-map))))))

(defn update-label-node [updates label-val]
  (let [rowids (-> (medley/filter-vals {label-val true} updates)
                   (keys))]
    (when (seq rowids)
      (let [cond-str (->> rowids
                          (map #(format "rowid=%d" %))
                          (str/join " OR "))
            qs (format "(UPDATE data SET label=%s WHERE %s) AS data" label-val cond-str)]
        (query/parse qs :start :with-map-entry-expr)))))

(defn add-label-update [node updates]
  (let [entry-exprs (as-> node $
                          (tree/get-node $ :with-map-expr)
                          (tree/child-nodes $))

        existing-updates (->> entry-exprs
                           (keep update-node-map)
                           (apply merge))
        all-updates (merge existing-updates updates)

        pos-update-node (update-label-node all-updates true)
        neg-update-node (update-label-node all-updates false)

        entry-exprs (remove update-node-map entry-exprs)
        entry-exprs (cond-> entry-exprs
                            pos-update-node (concat [pos-update-node])
                            neg-update-node (concat [neg-update-node]))
        entry-exprs (as-> entry-exprs $
                          (interleave $ (repeat ",") (repeat whitespace))
                          (drop-last 2 $))]
    (-> (zipper node)
        (seek-tag :with-map-expr)
        (z/replace (tree/node :with-map-expr entry-exprs))
        (z/root))))

(defn add-updates-to-with [node updates]
  (-> node
      (add-label-alter)
      (add-label-update updates)))

(defn new-with [sub-query-node updates]
  (let [pos-update-node (update-label-node updates true)
        neg-update-node (update-label-node updates false)
        entry-exprs (cond-> [label-alter-node]
                      pos-update-node (concat [pos-update-node])
                      neg-update-node (concat [neg-update-node]))
        entry-exprs (as-> entry-exprs $
                          (interleave $ (repeat ",") (repeat whitespace))
                          (drop-last 2 $))
        with-map-expr (tree/node :with-map-expr entry-exprs)]
    (str "WITH " (query/unparse with-map-expr) ":\n" (query/unparse sub-query-node))))

(defn add-update-labels-expr [new-qs prev-qs updates]
  (if (seq updates)
    (let [updates (medley/map-vals #(coerce-bool (:label %)) updates)
          sub-query-node (with-sub-query-node new-qs)
          qz (some-> prev-qs query/parse zipper)
          qz-with (some-> qz (seek-tag :with-expr))]
      (if qz-with
        (-> (z/edit qz-with add-updates-to-with updates)
            (seek-tag :query-expr)
            (z/replace sub-query-node)
            (z/root)
            (query/unparse))
        (new-with sub-query-node updates)))
    new-qs))


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
  (if (seq updates)
    (loop [qz (-> query-string query/parse zipper)]
      (if (z/end? qz)
        (-> qz z/node query/unparse)
        (let [qz-under (seek-tag qz :under-clause)]
          (if (some-> qz-under no-incorp-node?)
            (recur (z/edit qz-under add-incorp-node updates))
            (recur (z/next qz))))))
    query-string))

;--------------------------------------------

;;; Testing

(def updates {3 {:label "True"}, 6 {:label "True"}, 7 {:label "False"}, 10 {:label "False"}})
(def updates {3 {:label "True"}, 6 {:label "True"}})
(def updates {7 {:label "False"}, 10 {:label "False"}})
(def updates {})
(def updates nil)

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


;;(add-incorp-labels-expr "SELECT * FROM data;" updates)
;;(add-incorp-labels-expr prob-of-query updates)


(def test-new-qs "SELECT age, gender FROM data;")
(def test-prev-qs
  (long-str
   "WITH (ALTER data ADD label) AS data,"
   "     (UPDATE data SET label=true WHERE rowid=1 OR rowid=2 OR rowid=3) AS data:"
   "SELECT * FROM data;"))


;;(add-update-labels-expr test-new-qs nil updates)
