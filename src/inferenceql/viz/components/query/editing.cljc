(ns inferenceql.viz.components.query.editing
  "Defs related to editing inferenceql.query queries."
  (:require [instaparse.core :as insta]
            [inferenceql.query :as query]
            [inferenceql.query.parse-tree :as tree]
            [inferenceql.viz.util :refer [coerce-bool]]
            [inferenceql.viz.components.query.util :refer [long-str make-node children zipper
                                                           seek-tag]]
            [clojure.zip :as z]
            [clojure.string :as str]
            [medley.core :as medley]
            #?(:cljs [goog.string :refer [format]])))

(defn- add-rowid-and-label-helper
  "Performs the heavy lifting for `add-row-id`.

  Args:
    query: A query string.
  Returns:
    A query string."
  [query]
  (let [rowid-selection-node (query/parse "rowid" :start :selection)
        editable-col-selection-node (query/parse "editable" :start :selection)
        label-col-selection-node (query/parse "label" :start :selection)

        add-rowid (fn [select-list-node]
                    (let [selections (into [rowid-selection-node
                                            editable-col-selection-node
                                            label-col-selection-node]
                                           (tree/child-nodes select-list-node))
                          selections (as-> selections $
                                           (interleave $ (repeat ",") (repeat (query/parse " " :start :ws)))
                                           (drop-last 2 $))]

                      (tree/node :select-list selections)))]
    (-> (query/parse query)
        (zipper)
        (seek-tag :select-list)
        (z/edit add-rowid)
        (z/root)
        (query/unparse))))

(defn add-rowid-and-label
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
      (add-rowid-and-label-helper query))))

;-------------------------------------------------------

(def whitespace (query/parse "\n     " :start :ws))

(defn with-sub-query-node [qs]
  (let [qz (-> qs query/parse zipper)
        qz-with (seek-tag qz :with-expr)]
    (if qz-with
      (-> (z/node qz-with)
          (tree/get-node-in [:with-sub-expr :query-expr]))
      (z/node qz))))

;------------------------------------------------------

(def label-alter-node (query/parse "(ALTER data ADD label) AS data" :start :with-map-entry-expr))

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

(defn remove-label-alter [entry-exprs]
  (remove is-label-alter? entry-exprs))

(defn add-label-alter [entry-exprs]
  (concat entry-exprs [label-alter-node]))

;------------------------------------------------------

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

(defn make-update-label-node [updates label-val]
  (let [rowids (-> (medley/filter-vals {label-val true} updates)
                   (keys))]
    (when (seq rowids)
      (let [cond-str (->> rowids
                          (map #(format "rowid=%d" %))
                          (str/join " OR "))
            qs (format "(UPDATE data SET label=%s WHERE %s) AS data" label-val cond-str)]
        (query/parse qs :start :with-map-entry-expr)))))

(defn remove-label-update [entry-exprs]
  (remove update-node-map entry-exprs))

(defn add-label-update [entry-exprs updates]
  (let [pos-update-node (make-update-label-node updates true)
        neg-update-node (make-update-label-node updates false)
        _ (do #?(:cljs (.log js/console :pos pos-update-node)))
        _ (do #?(:cljs (.log js/console :neg neg-update-node)))]
    (cond-> entry-exprs
            pos-update-node (concat [pos-update-node])
            neg-update-node (concat [neg-update-node]))))

(defn new-row-node? [node]
  (some-> (zipper node)
          (seek-tag :insert-into-clause)
          (some?)))

(defn make-new-row-node [row]
  (let [kv-strs (for [[k v] row]
                  (format "%s=%s" k v))
        row-str (str/join ", " kv-strs)
        qs (format "(INSERT INTO data VALUES (%s)) AS data" row-str)]
    (query/parse qs :start :with-map-entry-expr)))

(defn remove-new-rows [entry-exprs]
  (remove new-row-node? entry-exprs))

(defn add-new-rows [entry-exprs new-rows]
  (concat entry-exprs (map make-new-row-node new-rows)))

;------------------------------------------------------

(defn edit-map-exprs [node f & args]
  (let [f-ready #(apply f (concat [%] args))
        entry-exprs (-> node
                        (tree/get-node :with-map-expr)
                        (tree/child-nodes)
                        (f-ready))

        ;; Add commas and whitespace
        entry-exprs (as-> entry-exprs $
                          (interleave $ (repeat ",") (repeat whitespace))
                          (drop-last 2 $))]
    (-> (zipper node)
        (seek-tag :with-map-expr)
        (z/replace (tree/node :with-map-expr entry-exprs))
        (z/root))))

(defn add-new-with [sub-query-node updates new-rows]
  (if (or (seq updates) (seq new-rows))
    (let [pos-update-node (make-update-label-node updates true)
          neg-update-node (make-update-label-node updates false)
          new-row-nodes (map make-new-row-node new-rows)

          entry-exprs (cond-> []
                              (seq updates) (concat [label-alter-node])
                              pos-update-node (concat [pos-update-node])
                              neg-update-node (concat [neg-update-node])
                              (seq new-row-nodes) (concat new-row-nodes))
          entry-exprs (as-> entry-exprs $
                            (interleave $ (repeat ",") (repeat whitespace))
                            (drop-last 2 $))
          with-map-expr (tree/node :with-map-expr entry-exprs)]
      (str "WITH " (query/unparse with-map-expr) ":\n" (query/unparse sub-query-node)))
    (query/unparse sub-query-node)))

(defn handle [qz-with sub-query-node updates new-rows]
  (if qz-with
    (let [with-node (z/node qz-with)
          new-with-node (cond-> with-node
                          :always (edit-map-exprs remove-label-alter)
                          :always (edit-map-exprs remove-label-update)
                          :always (edit-map-exprs remove-new-rows)
                          (seq updates) (edit-map-exprs add-label-alter)
                          (seq updates) (edit-map-exprs add-label-update updates)
                          (seq new-rows) (edit-map-exprs add-new-rows new-rows))

          has-map-entries (-> new-with-node
                              (tree/get-node :with-map-expr)
                              (tree/child-nodes)
                              (seq))

          return-node (if has-map-entries
                        (-> qz-with
                            (z/replace new-with-node)
                            (seek-tag :query-expr)
                            (z/replace sub-query-node)
                            (z/root))
                        sub-query-node)]
      (query/unparse return-node))
    (add-new-with sub-query-node updates new-rows)))

(defn add-update-labels-expr [cur-qs prev-qs updates new-rows]
  (let [cur-qs (str/trim cur-qs)]
    (if-not (insta/failure? (query/parse cur-qs))
      ;; Edit the query.
      (let [prev-qz (some-> prev-qs query/parse zipper)
            prev-qz-with (some-> prev-qz (seek-tag :with-expr))
            cur-qz (some-> cur-qs query/parse zipper)
            cur-qz-with (some-> cur-qz (seek-tag :with-expr))
            sub-query-node (with-sub-query-node cur-qs)]
        (if prev-qz-with
          ;; Use the previous WITH clause.
          (handle prev-qz-with sub-query-node updates new-rows)
          ;; Use the current WITH clause.
          (handle cur-qz-with sub-query-node updates new-rows)))

      ;; Just return the current un-edited query if it can't be parsed.
      cur-qs)))

;-------------------------------------------------------

;;; INCORPORATE column.

(defn maybe-incorp-node [updates editable-rows model-name]
  #?(:cljs (.log js/console :model-name model-name))
  (let [row-incorps  (if (seq editable-rows)
                       (loop [[r & rs] (reverse editable-rows) query model-name]
                         (if r
                           (let [kv-strs (for [[k v] r]
                                           (format "%s=%s" k v))
                                 row-str (str/join ", " kv-strs)
                                 new-query (format "(INCORPORATE ROW (%s) INTO %s)" row-str query)]
                             (recur rs new-query))
                           query))
                       model-name)

        column-incorp (if (seq updates)
                        ;; Create an INCORPORATE statement.
                        (let [bindings-str (->> (seq updates)
                                                (sort-by first)
                                                (map (fn [[k v]] (format "%s=%s" k v)))
                                                (str/join ", "))]
                          (format "(INCORPORATE COLUMN (%s) AS label INTO %s)", bindings-str row-incorps))

                        row-incorps)]
    #?(:cljs (.log js/console :incorp-string column-incorp))
    (query/parse column-incorp :start :model-expr)))

(defn add-incorp-node [node updates editable-rows]
  (let [loc (zipper node)
        incorp-loc (seek-tag loc :incorporate-expr)
        model-name (if incorp-loc
                       (-> incorp-loc
                           (seek-tag :ref)
                           (z/node)
                           (tree/get-node-in [:name :simple-symbol])
                           (tree/only-child))
                       (-> (seek-tag loc :simple-symbol)
                           (z/node)
                           (tree/only-child)))
        new-model-expr-node (maybe-incorp-node updates editable-rows model-name)]
    ;; NOTE: or I could edit the original node
    [:under-clause "UNDER" [:ws " "] new-model-expr-node]))

(defn add-incorp-labels-expr [query-string updates editable-rows]
  #?(:cljs (.log js/console :query-string query-string))
  #?(:cljs (.log js/console :updates updates))
  #?(:cljs (.log js/console :editable-rows editable-rows))
  (loop [qz (-> query-string query/parse zipper)]
    (if (z/end? qz)
      (-> qz z/node query/unparse)
      (if-let [qz-under (seek-tag qz :under-clause)]
        (recur (z/next (z/edit qz-under add-incorp-node updates editable-rows)))
        (recur (z/next qz))))))

;--------------------------------------------

;;; Testing

(def updates {3 true 6 true 7 false 10 false})
#_(def updates {3 true 6 true})
#_(def updates {7 false 10 false})
#_(def updates {})
#_(def updates nil)

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

(def prob-of-query-2
  (long-str
    "WITH (ALTER data ADD label) AS data,"
    "     (UPDATE data SET label=true WHERE rowid=4 OR rowid=10) AS data:"
    "SELECT (PROBABILITY OF label=true"
    "         GIVEN gender, height"
    "         UNDER model),"
    "       age, gender, height"
    "FROM data"))

(def prob-of-query-3
  (long-str
    "WITH (ALTER data ADD label) AS data,"
    "       (UPDATE data SET label=true WHERE rowid=4 OR rowid=10) AS data:"
    "  SELECT (PROBABILITY OF label=true"
    "           GIVEN gender, height"
    "           UNDER (INCORPORATE COLUMN (7=false, 10=false) AS label INTO model)),"
    "         age, gender, height"
    "  FROM data"))

(def new-row-query
  (long-str
    "WITH (ALTER data ADD label) AS data,"
    "     (UPDATE data SET label=true WHERE rowid=4 OR rowid=8) AS data,"
    "     (INSERT INTO data VALUES (age=75, editable=true)) AS data:"
    "SELECT age, gender, height FROM data;"))


(def incorp-row-query
  (long-str
   "WITH (ALTER data ADD label) AS data,"
   "       (UPDATE data SET label=true WHERE rowid=4 OR rowid=10) AS data:"
   "  SELECT (PROBABILITY OF label=true"
   "           GIVEN gender, height"
   "           UNDER (INCORPORATE COLUMN (7=false, 10=false) AS label INTO"
   "                  (INCORPORATE ROW (age=75, gender=\"Male\") INTO model))),"
   "         age, gender, height"
   "  FROM data"))

(query/parse incorp-row-query)


;;(add-incorp-labels-expr "SELECT * FROM data;" updates)
;;(add-incorp-labels-expr prob-of-query-3 updates)

(def test-new-qs "SELECT age, gender FROM data;")
(def test-prev-qs
  (long-str
   "WITH (ALTER data ADD label) AS data,"
   "     (UPDATE data SET label=true WHERE rowid=1 OR rowid=2 OR rowid=3) AS data:"
   "SELECT * FROM data;"))

;;(add-update-labels-expr test-new-qs test-prev-qs updates)
