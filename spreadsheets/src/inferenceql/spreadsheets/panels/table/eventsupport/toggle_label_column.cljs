(ns inferenceql.spreadsheets.panels.table.eventsupport.toggle-label-column
  (:require [medley.core :as medley]))

(defn shift-sort
  "Shift the column numbers in `sort-config` by `amount`."
  [sort-config amount]
  (when sort-config
    (mapv #(update % :column + amount) sort-config)))

(defn shift-selections
  "Shift the column numbers in layers in `selection-layers` by `amount`."
  [selection-layers amount]
  (let [shift-cols (fn [[r1 c1 r2 c2]]
                     [r1 (+ c1 amount) r2 (+ c2 amount)])
        shift-layer (fn [layer-map]
                      (-> layer-map
                          (update :coords (partial mapv shift-cols))))]
    (medley/map-vals shift-layer selection-layers)))

(defn adjust-headers
  "Adds or removes a dummy column to the front of `headers` depending on `amount`."
  [headers amount]
  (case amount
    1 (into [:dummy-col] headers)
    -1 (subvec headers 1)))

