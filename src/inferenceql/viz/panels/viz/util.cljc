(ns inferenceql.viz.panels.viz.util
  "Supporting code for producing vega-lite specs."
  (:require [medley.core :as medley]))

(def vl5-schema "https://vega.github.io/schema/vega-lite/v5.json")

(def obs-data-color "#4e79a7") ;; Tableau-10 Blue
(def virtual-data-color "#f28e2b") ;; Tableau-10 Orange
(def unselected-color "lightgrey")
(def regression-color "black")

(defn vega-type-fn
  "Given a `schema`, returns a vega-type function.

  Args:
    schema: (map) Mapping from column name to iql stat-type.

  Returns: (a function) Which returns a vega-lite type given `col-name`, a column name
    from the data table. Returns nil if vega-lite type can't be deterimend."
  [schema]
  (fn [col-name]
    (let [;; Ensure schema's columns names are strings in order to
          ;; be more permissive.
          schema (medley/map-keys name schema)
          ;; Mapping from multi-mix stat-types to vega-lite data-types.
          mapping {:numerical "quantitative"
                   :nominal "nominal"}]
      (get mapping (get schema (name col-name))))))

(defn should-bin?
  "Returns whether data for a certain column should be binned in a vega-lite spec.

  Args:
    col-type: A vega-lite column type."
  [col-type]
  (case col-type
    "quantitative" {:maxbins 30}
    "nominal" false
    false))

(defn filtering-summary
  "Takes a collection of `samples` and returns information useful for filtering nulls.

  Args:
    cols - a sequence of column names as keywords.
    vega-type - a function to go from column name to vega-type.
    n-cats - number of categories to permit for categorical (nominal) columns.
    samples - a sequence of samples.

  Returns information on the number of samples that have valid values, where valid means not-null
  and also falling within the top-n category values (for categorical columns). Also returns the
  top-n and below top-n category values for categorical columns.

  Returns:
    A map with
      :top-cats - mapping from column-name to the n-cat most frequent category values.
      :rem-cats - mapping from column-name to the category values not in :top-cats
      :num-valid - the number of samples that have valid values for all the columns in `cols`.
      :num-invalid - the number of samples that do not have valid values for all `cols`."
  [cols vega-type n-cats samples]
  (let [;; Only work with observed data.
        samples (filter (comp #{"observed"} :collection) samples)
        split-cats (fn [col]
                     (let [freqs (->> samples
                                      (map col)
                                      (remove nil?)
                                      (frequencies))
                           cats-sorted (->> freqs
                                            (sort-by second >)
                                            (map first))]
                       (if n-cats
                         (split-at n-cats cats-sorted)
                         [cats-sorted []])))
        nom-cols (filter (comp #{"nominal"} vega-type) cols)
        split-cats-seq (map split-cats nom-cols)

        top-cats (zipmap nom-cols (map first split-cats-seq))
        rem-cats (zipmap nom-cols (map second split-cats-seq))

        col-tests (for [col cols]
                    (case (vega-type col)
                      "nominal"
                      (let [cats (get top-cats col)]
                        (comp some? (set cats) col))
                      "quantitative"
                      (comp some? col)))
        valid (apply every-pred col-tests)
        valid-samples (filter valid samples)
        num-valid (count valid-samples)]
    {:top-cats top-cats
     :rem-cats rem-cats
     :num-valid num-valid
     :num-invalid (- (count samples) num-valid)}))
