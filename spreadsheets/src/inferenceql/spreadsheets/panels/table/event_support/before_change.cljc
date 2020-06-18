(ns inferenceql.spreadsheets.panels.table.event-support.before-change
  "Supporting functions for the :hot/before-change event"
  (:require [inferenceql.spreadsheets.panels.table.db :as db]
            [inferenceql.spreadsheets.model :as model]
            [inferenceql.inference.gpm.multimixture.specification :as mm.spec]
            [medley.core :as medley]
            [clojure.edn :as edn]
            [clojure.spec.alpha :as s]
            [goog.string :as gstring]))

(s/def ::row-id ::db/row-id)
(s/def ::row-data ::db/row)
(s/def ::col ::db/header)
(s/def ::new-val any?)
(s/def ::valid boolean?)
(s/def ::error string?)

(s/def ::change-map (s/keys :req-un [::row-id ::row-data ::col ::new-val]))
(s/def ::validation-and-cast (s/keys :req-un [::valid]
                                     :opt-un [::error ::new-val]))
(s/def ::change-maps (s/coll-of ::change-map))
(s/def ::change-maps-returned (s/coll-of (s/merge ::change-map ::validation-and-cast)))

(defn merge-row-updates
  "Merges `updates` into `rows`.
  Both `updates` and `rows` are a maps where keys are row-ids and vals are rows
  (or row updates) in the case of `updates`."
  [rows updates]
  (let [merged-rows (merge-with merge rows updates)

        ;; Updates will sometimes have nil or "" as the new value for a particular attribute
        ;; in a row. This means the user has entered "" in the cell or has deleted the cell's value.
        ;; For these cases we want to remove these values and their corresponding keys
        ;; from the map representing the row.
        filter-pred #(or (nil? %) (= "" %))
        pairs (for [[row-id row] merged-rows]
                [row-id (medley/remove-vals filter-pred row)])]
    (into {} pairs)))
(s/fdef merge-row-updates :args (s/cat :rows ::db/rows-by-id :updates ::db/rows-by-id)
        :ret ::db/rows-by-id)

(defn validate-and-cast-changes
  "Validates the changes in `change-maps`. If necessary, casts the new value supplied.
  This is done according to the datatype for the column specified in the model-spec."
  [change-maps]
  (let [model-variables (mm.spec/variables model/spec)
        validate-and-cast (fn [index change]
                            (let [{:keys [row-id col new-val]} change
                                  col-type (mm.spec/stattype model/spec col)

                                  ;; Always contains the key :valid -- Change is valid.
                                  ;; May contain key :error -- Error that caused validation to fail.
                                  ;; May contain key :new-val -- New value casted appropriately.
                                  ret (cond
                                        (= col :inferenceql.viz.row/label__)
                                        {:valid true}

                                        (= col-type :gaussian)
                                        (try
                                          (let [new-val-casted (edn/read-string new-val)]
                                            (if (or (number? new-val-casted)
                                                    (nil? new-val-casted))
                                              {:valid true :new-val new-val-casted}
                                              {:valid false :error (gstring/format "The value '%s' is not a number. New values for column '%s' must be numbers." new-val col)}))
                                          (catch ExceptionInfo e
                                            {:valid false :error (ex-message e)}))

                                        (= col-type :categorical)
                                        {:valid true}

                                        :else
                                        {:valid false :error (gstring/format (str "Column \"%s\" is not in the set of model variables: %s. "
                                                                                  "Therefore inserting new values is not allowed.")
                                                                             col model-variables)})]
                              ;; Add in :change-index and new (or updated data) contained in ret.
                              (merge change {:change-index index} ret)))]
    (map-indexed validate-and-cast change-maps)))
(s/fdef validate-and-cast-changes :args (s/cat :change-maps ::change-maps)
        :ret ::change-maps-returned)

(defn assert-permitted-changes
  "Asserts that `changes` only occur in cells and via sources allowed by our Handsontable settings."
  [changes source]
  (let [valid-change-sources #{"edit" "CopyPaste.paste" "Autofill.fill"}]

    ;; Changes should only be happening in the label column or in user-added rows.
    ;; This should be enforced by Hansontable settings.
    (doseq [{:keys [col row-data]} changes]
      (assert (or (= :inferenceql.viz.row/label__ col)
                  (:inferenceql.viz.row/user-added-row__ row-data))))

    ;; Changes should only be the result of user edits, copy paste, or drag and autofill.
    ;; This should be enforced by Hansontable settings.
    (assert (valid-change-sources source))))
;; TODO: why doesn't this work with s/tuple for args? It appears args is coming in as a list not vector.
;; Therefore s/tuple can't match it.?
(s/fdef assert-permitted-changes :args (s/cat :changes ::change-maps :source string?))
