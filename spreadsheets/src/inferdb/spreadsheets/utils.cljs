(ns inferdb.spreadsheets.utils)

(defn gen-insert-fn [override-map]
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
