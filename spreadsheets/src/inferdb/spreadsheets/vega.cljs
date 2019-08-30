(ns inferdb.spreadsheets.vega
  "A Handsontable Reagent component."
  (:require [yarn.vega-embed]
            [reagent.core :as r]
            [clojure.walk :as walk]
            [metaprob.distributions :as dist]
            [inferdb.spreadsheets.model :as model]))

(defn vega-lite
  [spec opt generator]
  (let [run (atom 0)
        embed (fn [this spec opt generator]
                (when spec
                  (let [spec (clj->js spec)
                        opt (clj->js (merge {:renderer "canvas"
                                             :mode "vega-lite"}
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

(def ^:private topojson-feature "cb_2017_us_cd115_20m")

(defn- left-pad
  [s n c]
  (str (apply str (repeat (max 0 (- n (count s)))
                          c))
       s))

(defn stattype
  [column]
  (let [stattype-kw (if (or (= "probability" column) (= "🏷" column))
                      :gaussian
                      (get-in model/spec [:vars column]))]
    (case stattype-kw
      :gaussian dist/gaussian
      :categorical dist/categorical)))

(def color-for-table {:real-table "SteelBlue" :virtual-table "DarkKhaki"})

(defn gen-simulate-plot [selected-columns row-at-selection-start t-clicked]
 (let [selected-row-kw (walk/keywordize-keys row-at-selection-start)
       selected-column-kw (keyword (first selected-columns))
       y-axis {:title "distribution of probable values"
               :grid false
               :labels false
               :ticks false}
       y-scale {:nice false}]
   {:$schema
    "https://vega.github.io/schema/vega-lite/v3.json"
    :width 400
    :height 400
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
     :width 400
     :height 400
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
     :width 500
     :height 300
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

(defn gen-comparison-plot [selections selected-columns]
  (let [selection (first selections)
        types (into #{}
                    (map stattype)
                    (take 2 selected-columns))]
    (condp = types
      ;; Scatterplot
      #{dist/gaussian} {:$schema
                        "https://vega.github.io/schema/vega-lite/v3.json"
                        :width 400
                        :height 400
                        :data {:values selection}
                        :mark "circle"
                        :encoding {:x {:field (first selected-columns)
                                       :type "quantitative"}
                                   :y {:field (second selected-columns)
                                       :type "quantitative"}}}
      ;; Heatmap
      #{dist/categorical} {:$schema
                           "https://vega.github.io/schema/vega-lite/v3.json"
                           :width 400
                           :height 400
                           :data {:values selection}
                           :mark "rect"
                           :encoding {:x {:field (first selected-columns)
                                          :type "nominal"}
                                      :y {:field (second selected-columns)
                                          :type "nominal"}
                                      :color {:aggregate "count"
                                              :type "quantitative"}}}
      ;; Bot-and-line
      #{dist/gaussian
        dist/categorical} {:$schema
                           "https://vega.github.io/schema/vega-lite/v3.json"
                           :width 400
                           :height 400
                           :data {:values selection}
                           :mark {:type "boxplot"
                                  :extent "min-max"}
                           :encoding {:x {:field (first selected-columns)
                                          :type (condp = (stattype (first selected-columns))
                                                  dist/gaussian "quantitative"
                                                  dist/categorical "nominal")}
                                      :y {:field (second selected-columns)
                                          :type (condp = (stattype (second selected-columns))
                                                  dist/gaussian "quantitative"
                                                  dist/categorical "nominal")}
                                      :color {:aggregate "count"
                                              :type "quantitative"}}}
      {})))
