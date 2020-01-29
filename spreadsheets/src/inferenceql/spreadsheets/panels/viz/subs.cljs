(ns inferenceql.spreadsheets.panels.viz.subs
  (:require [re-frame.core :as rf]
            [metaprob.prelude :as mp]
            [inferenceql.multimixture :as mmix]
            [inferenceql.multimixture.search :as search]
            [inferenceql.spreadsheets.model :as model]
            [inferenceql.spreadsheets.panels.viz.vega :as vega]
            [inferenceql.spreadsheets.panels.override.helpers :as co]))

(defn vega-lite-spec
  [{:keys [table-states t-clicked]}]
  (let [{selections :selections cols :selected-columns row :row-at-selection-start} (table-states t-clicked)
        ;; These are column names that cannot be simulated
        ;; `vega/label-col-header` and `vega/score-col-header` are not part of any dataset.
        ;; And `geo-fips` and `NAME` are columns from the NYTimes dataset that have been excluded.
        invalid-for-sim #{"geo_fips" "NAME" vega/label-col-header vega/score-col-header}]
    (when (first selections)
      (clj->js
       (cond (and (= 1 (count cols))
                  (= 1 (count (first selections)))
                  (not (invalid-for-sim (first cols))))
             (vega/gen-simulate-plot cols row t-clicked)

             (= 1 (count cols))
             (vega/gen-histogram table-states t-clicked)

             (some #{"geo_fips"} cols)
             (vega/gen-choropleth selections cols)

             :else
             (vega/gen-comparison-plot table-states t-clicked))))))
(rf/reg-sub :vega-lite-spec
            (fn [_ _]
              {:table-states (rf/subscribe [:both-table-states])
               :t-clicked (rf/subscribe [:table-last-clicked])})
            (fn [data-for-spec]
              (vega-lite-spec data-for-spec)))

(rf/reg-sub :vega-lite-log-level
            :<- [:one-cell-selected]
            (fn [one-cell-selected]
              (if one-cell-selected
                (.-Error js/vega)
                (.-Warn js/vega))))

(rf/reg-sub :generator
            (fn [_ _]
              {:selection-info (rf/subscribe [:table-state-active])
               :one-cell-selected (rf/subscribe [:one-cell-selected])
               :override-fns (rf/subscribe [:column-override-fns])})
            (fn [{:keys [selection-info one-cell-selected override-fns]}]
              (let [row (:row-at-selection-start selection-info)
                    columns (:selected-columns selection-info)
                    col-to-sample (first columns)
                    override-map (select-keys override-fns [col-to-sample])
                    override-insert-fn (co/gen-insert-fn override-map)]
                (when (and one-cell-selected
                           ;; TODO clean up this check
                           (not (contains? #{"geo_fips" "NAME" vega/score-col-header vega/label-col-header} col-to-sample)))
                  (let [constraints (mmix/with-row-values {} (-> row
                                                                 (select-keys (keys (:vars model/spec)))
                                                                 (dissoc col-to-sample)))
                        gen-fn #(first (mp/infer-and-score :procedure (search/optimized-row-generator model/spec)
                                                           :observation-trace constraints))
                        has-negative-vals? #(some (every-pred number? neg?) (vals %))]
                    ;; returns the first result of gen-fn that doesn't have a negative salary
                    ;; TODO: (remove negative-vals? ...) is a hack for StrangeLoop2019
                    #(take 1 (map override-insert-fn (remove has-negative-vals? (repeatedly gen-fn)))))))))
