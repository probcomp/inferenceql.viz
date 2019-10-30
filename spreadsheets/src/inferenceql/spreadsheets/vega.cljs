(ns inferenceql.spreadsheets.vega
  "Code related to displaying and generating vega-lite specs"
  (:require [yarn.vega-embed]
            [reagent.core :as r]
            [clojure.walk :as walk]
            [metaprob.distributions :as dist]
            [inferenceql.spreadsheets.model :as model]))

(def label-col-header
  "Header text for the column used for labeling rows as examples."
  "🏷")
(def score-col-header
  "Header text for the column that shows scores."
  "probability")

(def vega-width
  "Width setting for the specs produced by the :vega-lite-spec sub"
  400)
(def vega-height
  "Height setting for the specs produced by the :vega-lite-spec sub"
  400)

(def vega-map-width
  "Width setting for the choropleth specs produced by the :vega-lite-spec sub"
  500)
(def vega-map-height
  "Height setting for the choropleth specs produced by the :vega-lite-spec sub"
  300)

(def ^:private topojson-feature "cb_2017_us_cd115_20m")

(def color-for-table {:real-table "SteelBlue" :virtual-table "DarkKhaki"})


(def ^:private default-vega-embed-options {:renderer "svg"
                                           :mode "vega-lite"
                                           :config {:axis {:labelFontSize 14 :titleFontSize 14 :titlePadding 5}
                                                    :legend {:labelFontSize 12 :titleFontSize 12}
                                                    :header {:labelFontSize 14}
                                                    :mark {:tooltip true}
                                                    ;; Remove title from faceted plots.
                                                    :headerFacet {:title nil}}})

(defn vega-lite
  "vega-lite reagent component"
  [spec opt generator]
  (let [run (atom 0)
        embed (fn [this spec opt generator]
                (when spec
                  (let [spec (clj->js spec)
                        opt (clj->js (merge default-vega-embed-options
                                            opt))]
                    (cond-> (js/vegaEmbed (r/dom-node this)
                                          spec
                                          opt)
                      generator (.then (fn [res]
                                         (let [current-run (swap! run inc)]
                                           (js/requestAnimationFrame
                                            (fn send []
                                              (when (= current-run @run)
                                                (let [datum (generator)
                                                      changeset (.. js/vega
                                                                    (changeset)
                                                                    (insert (clj->js datum)))]
                                                  (.run (.change (.-view res) "data" changeset)))
                                                (js/requestAnimationFrame send)))))))
                      true (.catch (fn [err]
                                     (js/console.error err)))))))]
    (r/create-class
     {:display-name "vega-lite"

      :component-did-mount
      (fn [this]
        (embed this spec opt generator))

      :component-will-update
      (fn [this [_ new-spec new-opt new-generator]]
        (embed this new-spec new-opt new-generator))

      :component-will-unmount
      (fn [this]
        (swap! run inc))

      :reagent-render
      (fn [spec]
        [:div#vis])})))


(defn- left-pad
  [s n c]
  (str (apply str (repeat (max 0 (- n (count s)))
                          c))
       s))

(defn stattype
  [column]
  (let [stattype-kw (if (contains? #{score-col-header label-col-header} column)
                      :gaussian
                      (get-in model/spec [:vars column]))]
    (case stattype-kw
      :gaussian dist/gaussian
      :categorical dist/categorical)))

(defn gen-simulate-plot [selected-columns row-at-selection-start t-clicked]
 (let [selected-column-kw (keyword (first selected-columns))
       y-axis {:title "distribution of probable values"
               :grid false
               :labels false
               :ticks false}
       y-scale {:nice false}]
   {:$schema
    "https://vega.github.io/schema/vega-lite/v3.json"
    :width vega-width
    :height vega-height
    :data {:name "data"}
    :autosize {:resize true}
    :layer (cond-> [{:mark {:type "bar" :color (color-for-table t-clicked)}
                     :encoding (condp = (stattype (first selected-columns))
                                 dist/gaussian {:x {:bin true
                                                    :field selected-column-kw
                                                    :type "quantitative"}
                                                :y {:aggregate "count"
                                                    :type "quantitative"
                                                    :axis y-axis
                                                    :scale y-scale}}
                                 dist/categorical {:x {:field selected-column-kw
                                                       :type "nominal"}
                                                   :y {:aggregate "count"
                                                       :type "quantitative"
                                                       :axis y-axis
                                                       :scale y-scale}})}]
             (get row-at-selection-start (first selected-columns))
             (conj {:data {:values [{selected-column-kw (-> row-at-selection-start (get (first selected-columns)))
                                     :label "Selected row"}]}
                    :mark {:type "rule"
                           :color "red"}
                    :encoding {:x {:field selected-column-kw
                                   :type (condp = (stattype (first selected-columns))
                                           dist/gaussian "quantitative"
                                           dist/categorical "nominal")}}}))}))

(defn get-col-type [col-name]
  (condp = (stattype col-name)
    dist/gaussian "quantitative"
    dist/categorical "nominal"))

(defn get-col-should-bin [col-name]
  (condp = (stattype col-name)
    dist/gaussian true
    dist/categorical false))

(defn gen-histogram [table-states t-clicked]
  (let [selection-real (first (get-in table-states [:real-table :selections]))
        selection-virtual (first (get-in table-states [:virtual-table :selections]))

        col-real (first (get-in table-states [:real-table :selected-columns]))
        col-virtual (first (get-in table-states [:virtual-table :selected-columns]))
        make-layered-hist (= col-real col-virtual)


        col-to-draw (first (get-in table-states [t-clicked :selected-columns]))
        col-type (get-col-type col-to-draw)
        col-binning (get-col-should-bin col-to-draw)

        virtual-layer-opacity (if make-layered-hist 0.5 1.0)

        real-layer {:data {:values selection-real}
                    :mark {:type "bar" :color (color-for-table :real-table) :opacity 1}
                    :encoding {:y {:aggregate "count"
                                   :type "quantitative"}}}
        virtual-layer {:data {:values selection-virtual}
                       :mark {:type "bar" :color (color-for-table :virtual-table) :opacity virtual-layer-opacity}
                       :encoding {:y {:aggregate "count"
                                      :type "quantitative"}}}
        layers-to-draw (cond
                         make-layered-hist
                         [real-layer virtual-layer]

                         (= t-clicked :real-table)
                         [real-layer]

                         (= t-clicked :virtual-table)
                         [virtual-layer])]
    {:$schema
     "https://vega.github.io/schema/vega-lite/v3.json"
     :width vega-width
     :height vega-height
     :encoding {:x {:bin col-binning
                    :field col-to-draw
                    :type col-type}}
     :layer layers-to-draw}))

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
    {:$schema "https://vega.github.io/schema/vega-lite/v3.json"
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
  [data cols-to-draw facet-column]
  (let [spec {:$schema "https://vega.github.io/schema/vega-lite/v3.json"
              :width vega-width
              :height vega-height
              :data {:values data}
              :mark "circle"
              :encoding {:x {:field (first cols-to-draw)
                             :type "quantitative"}
                         :y {:field (second cols-to-draw)
                             :type "quantitative"}}}]
    (if facet-column
      (assoc-in spec [:encoding :facet] {:field facet-column :type "nominal"})
      spec)))

(defn- heatmap-plot
  "Generates vega-lite spec for a heatmap plot.
  Useful for comparing nominal-nominal data."
  [data cols-to-draw facet-column]
  (let [spec {:$schema "https://vega.github.io/schema/vega-lite/v3.json"
              :width vega-width
              :height vega-height
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
        spec {:$schema "https://vega.github.io/schema/vega-lite/v3.json"
              :width vega-width
              :height vega-height
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

(defn gen-comparison-plot [table-states t-clicked]
  (let [selection-real (->> (first (get-in table-states [:real-table :selections]))
                            (map #(assoc % :table "Real Data")))

        selection-virtual (->> (first (get-in table-states [:virtual-table :selections]))
                               (map #(assoc % :table "Virtual Data")))

        cols-real (take 2 (get-in table-states [:real-table :selected-columns]))
        cols-virtual (take 2 (get-in table-states [:virtual-table :selected-columns]))

        ;; Checks if the user has selected the same columns in both the real and virtual tables.
        make-faceted (= cols-real cols-virtual)

        cols-to-draw (take 2 (get-in table-states [t-clicked :selected-columns]))
        cols-types (set (doall (map stattype cols-to-draw)))

        ;; This is the selection from the last-clicked-on table.
        selection-not-faceted (first (get-in table-states [t-clicked :selections]))
        ;; This is the selections from both the real and virtual tables combined.
        selection-faceted (concat selection-real selection-virtual)

        selection-to-use (if make-faceted selection-faceted selection-not-faceted)
        facet-column (if make-faceted "table" nil)]
    (condp = cols-types
      #{dist/gaussian} (scatter-plot selection-to-use cols-to-draw facet-column)
      #{dist/categorical} (heatmap-plot selection-to-use cols-to-draw facet-column)
      #{dist/gaussian dist/categorical} (box-and-line-plot selection-to-use cols-to-draw facet-column)
      ;; Default case: no plot -- empty vega-lite spec.
      {})))
