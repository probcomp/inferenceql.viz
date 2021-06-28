(ns inferenceql.viz.observable.vega-cell-by-cell)

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
                mapping {:gaussian "quantitative"
                         :categorical "nominal"}

                infer-type (fn [col-name]
                             (let [numbers (->> data
                                                (take 10)
                                                (map #(get % col-name))
                                                (map number?)
                                                (every? true?))]
                               (if numbers :gaussian :categorical)))

                iql-type (or (->> col-name
                                  (get schema)
                                  (keyword))
                             (infer-type col-name))]
            (get mapping iql-type)))))

(defn- scatter-plot
  "Generates vega-lite spec for a scatter plot.
  Useful for comparing quatitative-quantitative data."
  [data cols-to-draw]
  (let [zoom-control-name (keyword (gensym "zoom-control"))] ; Random id so pan/zoom is independent.
    {:width 250
     :height 250
     :data {:values data}
     :mark {:type "circle" :tooltip {:content "data"}}
     :selection {zoom-control-name {:type "interval"
                                    :bind "scales"
                                    :on "[mousedown[event.shiftKey], window:mouseup] > window:mousemove"
                                    :translate "[mousedown[event.shiftKey], window:mouseup] > window:mousemove"
                                    :clear "dblclick[event.shiftKey]"
                                    :zoom "wheel![event.shiftKey]"}
                 :pts {:type "interval"
                       :on "[mousedown[!event.shiftKey], window:mouseup] > window:mousemove"
                       :translate "[mousedown[!event.shiftKey], window:mouseup] > window:mousemove"
                       :clear "dblclick[!event.shiftKey]"
                       :zoom "wheel![!event.shiftKey]"
                       :empty "none"}}
     :encoding {:x {:field (first cols-to-draw)
                    :type "quantitative"
                    :scale {:zero false}}
                :y {:field (second cols-to-draw)
                    :type "quantitative"
                    :scale {:zero false}}
                #_:order #_{:field "anomaly"
                            :type "nominal"
                            :scale {:domain ["true", "false", "undefined"]
                                    :range [1 1 0]}
                            :legend nil}
                :size {:condition {:selection "pts"
                                   :value 100}
                       :value 50}
                :color {:field "anomaly"
                        :type "nominal"
                        :scale {:domain ["true", "false", "undefined"]
                                :range ["Crimson" "steelblue" "lightgrey"]}
                        :legend nil}}}))

(defn- strip-plot-size-helper
  "Returns a vega-lite height/width size.

  Args:
    `col-type` - A vega-lite column type."
  [col-type]
  (case col-type
    "quantitative" 400
    "nominal" {:step 40}))

(defn- strip-plot
  "Generates vega-lite spec for a strip plot.
  Useful for comparing quantitative-nominal data."
  [data cols-to-draw vega-type]
  (let [zoom-control-name (keyword (gensym "zoom-control")) ; Random id so pan/zoom is independent.

        ;; NOTE: This is a temporary hack to that forces the x-channel in the plot to be "numerical"
        ;; and the y-channel to be "nominal". The rest of the code remains nuetral to the order so that
        ;; it can be used by the iql-viz query language later regardless of column type order.
        first-col-nominal (= "nominal" (vega-type (first cols-to-draw)))
        cols-to-draw (cond->> (take 2 cols-to-draw)
                       first-col-nominal (reverse))

        [x-field y-field] cols-to-draw
        [x-type y-type] (map vega-type cols-to-draw)
        quant-dimension (if (= x-type "quantitative") :x :y)
        [width height] (map (comp strip-plot-size-helper vega-type) cols-to-draw)]
    {:width width
     :height height
     :data {:values data}
     :mark {:type "tick" :tooltip {:content "data"}}
     :selection {zoom-control-name {:type "interval"
                                    :bind "scales"
                                    :on "[mousedown[event.shiftKey], window:mouseup] > window:mousemove"
                                    :translate "[mousedown[event.shiftKey], window:mouseup] > window:mousemove"
                                    :clear "dblclick[event.shiftKey]"
                                    :encodings [quant-dimension]
                                    :zoom "wheel![event.shiftKey]"}
                 :pts {:type "interval"
                       :on "[mousedown[!event.shiftKey], window:mouseup] > window:mousemove"
                       :translate "[mousedown[!event.shiftKey], window:mouseup] > window:mousemove"
                       :clear "dblclick[!event.shiftKey]"
                       :zoom "wheel![!event.shiftKey]"
                       :empty "none"}}
     :encoding {:x {:field x-field
                    :type x-type
                    :axis {:grid true :gridDash [2 2]}
                    ;; Note: this assumes the x-axis is quantitative.
                    :scale {:zero false}}
                :y {:field y-field
                    :type y-type
                    :axis {:grid true :gridDash [2 2]}}
                #_:order #_{:field "anomaly"
                            :type "nominal"
                            :scale {:domain ["true", "false", "undefined"]
                                    :range [1 1 0]}
                            :legend nil}
                :color {:field "anomaly"
                        :type "nominal"
                        :scale {:domain ["true", "false", "undefined"]
                                :range ["Crimson" "steelblue" "lightgrey"]}
                        :legend nil}}}))

(defn- spec-for-selection-layer [schema data cols]
  (let [vega-type (vega-type-fn schema data)]
    (when (and (= (count cols) 2) (every? some? (map vega-type cols)))
      (let [cols-types (set (doall (map vega-type cols)))]
        (condp = cols-types
          #{"quantitative"} (scatter-plot data cols)
          #{"quantitative" "nominal"} (strip-plot data cols vega-type))))))

(defn generate-spec [schema data selections]
  (when-let [spec-layers (seq (keep #(spec-for-selection-layer schema data %)
                                    selections))]
    {:$schema "https://vega.github.io/schema/vega-lite/v5.json"
     :concat spec-layers
     :columns 2
     :config {:tick {:thickness 3}}
     :resolve {:legend {:size "independent"
                        :color "independent"}
               :scale {:color "independent"}}}))
