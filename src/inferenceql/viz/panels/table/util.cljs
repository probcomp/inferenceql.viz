(ns inferenceql.viz.panels.table.util)

(defn merge-row-updates
  "Merges `updates` into `rows`.
  Both `updates` and `rows` are a maps where keys are row-ids and vals are rows
  (or row updates) in the case of `updates`."
  [rows updates]
  (let [merge-op (fnil (partial merge-with merge) {} {})]
    (merge-op rows updates)))

(defn column-settings [headers]
  "Returns an array of objects that define settings for each column
  in the table including which attribute from the underlying map for the row
  is presented."
  (let [settings-map (fn [attr]
                       {:data attr})]
    (map settings-map headers)))
