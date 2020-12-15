(ns inferenceql.spreadsheets.panels.viz.subs
  (:require [re-frame.core :as rf]
            [metaprob.prelude :as mp]
            [inferenceql.inference.gpm.multimixture.utils :as mm.utils]
            [inferenceql.inference.gpm.multimixture.search :as search]
            [inferenceql.spreadsheets.model :as model]
            [inferenceql.spreadsheets.panels.viz.vega :as vega]
            [inferenceql.spreadsheets.panels.override.helpers :as co]
            [inferenceql.spreadsheets.panels.table.handsontable :as hot]
            [medley.core :as medley]))

(rf/reg-sub :viz/vega-lite-spec
            :<- [:table/selection-layers-list]
            (fn [selection-layers]
              (clj->js
                (vega/generate-spec selection-layers))))

(defn make-simulate-fn
  [col-to-sim row override-fns]
  (let [override-map (select-keys override-fns [col-to-sim])
        override-insert-fn (co/gen-insert-fn override-map)
        constraints (mm.utils/with-row-values {} (as-> row $
                                                   (select-keys $ (keys (:vars model/spec)))
                                                   (dissoc $ col-to-sim)
                                                   (medley/remove-vals nil? $)))
        _ (.log js/console :col-to-sim col-to-sim)
        _ (.log js/console :constraints constraints)
        gen-fn #(first (mp/infer-and-score :procedure (search/optimized-row-generator model/spec)
                                           :observation-trace constraints))
        has-negative-vals? #(some (every-pred number? neg?) (vals %))]
    ;; returns the first result of gen-fn that doesn't have a negative salary
    ;; TODO: (remove negative-vals? ...) is a hack for StrangeLoop2019
    #(take 1 (map override-insert-fn (remove has-negative-vals? (repeatedly gen-fn))))))

(rf/reg-sub :viz/generators
            :<- [:table/selection-layers]
            :<- [:override/column-override-fns]
            (fn [[layers override-fns]]
              (->> layers
                   (medley/map-vals (fn [layer]
                                      (let [{selections :selections
                                             cols :selected-columns
                                             row :row-at-selection-start} layer]
                                        (when (vega/simulatable? selections cols)
                                          (make-simulate-fn (first cols) row override-fns)))))
                   (medley/remove-vals nil?))))

(rf/reg-sub :viz/pts-store
  (fn [db _]
    (get-in db [:viz-panel :pts-store])))

;; Returns a function that checks whether a data row matches the filtering criteria in `:viz/pts-store`
(rf/reg-sub :viz/pts-store-filter
            :<- [:viz/pts-store]
            (fn [pts-store]
              (when (seq pts-store)
                (let [;; A single entry in pts-store mostly consists of a sequence of field-maps and value
                      ;; sequences. We are mostly combining the respective field maps with their values.
                      join-field-vals (fn [store-entry]
                                        (let [{:keys [fields values]} store-entry]
                                          (for [[field-map vals-seq] (map vector fields values)]
                                            (let [{:keys [field type]} field-map]
                                              {:field (keyword field)
                                               :type type
                                               :vals vals-seq}))))
                      ;; Some entries in pts-store contain a single field-map, vals-seq pair. Some contain multiple.
                      ;; So we flatten them all down here with map cat.
                      entries (mapcat join-field-vals pts-store)

                      ;; Entity "E" typed selections can have multiple entries. This is most often due to selections
                      ;; in choropleths. If this is the case we need to reduce these down to one filter map.
                      groups (group-by (juxt :field :type) entries)

                      filter-maps (for [[[field type] entries] groups]
                                    (case type
                                      ;; "R" Range typed selections should only have one entry.
                                      "R" (do (assert (= 1 (count entries)))
                                              (first entries))
                                      ;; "E" Entity typed selections may have many entries and may need to be reduced.
                                      "E" {:field field
                                           :type  type
                                           :vals (flatten (map :vals entries))}))]
                  (fn [a-row]
                    (let [;; Checks that `a-row` passes the filter represented by `filter-map`.
                          passes-filter? (fn [filter-map]
                                           (let [{:keys [type vals field]} filter-map
                                                 row-val (get a-row field)]
                                             (case type
                                               "R" (let [[low high] (sort vals)]
                                                     (<= low row-val high))
                                               "E" (contains? (set vals) row-val))))]
                      (every? true? (map passes-filter? filter-maps))))))))
