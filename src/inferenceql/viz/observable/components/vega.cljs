(ns inferenceql.viz.observable.components.vega
  (:require [inferenceql.viz.panels.viz.vega :as app-vega]))

(defn vega-type-fn
  "Given a `schema`, returns a vega-type function.

  Args:
    schema: (map) Mapping from column name to iql stat-type.

  Returns: (a function) Which returns a vega-lite type given `col-name`, a column name
    from the data table. Returns nil if vega-lite type can't be deterimend."
  [schema data]
  (fn [col-name]
    (cond (app-vega/probability-column? col-name)
          "quantitative"

          :else
          (let [ ;; Mapping from multi-mix stat-types to vega-lite data-types.
                mapping {:numerical "quantitative"
                         :nominal "nominal"}

                iql-type (or (->> (keyword col-name)
                                  (get schema)
                                  (keyword))
                             ;; Assume unknown columns are numerical.
                             :numerical)]
            (get mapping iql-type)))))

(defn- spec-for-selection-layer [schema data cols]
  (let [vega-type (vega-type-fn schema data)]
    ;; Only produce a spec when we can find a vega-type for all selected columns
    ;; except the geo-id-col which we handle specially.
    (when (every? some? (map vega-type cols))
      (cond (= 1 (count cols)) ; One column selected.
            (app-vega/gen-histogram (first cols) data vega-type)

            :else ; Two or more columns selected.
            (app-vega/gen-comparison-plot (take 2 cols) data vega-type)))))

(defn generate-spec [schema data selections]
  (when-let [spec-layers (seq (keep #(spec-for-selection-layer schema data %)
                                    selections))]
    {:$schema app-vega/default-vega-lite-schema
     :concat spec-layers
     :columns 2
     :resolve {:legend {:size "independent"
                        :color "independent"}
               :scale {:color "independent"}}}))
