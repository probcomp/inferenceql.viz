(ns inferenceql.spreadsheets.panels.viz.vega
  "Code related to generating vega-lite specs"
  (:require [clojure.walk :as walk]
            [clojure.string :as string]
            [inferenceql.spreadsheets.model :as model]
            [inferenceql.spreadsheets.panels.table.handsontable :as hot]
            [inferenceql.spreadsheets.panels.table.db :as table-db]
            [inferenceql.spreadsheets.config :as config]
            [goog.string :as gstring]
            [medley.core :as medley]))

;; These are defs related to choropleth related columns in the dataset.
;; See spreadsheets/resources/config.edn for more info.
(def ^:private geo-id-col (keyword (get-in config/config [:geo :table-geo-id-col])))

(def vega-map-width
  "Width setting for the choropleth specs produced by the :vega-lite-spec sub"
  700)
(def vega-map-height
  "Height setting for the choropleth specs produced by the :vega-lite-spec sub"
  700)

(def vega-strip-plot-quant-size
  "Size of the strip plot for the quantitative dimension"
  350)
(def vega-strip-plot-step-size
  "Width of each band in the strip plot in the categorical dimension"
  30)

(def default-table-color "SteelBlue")

(defn- title-color [layer-name]
  "Maps the name of a selection layer, `layer-name`, to a color for its plot title."
  (case layer-name
        :blue "blue"
        :green "green"
        :red "red"
        "black"))

(def ^:private default-vega-lite-schema "https://vega.github.io/schema/vega-lite/v4.json")
(def ^:private v3-vega-lite-schema "https://vega.github.io/schema/vega-lite/v3.json")

(defn- left-pad
  [s n c]
  (str (apply str (repeat (max 0 (- n (count s)))
                          c))
       s))

(defn probability-column? [column]
  "Returns whether a `column` was the result of probability-of statement.
  `column` is the name of the column."
  (when column
    (some? (re-matches #"^prob[\w\-]*$" (name column)))))

(defn stat-type
  "Returns a multi-mixture stat-type given a column name from the data table."
  [col-name]
  (get-in model/spec [:vars col-name]))

(defn present-in-model?
  "Returns whether `col-name` is present in the loaded model."
  [col-name]
  (some? (stat-type col-name)))

(defn vega-type
  "Returns a vega-lite type given `col-name`, a column name from the data table.
  May return nil if multi-mix stat-type for `col-name` can`t be found."
  [col-name]
  (cond (probability-column? col-name)
        "quantitative"

        (contains? #{hot/label-col-header geo-id-col} col-name)
        "nominal"

        :else
        ;; Mapping from multi-mix stat-types to vega-lite data-types.
        (let [mapping {:gaussian "quantitative"
                       :categorical "nominal"}]
          (get mapping (stat-type col-name)))))

(defn should-bin?
  "Returns whether data for a certain column should be binned in a vega-lite spec."
  [col-name]
  (case (vega-type col-name)
        "quantitative" true
        "nominal" false
        false))

(defn simulatable?
  "Checks if `selections` and `cols` are valid for simulation"
  [selections cols]
  (and (= 1 (count selections)) ; Single row selected.
       (= 1 (count cols)) ; Single column selected.
       (present-in-model? (first cols))))

(defn gen-simulate-plot
  "Generates a vega-lite spec for a histogram of simulated values for a cell.
  `col-name` is the column that the cell is in. And `row` is map of
  data representing the row our cell is in.
  This spec itself does not perform the simulation. An other function must update
  the `data` dataset via the vega-lite API."
  [col-name row dataset-name]
  (let [col-kw (keyword col-name)
        col-val (get row col-name)
        y-axis {:title "Distribution of Probable Values"
                :grid false
                :labels false
                :ticks false}
        y-scale {:nice false}
        simulations-layer {:mark {:type "bar" :color default-table-color}
                           :encoding {:x {:bin (should-bin? col-name)
                                          :field col-kw
                                          :type (vega-type col-name)}
                                      :y {:aggregate "count"
                                          :type "quantitative"
                                          :axis y-axis
                                          :scale y-scale}}}
        ;; This layer draws a red line on the histogram bin that contains
        ;; the observed value.
        observed-layer {:data {:values [{col-kw col-val}]}
                        :mark {:type "rule"
                               :color "red"}
                        :encoding {:x {:field col-kw
                                       :type (vega-type col-name)}}}
        layers (cond-> [simulations-layer]
                 col-val (conj observed-layer))]
    {:data {:name dataset-name}
     :layer layers}))

(defn gen-histogram [col selections]
  {:data {:values selections}
   :mark {:type "bar" :color default-table-color}
   :encoding {:x {:bin (should-bin? col)
                  :field col
                  :type (vega-type col)}
              :y {:aggregate "count"
                  :type "quantitative"}}})

(defn gen-choropleth [selections selected-columns]
  ;; TODO: Add a spec for topojson config map.
  (when-let [geo-config (get config/config :geo)]
    (let [color-by-col (first (filter #(not= geo-id-col %) ; The other column selected, if any.
                                      selected-columns))

          pad-fips (fn [v] (left-pad v (get geo-config :fips-code-length) \0))
          rows-cleaned (cond->> selections
                                (probability-column? color-by-col)
                                ;; Remove rows with probability values of 1.
                                (remove #(= (get % color-by-col) 1.0))

                                (some? (get geo-config :fips-code-length))
                                ;; Add padding to fips codes.
                                (mapv #(medley/update-existing % geo-id-col pad-fips)))

          data-format (case (get geo-config :filetype)
                            :geojson {:property "features"}
                            :topojson {:type "topojson"
                                       :feature (get geo-config :feature)})

          spec {:$schema default-vega-lite-schema
                :width vega-map-width
                :height vega-map-height
                :data {:values (get geo-config :data)
                       :format data-format}
                :transform [{:lookup (get geo-config :prop)
                             :from {:data {:values rows-cleaned}
                                    :key geo-id-col}
                             :as "row"}
                            ;; We filter entities in the topojson that did not join on a row
                            ;; in `rows-cleaned`.
                            {:filter "datum.row"}]
                :projection {:type (get geo-config :projection-type)}
                :mark {:type "geoshape"
                       :color "#eee"
                       :stroke "#757575"
                       :strokeWidth "0.5"}
                       ;;:tooltip {:content "data"}}
                :encoding {:tooltip {:field "row"
                                     ;; This field is actually an object, but specifying type
                                     ;; nominal here to remove vega-tooltip warning message.
                                     :type "nominal"}}}]

      ;; If we have another column selected besides `geo-id-col`,
      ;; color the choropleth according to the values in that column, `color-by-col`.
      (if-not color-by-col
        spec
        (assoc-in spec [:encoding :color]
                       {:field (str "row." (name color-by-col))
                        :type (vega-type color-by-col)
                        :scale {:type "quantize"
                                :range ["#f2f2f2" "#f4e5d2" "#fed79c" "#fca52a" "#ff6502"]}})))))

(defn- scatter-plot
  "Generates vega-lite spec for a scatter plot.
  Useful for comparing quatitative-quantitative data."
  [data cols-to-draw]
  (let [zoom-control-name (keyword (gensym "zoom-control"))] ; Random id so pan/zoom is independent.
    {:data {:values data}
     :mark {:type "circle" :tooltip {:content "data"}}
     ;:selection {zoom-control-name {:type "interval" :bind "scales"}}
     :encoding {:x {:field (first cols-to-draw)
                    :type "quantitative"}
                :y {:field (second cols-to-draw)
                    :type "quantitative"}}}))


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
  (let [col-1 (first cols-to-draw)
        col-2 (second cols-to-draw)
        spec {:$schema v3-vega-lite-schema
              :data {:values data}
              :mark {:type "boxplot"
                     :extent "min-max"}
              :encoding {:x {:field col-1
                             :type (vega-type col-1)}
                         :y {:field col-2
                             :type (vega-type col-2)}
                         :color {:aggregate "count"
                                 :type "quantitative"}}}]
    (if facet-column
      (assoc-in spec [:encoding :facet] {:field facet-column :type "nominal"})
      spec)))

(defn- strip-plot-size-helper
  "Return a vega-lite height/width size setting given `col-name` a column name from the table"
  [col-name]
  (case (vega-type col-name)
        "quantitative" vega-strip-plot-quant-size
        "nominal" {:step vega-strip-plot-step-size}))

(defn- strip-plot
  "Generates vega-lite spec for a strip plot.
  Useful for comparing quantitative-nominal data."
  [data cols-to-draw]
  (let [zoom-control-name (keyword (gensym "zoom-control")) ; Random id so pan/zoom is independent.
        [x-field y-field] cols-to-draw
        [x-type y-type] (map vega-type cols-to-draw)
        quant-dimension (if (= x-type "quantitative") :x :y)
        [width height] (map strip-plot-size-helper cols-to-draw)]
    {:width width
     :height height
     :data {:values data}
     :mark {:type "tick" :tooltip {:content "data"}}
     ;:selection {zoom-control-name {:type "interval" :bind "scales" :encodings [quant-dimension]}}
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
  (let [cols-types (set (doall (map vega-type cols)))]
    (condp = cols-types
           #{"quantitative"} (scatter-plot selections cols)
           #{"nominal"} (table-bubble-plot selections cols)
           #{"quantitative" "nominal"} (strip-plot selections cols))))

(defn- spec-for-selection-layer [selection-layer]
  (let [{layer-name :id
         selections :selections
         cols :selected-columns
         row :row-at-selection-start} selection-layer]
    ;; Only produce a spec when we can find a vega-type for all selected columns.
    (when (every? some? (map vega-type cols))
      (let [spec (cond (some #{geo-id-col} cols) ; Fips column selected.
                       (gen-choropleth selections cols)

                       (simulatable? selections cols)
                       (gen-simulate-plot (first cols) row (name layer-name))

                       (= 1 (count cols)) ; One column selected.
                       (gen-histogram (first cols) selections)

                       :else ; Two or more columns selected.
                       (gen-comparison-plot (take 2 cols) selections))
             title {:title {:text (str (name layer-name) " " "selection")
                            :color (title-color layer-name)
                            :fontWeight 500}}]
        (merge spec title)))))

(defn generate-spec [selection-layers]
  (when-let [spec-layers (seq (keep spec-for-selection-layer selection-layers))]
    {:$schema default-vega-lite-schema
     :hconcat spec-layers
     :resolve {:legend {:size "independent"
                        :color "independent"}
               :scale {:color "independent"}}}))
