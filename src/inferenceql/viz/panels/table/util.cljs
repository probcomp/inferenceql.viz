(ns inferenceql.viz.panels.table.util
  (:require [medley.core :as medley]))

(defn merge-row-updates
  "Merges `updates` into `rows`.
  Both `updates` and `rows` are a maps where keys are row-ids and vals are rows
  (or row updates) in the case of `updates`."
  [rows updates]
  (let [merge-op (fnil (partial merge-with merge) {} {})
        merged-rows (merge-op rows updates)

        ;; Updates will sometimes have nil or "" as the new value for a particular attribute
        ;; in a row. This means the user has entered "" in the cell or has deleted the cell's value.
        ;; For these cases we want to remove these values and their corresponding keys
        ;; from the map representing the row.
        empty-cell? #(or (nil? %) (= "" %))]

    (reduce-kv (fn [acc row-id row]
                 (let [clean-row (medley/remove-vals empty-cell? row)]
                   (if (seq clean-row)
                     (assoc acc row-id clean-row)
                     acc)))
               {}
               merged-rows)))

