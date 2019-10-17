(ns inferenceql.spreadsheets.column-overrides)

(defn gen-insert-fn
  "Returns a function that takes `row` and applies the given overrides.
  Overrides as defined in `override-map`. This does not take into
  account what order the overrides get applied. For now we assume no
  dependencies, so we can apply them in any order."
  [override-map]
  (fn [row]
    (loop [[[oride-col oride-fn] & orides-rem] override-map
           row-state row]
      (if (and oride-col oride-fn)
        (let [deps (js/getArgs oride-fn)
              dep-vals (map #(get row-state %) deps)
              new-val (apply oride-fn dep-vals)
              new-row-state (assoc row-state oride-col new-val)]
          (recur orides-rem new-row-state))
        row-state))))
