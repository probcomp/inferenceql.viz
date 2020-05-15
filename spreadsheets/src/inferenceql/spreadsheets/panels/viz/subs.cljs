(ns inferenceql.spreadsheets.panels.viz.subs
  (:require [re-frame.core :as rf]
            [metaprob.prelude :as mp]
            [inferenceql.inference.multimixture :as mmix]
            [inferenceql.inference.multimixture.search :as search]
            [inferenceql.spreadsheets.model :as model]
            [inferenceql.spreadsheets.panels.viz.vega :as vega]
            [inferenceql.spreadsheets.panels.override.helpers :as co]
            [inferenceql.spreadsheets.panels.table.handsontable :as hot]
            [medley.core :as medley]))

(rf/reg-sub :viz/vega-lite-spec
            :<- [:table/selection-layers-list]
            (fn [selection-layers]
              (let [spec (clj->js (vega/generate-spec selection-layers))]
                (.log js/console "spec: " spec)
                spec)))


(defn make-simulate-fn
  [col-to-sim row override-fns]
  (let [override-map (select-keys override-fns [col-to-sim])
        override-insert-fn (co/gen-insert-fn override-map)
        constraints (mmix/with-row-values {} (-> row
                                                 (select-keys (keys (:vars model/spec)))
                                                 (dissoc col-to-sim)))
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
                (let [filter-maps (if (= 1 (count pts-store))
                                    (let [s (first pts-store)]
                                      (map (fn [[fields vals]]
                                             {:field (get fields "field")
                                              :type (get fields "type")
                                              :vals vals})
                                           (map vector (get s "fields") (get s "values"))))
                                    (let [s (first (get (first pts-store) "fields"))
                                          vals (mapcat #(get % "values") pts-store)]
                                      (assert (= 1 (count (distinct (map #(get-in % ["fields" "field"]) pts-store)))))
                                      (assert (= 1 (count (distinct (map #(get-in % ["fields" "type"]) pts-store)))))
                                      [{:field (get s "field")
                                        :type (get s "type")
                                        :vals vals}]))]
                  (.log js/console "filter-maps" filter-maps)
                  (fn [a-row]
                    ;(.log js/console "a-row: " a-row)
                    (let [passes-filter? (fn [filter-map]
                                           (case (:type filter-map)
                                             "R" (let [[low high] (sort (:vals filter-map))
                                                       row-val (get a-row (:field filter-map))]
                                                   (<= low row-val high))
                                             "E" (let [row-val (get a-row (:field filter-map))]
                                                   (contains? (set (:vals filter-map)) row-val))))]
                      ;(.log js/console "filter checks: " (map passes-filter? filter-maps))
                      (every? true? (map passes-filter? filter-maps))))))))



