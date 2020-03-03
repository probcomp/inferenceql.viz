(ns inferenceql.spreadsheets.panels.viz.vega
  "Code related to generating vega-lite specs"
  (:require [clojure.walk :as walk]
            [metaprob.distributions :as dist]
            [inferenceql.spreadsheets.model :as model]
            [inferenceql.spreadsheets.panels.table.handsontable :as hot]
            [inferenceql.spreadsheets.panels.table.db :as table-db]
            [goog.string :as gstring]))

(def fips-col "country")
(def map-names-col "country")
(def topojson-prop "properties.geounit")
(def ^:private topojson-feature "countries")

;; These are column names that cannot be simulated.
;; `hot/label-col-header` and `hot/score-col-header` are not part of any dataset.
;; And `geo-fips` and `NAME` are columns from the NYTimes dataset that have been excluded.
(def cols-invalid-for-sim #{fips-col map-names-col hot/label-col-header hot/score-col-header})

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

(defn stattype
  [column]
  (let [stattype-kw (cond (contains? #{hot/score-col-header hot/label-col-header} column)
                          :gaussian

                          :else
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

(defn gen-simulate-plot-vega-lite
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
    {:data {:name dataset-name}
     :layer layers
     :autosize {:resize true}}))

(defn gen-simulate-plot
  [col row dataset-name]
  (let [domain-vals (get-in model/spec [:categories col])]
    {:$schema "https://vega.github.io/schema/vega/v5.json",
     :width 200,
     :height 200,
     :autosize "pad",

     :signals
     [{:name "padAngle", :value 0}
      {:name "innerRadius", :value 0}
      {:name "cornerRadius", :value 0}
      {:name "sort" :value false}]

     :data
     [{:name dataset-name,
       :transform
       [{:type "aggregate"
         :groupby [col]}
        {:type "pie",
          :field "count",
          :startAngle 0,
          :endAngle 6.29,}]}]

     :scales
     [{:name "color",
       :type "ordinal",
       :domain domain-vals
       :range {:scheme "category20"},}]

     :legends
     [{:fill "color",
       :title (str "Simulations: " col)
       :orient "right",
       :encode
       {:symbols
        {:enter {:fillOpacity {:value 1.0}}},
        :labels
        {:update {:text {:field "value"}}}}}],

     :marks
     [{:type "arc",
       :from {:data dataset-name},
       :encode
       {:enter
        {:fill {:scale "color", :field col},
         :x {:signal "width / 2"},
         :y {:signal "height / 2"},
         :tooltip {:signal (gstring/format "{'value': datum['%s'], 'count': datum.count}" col)}}
        :update
        {:startAngle {:field "startAngle"},
         :endAngle {:field "endAngle"},
         :padAngle {:signal "padAngle"},
         :innerRadius {:signal "innerRadius"},
         :outerRadius {:signal "width / 2"},
         :tooltip {:signal (gstring/format "{'value': datum['%s'], 'count': datum.count}" col)}
         :cornerRadius
         {:signal "cornerRadius"}}}}]}))

(defn get-col-type [col-name]
  (condp = (stattype col-name)
    dist/gaussian "quantitative"
    dist/categorical "nominal"))

(defn get-col-should-bin [col-name]
  (condp = (stattype col-name)
    dist/gaussian true
    dist/categorical false))

(defn gen-histogram-vega-lite [col selections]
  (let [col-type (get-col-type col)
        col-binning (get-col-should-bin col)]
    {:data {:values selections}
     :mark {:type "bar" :color default-table-color}
     :encoding {:x {:bin col-binning
                    :field col
                    :type col-type}
                :y {:aggregate "count"
                    :type "quantitative"}}}))

(defn gen-histogram [col selections]
  (let [col-type (get-col-type col)
        col-binning (get-col-should-bin col)
        attach-null? (some nil? (map #(get % col) selections))

        domain-vals (cond-> (vec (get-in model/spec [:categories col]))
                            attach-null? (conj "Null"))
        selections (map (fn [row] (if (nil? (get row col))
                                    (assoc row col "Null")
                                    row))
                        selections)]
    {:$schema "https://vega.github.io/schema/vega/v5.json",
     :width 200,
     :height 200,
     :autosize "pad",

     :signals
     [{:name "padAngle", :value 0}
      {:name "innerRadius", :value 0}
      {:name "cornerRadius", :value 0}
      {:name "sort" :value false}]

     :data
     [{:name "table",
       :values selections
       :transform
       [{:type "aggregate"
         :groupby [col]}
        {:type "collect"
         :sort {:field col :order "descending"}}
        {:type "pie",
          :field "count",
          :startAngle 0,
          :endAngle 6.29,
          :sort {:signal "sort"}}]}]

     :scales
     [{:name "color",
       :type "ordinal",
       :domain domain-vals
       :range {:scheme "category20"},}]

     :legends
     [{:fill "color",
       :title col,
       :orient "right",
       :encode
       {:symbols
        {:enter {:fillOpacity {:value 1.0}}},
        :labels
        {:update {:text {:field "value"}}}}}],

     :marks
     [{:type "arc",
       :from {:data "table"},
       :encode
       {:enter
        {:fill {:scale "color", :field col},
         :x {:signal "width / 2"},
         :y {:signal "height / 2"},
         :tooltip {:signal (gstring/format "{'value': datum['%s'], 'count': datum.count}" col)}}
        :update
        {:startAngle {:field "startAngle"},
         :endAngle {:field "endAngle"},
         :padAngle {:signal "padAngle"},
         :innerRadius {:signal "innerRadius"},
         :outerRadius {:signal "width / 2"},
         :cornerRadius
         {:signal "cornerRadius"}}}}
      {:type "text",
       :from {:data "table"},
       :encode
       {:enter
        {:x {:signal "width / 2"},
         :y {:signal "height / 2"},
         :theta
         {:signal
          "(datum['startAngle'] + datum['endAngle'])/2"},
         :radius
         {:signal "(width / 2) + 10"}
         :baseline {:value "middle"},
         :align {:value "center"},
         :fill {:value "black"},
         :fontSize {:value 12},
         :text {:signal "datum.count"}
         :tooltip ""}}}]}))

(defn gen-choropleth [selections selected-columns]
  (let [correct-names {"Congo DR" "Democratic Republic of the Congo"
                       "Tanzania, United Republic of" "Tanzania"
                       "Cote d'Ivoire" "Ivory Coast"}
        update-fn (fn [v] (get correct-names v v))
        transformed-selection (mapv (fn [row] (update row fips-col update-fn)) selections)

        spec {:$schema default-vega-lite-schema
              :width vega-map-width
              :height vega-map-height
              :data {:values js/topojson
                     :format {:type "topojson"
                              :feature topojson-feature}}
              :transform [{:lookup topojson-prop
                           :from {:data {:values transformed-selection}
                                  :key fips-col
                                  :fields [map-names-col]}}]
              :projection {:type "identity"}
              :mark "geoshape"
              :encoding {:tooltip [{:field map-names-col
                                    :type "nominal"}]}}

        ;; If we have a second column selected, color the choropleth according
        ;; to the values in that column.
        map-column (first (filter #(not= fips-col %) selected-columns))
        map-column-type (when map-column
                          (condp = (stattype map-column)
                                 dist/gaussian "quantitative"
                                 dist/categorical "nominal"))
        color-spec {:field map-column
                    :type map-column-type}]
      (if map-column
        (-> spec
            (assoc-in [:encoding :color] color-spec)
            (update-in [:encoding :tooltip] conj color-spec)
            (update-in [:transform 0 :from :fields] conj map-column))
        spec)))

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
    (merge (condp = cols-types
             #{dist/gaussian} (scatter-plot selections cols)
             #{dist/categorical} (table-bubble-plot selections cols)
             #{dist/gaussian dist/categorical} (strip-plot selections cols))
           {:autosize {:resize true}})))

(defn- spec-for-selection-layer [selection-layer table-rows]
  (let [{layer-name :id
         selections :selections
         cols :selected-columns
         row :row-at-selection-start} selection-layer

         ;; Only get the column type when we are not dealing with a special column.
         first-col-nominal (when-not (cols-invalid-for-sim (first cols))
                             (let [type (get-col-type (first cols))]
                               (= type "nominal")))

         spec (cond (some #{fips-col} cols)
                    (gen-choropleth table-rows cols)

                    (and first-col-nominal (simulatable? selections (first cols)))
                    (gen-simulate-plot (first cols) row (name layer-name))

                    (simulatable? selections (first cols))
                    (gen-simulate-plot-vega-lite (first cols) row (name layer-name))

                    (and first-col-nominal (= 1 (count cols))) ; One column selected.
                    (gen-histogram (first cols) selections)

                    (= 1 (count cols)) ; One column selected.
                    (gen-histogram-vega-lite (first cols) selections)

                    :else ; Two or more columns selected.
                    (gen-comparison-plot (take 2 cols) selections))
          title {:title {:text (str (name layer-name) " " "selection")
                         :color (title-color layer-name)
                         :fontWeight 500}}]
    (merge spec title)))


(defn generate-spec [selection-layers table-rows]
  (when-let [layer (first selection-layers)]
    (spec-for-selection-layer layer table-rows)))
