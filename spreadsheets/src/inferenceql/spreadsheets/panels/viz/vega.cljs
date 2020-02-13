(ns inferenceql.spreadsheets.panels.viz.vega
  "Code related to generating vega-lite specs"
  (:require [clojure.walk :as walk]
            [metaprob.distributions :as dist]
            [inferenceql.spreadsheets.model :as model]
            [inferenceql.spreadsheets.panels.table.handsontable :as hot]
            [inferenceql.spreadsheets.panels.table.db :as table-db]))

;; These are column names that cannot be simulated.
;; `hot/label-col-header` and `hot/score-col-header` are not part of any dataset.
;; And `geo-fips` and `NAME` are columns from the NYTimes dataset that have been excluded.
(def cols-invalid-for-sim #{"geo_fips" "NAME" hot/label-col-header hot/score-col-header})

(defn simulatable?
  "Checks if `selection` is valid for simulation"
  [selection col]
  (and (table-db/one-cell-selected? selection)
       (not (contains? cols-invalid-for-sim col))))

(def vega-map-width
  "Width setting for the choropleth specs produced by the :vega-lite-spec sub"
  500)
(def vega-map-height
  "Height setting for the choropleth specs produced by the :vega-lite-spec sub"
  300)

(def vega-strip-plot-quant-size
  "Size of the strip plot for the quantitative dimension"
  350)
(def vega-strip-plot-step-size
  "Width of each band in the strip plot in the categorical dimension"
  30)

(def ^:private topojson-feature "cb_2017_us_cd115_20m")

(def default-table-color "SteelBlue")

(def ^:private default-vega-lite-schema "https://vega.github.io/schema/vega-lite/v4.json")
(def ^:private v3-vega-lite-schema "https://vega.github.io/schema/vega-lite/v3.json")

(defn- left-pad
  [s n c]
  (str (apply str (repeat (max 0 (- n (count s)))
                          c))
       s))

(defn stattype
  [column]
  (let [stattype-kw (if (contains? #{hot/score-col-header hot/label-col-header} column)
                      :gaussian
                      (get-in model/spec [:vars column]))]
    (case stattype-kw
      :gaussian dist/gaussian
      :categorical dist/categorical)))

(defn- vega-type
  "Return a vega-lite type given `col-name` a column name from the table"
  [col-name]
  (condp = (stattype col-name)
    dist/gaussian "quantitative"
    dist/categorical "nominal"))

(defn gen-simulate-plot
  "Generates a vega-lite spec for a histogram of simulated values for a cell.
  `col-name` is the column that the cell is in. And `row` is map of
  data representing the row our cell is in.
  This spec itself does not perform the simulation. An other function must update
  the `data` dataset via the vega-lite API."
  [col-name row]
  (let [col-kw (keyword col-name)
        col-val (get row col-name)
        y-axis {:title "Distribution of Probable Values"
                :grid false
                :labels false
                :ticks false}
        y-scale {:nice false}
        simulations-layer {:mark {:type "bar" :color default-table-color}
                           :encoding (condp = (stattype col-name)
                                       dist/gaussian {:x {:bin true
                                                          :field col-kw
                                                          :type "quantitative"}
                                                      :y {:aggregate "count"
                                                          :type "quantitative"
                                                          :axis y-axis
                                                          :scale y-scale}}
                                       dist/categorical {:x {:field col-kw
                                                             :type "nominal"}
                                                         :y {:aggregate "count"
                                                             :type "quantitative"
                                                             :axis y-axis
                                                             :scale y-scale}})}
        ;; This layer draws a red line on the histogram bin that contains
        ;; the observed value.
        observed-layer {:data {:values [{col-kw col-val}]}
                        :mark {:type "rule"
                               :color "red"}
                        :encoding {:x {:field col-kw
                                       :type (condp = (stattype col-name)
                                               dist/gaussian "quantitative"
                                               dist/categorical "nominal")}}}
        layers (cond-> [simulations-layer]
                 col-val (conj observed-layer))]
    {:data {:name "data"}
     :autosize {:resize true}
     :layer layers}))

(defn get-col-type [col-name]
  (condp = (stattype col-name)
    dist/gaussian "quantitative"
    dist/categorical "nominal"))

(defn get-col-should-bin [col-name]
  (condp = (stattype col-name)
    dist/gaussian true
    dist/categorical false))

(defn gen-histogram [col selections]
  (let [col-type (get-col-type col)
        col-binning (get-col-should-bin col)]
    {:data {:values selections}
     :mark {:type "bar" :color default-table-color}
     :encoding {:x {:bin col-binning
                    :field col
                    :type col-type}
                :y {:aggregate "count"
                    :type "quantitative"}}}))

(defn gen-choropleth [selections selected-columns]
  (let [selection (first selections)
        map-column (first (filter #(not= "geo_fips" %) selected-columns))
        transformed-selection (mapv (fn [row]
                                      (update row "geo_fips" #(left-pad (str %) 4 \0)))
                                    selection)
        name {:field "NAME"
              :type "nominal"}
        color {:field map-column
               :type (condp = (stattype map-column)
                       dist/gaussian "quantitative"
                       dist/categorical "nominal")}]
    {:$schema v3-vega-lite-schema
     :width vega-map-width
     :height vega-map-height
     :data {:values js/topojson
            :format {:type "topojson"
                     :feature topojson-feature}}
     :transform [{:lookup "properties.GEOID"
                  :from {:data {:values transformed-selection}
                         :key "geo_fips"
                         "fields" [(:field name) (:field color)]}}]
     :projection {:type "albersUsa"}
     :mark "geoshape"
     :encoding {:tooltip [name color]
                :color color}}))

(defn- scatter-plot
  "Generates vega-lite spec for a scatter plot.
  Useful for comparing quatitative-quantitative data."
  [data cols-to-draw]
  {:data {:values data}
   :mark "circle"
   :encoding {:x {:field (first cols-to-draw)
                  :type "quantitative"}
              :y {:field (second cols-to-draw)
                  :type "quantitative"}}})

(defn- heatmap-plot
  "Generates vega-lite spec for a heatmap plot.
  Useful for comparing nominal-nominal data."
  [data cols-to-draw facet-column]
  (let [spec {:$schema v3-vega-lite-schema
              :data {:values data}
              :mark "rect"
              :encoding {:x {:field (first cols-to-draw)
                             :type "nominal"}
                         :y {:field (second cols-to-draw)
                             :type "nominal"}
                         :color {:aggregate "count"
                                 :type "quantitative"}}}]
    (if facet-column
      (assoc-in spec [:encoding :facet] {:field facet-column :type "nominal"})
      spec)))

(defn- box-and-line-plot
  "Generates vega-lite spec for a box-and-line plot.
  Useful for comparing quantitative-nominal data."
  [data cols-to-draw facet-column]
  (let [col-1-type (condp = (stattype (first cols-to-draw))
                     dist/gaussian "quantitative"
                     dist/categorical "nominal")
        col-2-type (condp = (stattype (second cols-to-draw))
                     dist/gaussian "quantitative"
                     dist/categorical "nominal")
        spec {:$schema v3-vega-lite-schema
              :data {:values data}
              :mark {:type "boxplot"
                     :extent "min-max"}
              :encoding {:x {:field (first cols-to-draw)
                             :type col-1-type}
                         :y {:field (second cols-to-draw)
                             :type col-2-type}
                         :color {:aggregate "count"
                                 :type "quantitative"}}}]
    (if facet-column
      (assoc-in spec [:encoding :facet] {:field facet-column :type "nominal"})
      spec)))

(defn- strip-plot-size-helper
  "Return a vega-lite height/width size setting given `col-name` a column name from the table"
  [col-name]
  (condp = (stattype col-name)
    dist/gaussian vega-strip-plot-quant-size
    dist/categorical {:step vega-strip-plot-step-size}))

(defn- strip-plot
  "Generates vega-lite spec for a strip plot.
  Useful for comparing quantitative-nominal data."
  [data cols-to-draw]
  (let [[x-field y-field] cols-to-draw
        [x-type y-type] (map vega-type cols-to-draw)
        [width height] (map strip-plot-size-helper cols-to-draw)]
    {:width width
     :height height
     :data {:values data}
     :mark {:type "tick"}
     :encoding {:x {:field x-field
                    :type x-type
                    :axis {:grid true :gridDash [2 2]}}
                :y {:field y-field
                    :type y-type
                    :axis {:grid true :gridDash [2 2]}}}}))

(defn- table-bubble-plot
  "Generates vega-lite spec for a table-bubble plot.
  Useful for comparing nominal-nominal data."
  [data cols-to-draw]
  (let [[x-field y-field] cols-to-draw]
    {:data {:values data}
     :mark {:type "circle"}
     :encoding {:x {:field x-field
                    :type "nominal"}
                :y {:field y-field
                    :type "nominal"}
                :size {:aggregate "count"
                       :type "quantitative"}}}))

(defn gen-comparison-plot [cols selections]
  (let [cols-types (set (doall (map stattype cols)))]
    (condp = cols-types
      #{dist/gaussian} (scatter-plot selections cols)
      #{dist/categorical} (table-bubble-plot selections cols)
      #{dist/gaussian dist/categorical} (strip-plot selections cols))))

(defn- spec-for-selection-layer [selection-layer]
  (let [{layer-name :id
         selections :selections
         cols :selected-columns
         row :row-at-selection-start} selection-layer]
    (cond (simulatable? selections (first cols))
          (gen-simulate-plot (first cols) row)

          (= 1 (count cols)) ; One column selected.
          (gen-histogram (first cols) selections)

          :else ; Two or more columns selected.
          (gen-comparison-plot (take 2 cols) selections))))

(defn generate-spec [selection-layers]
  (let [spec-layers (mapv spec-for-selection-layer selection-layers)]
    (when (not-empty spec-layers)
      {:$schema default-vega-lite-schema
       :hconcat spec-layers})))
