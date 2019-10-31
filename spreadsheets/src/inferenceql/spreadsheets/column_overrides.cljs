(ns inferenceql.spreadsheets.column-overrides)

(defn gen-insert-fn
  "Returns a function that takes `row` and applies the given overrides.
  Overrides as defined in `override-map`. This does not take into
  account what order the overrides get applied. For now we assume no
  dependencies, so we can apply them in any order."
  [override-map]
  (fn [row]
    (reduce-kv (fn [row oride-col oride-fn]
                 (let [func-deps (js/getArgs oride-fn)
                       dep-vals (map #(get row %) func-deps)
                       new-col-val (apply oride-fn dep-vals)]
                   (assoc row oride-col new-col-val)))
               row
               override-map)))
