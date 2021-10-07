(ns inferenceql.viz.panels.viz.dashboard
  "Code related to producing a vega-lite spec for a dashboard."
  (:require [inferenceql.viz.panels.viz.regression :as regression]
            [inferenceql.viz.panels.viz.util :refer [filtering-summary should-bin? bind-to-element
                                                     obs-data-color virtual-data-color
                                                     unselected-color vega-type-fn
                                                     regression-color
                                                     vl5-schema]]
            [cljs-bean.core :refer [->clj]]
            #?(:cljs [goog.string :refer [format]])
            #?(:cljs [vega-embed$vega :as vega])
            #?(:cljs [goog.object])))

(def cluster-selected-color "#4e79a7")

(defn bin-counts [data bin-outcome]
  (let [min (:start bin-outcome)
        max (:stop bin-outcome)
        width (:step bin-outcome)
        bin-vals (->> data
                      (filter #(and (<= min %) (<= % max))) ; Val in range.
                      (remove nil?)
                      (map #(quot (- % min) width))) ; Map to bucket number.
        num-bins (quot (- max min) width)

        ;; Numbers equal to `max` map to 1 bucket beyond the final bucket.
        ;; Simply dec the buckets that these numbers map to, so they land in the
        ;; final valid bucket. This is similar to how vega handles binning.
        bin-vals (map (fn [bin-num] (cond-> bin-num (= num-bins bin-num) dec))
                      bin-vals)]
    (reduce (fn [acc bv]
              (update acc bv inc))
            (vec (repeat num-bins 0))
            bin-vals)))

(defn bin-outcome [bin-config]
  (let [[min max] (:extent bin-config)]
    #?(:cljs (->clj (vega/bin (clj->js bin-config)))
       ;; Very bad appproximaiton of good binning.
       :clj {:start min :stop max :step (/ (- max min) 10)})))

(defn histogram-quant
  "Generates a vega-lite spec for a histogram.
  `selections` is a collection of maps representing data in selected rows and columns.
  `col` is the key within each map in `selections` that is used to extract data for the histogram.
  `vega-type` is a function that takes a column name and returns an vega datatype."
  [col vega-type samples]
  (let [col-type (vega-type col)

        max-bins 30
        points (remove nil? (map col samples))
        col-min (apply min points)
        col-max (apply max points)
        bin-config {:extent [col-min col-max]
                    :maxbins max-bins}
        bin-outcome (bin-outcome bin-config)

        observed-points (map col (filter #(= (:collection %) "observed") samples))
        virtual-points (map col (filter #(= (:collection %) "virtual") samples))
        bins-counted (concat (bin-counts observed-points bin-outcome)
                             (bin-counts virtual-points bin-outcome))

        y-range-buffer 1
        max-bin-count (+ y-range-buffer
                         (apply max bins-counted))

        fsum (filtering-summary [col] vega-type nil samples)]
    {:resolve {:scale {:x "shared" :y "shared"}}
     :spacing 0
     :bounds "flush"
     :transform [{:window [{:op "row_number", :as "row_number_subplot"}]
                  :groupby ["collection"]}
                 {:filter {:or [{:and [{:field "collection" :equal "observed"}
                                       {:field "row_number_subplot" :lte {:expr "numObservedPoints"}}]}
                                {:and [{:field "collection" :equal "virtual"}
                                       {:field "row_number_subplot" :lte (:num-valid fsum)}
                                       {:field "row_number_subplot" :lte {:expr "numVirtualPoints"}}]}]}}]
     :facet {:field "collection"
             :type "nominal"
             :header {:title nil :labelOrient "bottom" :labelPadding 34}}
     :spec {:layer [{:mark {:type "bar"
                            :color unselected-color
                            :tooltip {:content "data"}}
                     :params [{:name "brush-all"
                               ;; TODO: is there a way to select based on collection here as well?
                               :select {:type "interval" :encodings ["x"]}}]
                     :encoding {:x {:bin bin-config
                                    :field col
                                    :type col-type}
                                :y {:aggregate "count"
                                    :type "quantitative"
                                    :scale {:domain [0, max-bin-count]}}}}
                    {:transform [{:filter {:and ["cluster == null"
                                                 {:param "brush-all"}]}}]
                     :mark {:type "bar"}
                     :encoding {:x {:bin bin-config
                                    :field col
                                    :type col-type}
                                :y {:aggregate "count"
                                    :type "quantitative"
                                    :scale {:domain [0, max-bin-count]}}
                                :color {:field "collection"
                                        :scale {:domain ["observed", "virtual"]
                                                :range [obs-data-color virtual-data-color]}
                                        :legend {:orient "top"
                                                 :title nil}}}}
                    {:transform [{:filter {:and ["datum[view] == cluster"
                                                 (format "indexof(view_columns, '%s') != -1" (name col))]}}]
                     :mark {:type "bar"}
                     :encoding {:x {:bin bin-config
                                    :field col
                                    :type col-type}
                                :y {:aggregate "count"
                                    :type "quantitative"
                                    :scale {:domain [0, max-bin-count]}}
                                :color {:value cluster-selected-color}}}]}}))


(defn histogram-nom
  "Generates a vega-lite spec for a histogram.
  `selections` is a collection of maps representing data in selected rows and columns.
  `col` is the key within each map in `selections` that is used to extract data for the histogram.
  `vega-type` is a function that takes a column name and returns an vega datatype."
  [col vega-type samples]
  (let [col-type (vega-type col)
        freqs (frequencies (map col samples))
        col-vals (sort (keys freqs))
        ;; If nil is present, move it to the back of the list.
        col-vals (if (some nil? col-vals)
                   (concat (remove nil? col-vals) [nil])
                   col-vals)
        cat-max-count (apply max (vals freqs))
        bin-flag (should-bin? col-type)
        f-sum (filtering-summary [col] vega-type nil samples)]
    {:transform [{:window [{:op "row_number", :as "row_number_subplot"}]
                  :groupby ["collection"]}
                 {:filter {:or [{:and [{:field "collection" :equal "observed"}
                                       {:field "row_number_subplot" :lte {:expr "numObservedPoints"}}]}
                                {:and [{:field "collection" :equal "virtual"}
                                       {:field "row_number_subplot" :lte (:num-valid f-sum)}
                                       {:field "row_number_subplot" :lte {:expr "numVirtualPoints"}}]}]}}]
     :layer [{:mark {:type "point"
                     :color unselected-color
                     :tooltip {:content "data"}
                     :opacity 0.85}
              :params [{:name "brush-all"
                        :select {:type "point"
                                 :nearest true
                                 :toggle "true"
                                 :on "click[!event.shiftKey]"
                                 :fields [col "collection"]
                                 :clear "dblclick[!event.shiftKey]"}}]
              :encoding {:y {:bin bin-flag
                             :field col
                             :type col-type
                             :axis {:titleAnchor "start" :titleAlign "right" :titlePadding 1}
                             :scale {:domain col-vals}}
                         :x {:aggregate "count"
                             :type "quantitative"
                             :axis {:orient "top"}
                             :scale {:domain [0 cat-max-count]}}
                         ;; TODO: this ordering does not seem to be working.
                         :order {:field "collection"
                                 :scale {:domain ["observed", "virtual"]
                                         :range [1 0]}}
                         :color {:condition [{:test {:and [{:field "collection" :equal "observed"}
                                                           {:param "brush-all"}
                                                           ;; Only color the observed points when
                                                           ;; a view-cluster is not selected or when
                                                           ;; the column for this plot is in the
                                                           ;; view selected.
                                                           {:or ["cluster == null"
                                                                 (format "indexof(view_columns, '%s') != -1" (name col))]}]}
                                              :value obs-data-color}
                                             {:test {:and [{:field "collection" :equal "virtual"}
                                                           {:param "brush-all"}
                                                           ;; Only color the virtual points when
                                                           ;; a view-cluster is not selected.
                                                           "cluster == null"]}
                                              :value virtual-data-color}
                                             {:test "true"
                                              :value unselected-color}]
                                 :field "collection" ; Dummy field. Never gets used.
                                 :scale {:domain ["observed", "virtual"]
                                         :range [obs-data-color virtual-data-color]}
                                 :legend {:orient "top"
                                          :title nil
                                          :offset 10}}}}
             {:mark {:type "point"
                     :shape "stroke"
                     :color cluster-selected-color
                     :opacity 0.9}
              :transform [{:filter {:and ["datum[view] == cluster"
                                          (format "indexof(view_columns, '%s') != -1" (name col))]}}]
              :encoding {:y {:bin bin-flag
                             :field col
                             :type col-type
                             :axis {:titleAnchor "start" :titleAlign "right" :titlePadding 1}
                             :scale {:domain col-vals}}
                         :x {:aggregate "count"
                             :type "quantitative"
                             :axis {:orient "top"}
                             :scale {:domain [0 cat-max-count]}}}}]}))


(defn- scatter-plot
  "Generates vega-lite spec for a scatter plot.
  Useful for comparing quatitative-quantitative data."
  [col-1 col-2 vega-type correlation samples counter]
  (let [zoom-control-name (str "zoom-control-" counter) ; Random id so pan/zoom is independent.
        f-sum (filtering-summary [col-1 col-2] vega-type nil samples)

        base-spec {:width 400
                   :height 400
                   :layer [{:transform [{:window [{:op "row_number", :as "row_number_subplot"}]
                                         :groupby ["collection"]}
                                        {:filter {:or [{:and [{:field "collection" :equal "observed"}
                                                              {:field "row_number_subplot" :lte {:expr "numObservedPoints"}}]}
                                                       {:and [{:field "collection" :equal "virtual"}
                                                              {:field "row_number_subplot" :lte (:num-valid f-sum)}
                                                              {:field "row_number_subplot" :lte {:expr "numVirtualPoints"}}]}]}}]
                            :mark {:type "point"
                                   :tooltip {:content "data"}
                                   :filled true
                                   :size {:expr "splomPointSize"}}
                            :params [{:name zoom-control-name
                                      :bind "scales"
                                      :select {:type "interval"
                                               :on "[mousedown[event.shiftKey], window:mouseup] > window:mousemove"
                                               :translate "[mousedown[event.shiftKey], window:mouseup] > window:mousemove"
                                               :clear "dblclick[event.shiftKey]"
                                               :zoom "wheel![event.shiftKey]"}}
                                     {:name :brush-all
                                      :select {:type "interval"
                                               :on "[mousedown[!event.shiftKey], window:mouseup] > window:mousemove"
                                               :translate "[mousedown[!event.shiftKey], window:mouseup] > window:mousemove"
                                               :clear "dblclick[!event.shiftKey]"
                                               :zoom "wheel![!event.shiftKey]"}}]
                            :encoding {:x {:field col-1
                                           :type "quantitative"
                                           :scale {:zero false}
                                           :axis {:title col-1}}
                                       :y {:field col-2
                                           :type "quantitative"
                                           :scale {:zero false}
                                           :axis {:minExtent 40
                                                  :title col-2}}
                                       :order {:condition [{:test {:and ["datum[view] == cluster"
                                                                         (format "indexof(view_columns, '%s') != -1" (name col-1))
                                                                         (format "indexof(view_columns, '%s') != -1" (name col-2))]}
                                                            :value 10}
                                                           {:test {:and [{:field "collection" :equal "observed"}
                                                                         {:param "brush-all"}
                                                                         "cluster == null"]}
                                                            :value 1}
                                                           ;; Show the virtual data colored even
                                                           ;; when a particular cluster is selected.
                                                           {:test {:and [{:field "collection" :equal "virtual"}
                                                                         {:param "brush-all"}]}
                                                            :value 2}
                                                           {:test "true"
                                                            :value 0}]
                                               :value 0}
                                       :opacity {:field "collection"
                                                 :scale {:domain ["observed", "virtual"]
                                                         :range [{:expr "splomAlphaObserved"} {:expr "splomAlphaVirtual"}]}
                                                 :legend nil}
                                       :color {:condition [{:test {:and ["datum[view] == cluster"
                                                                         (format "indexof(view_columns, '%s') != -1" (name col-1))
                                                                         (format "indexof(view_columns, '%s') != -1" (name col-2))]}
                                                            :value cluster-selected-color}
                                                           {:test {:and [{:field "collection" :equal "observed"}
                                                                         {:param "brush-all"}
                                                                         "cluster == null"]}
                                                            :value obs-data-color}
                                                           {:test {:and [{:field "collection" :equal "virtual"}
                                                                         {:param "brush-all"}
                                                                         "cluster == null"]}
                                                            :value virtual-data-color}
                                                           {:test "true"
                                                            :value unselected-color}]
                                               :field "collection" ; Dummy field. Never gets used.
                                               :scale {:domain ["observed", "virtual"]
                                                       :range [obs-data-color virtual-data-color]}
                                               :legend {:orient "top"
                                                        :title nil}}}}]}]
    (regression/line col-1 col-2 correlation samples base-spec)))

(defn- strip-plot-size-helper
  "Returns a vega-lite height/width size.

  Args:
    `col-type` - A vega-lite column type."
  [col-type]
  (case col-type
    "quantitative" 400
    "nominal" {:step 24}))

(defn- strip-plot
  "Generates vega-lite spec for a strip plot.
  Useful for comparing quantitative-nominal data."
  [cols vega-type n-cats samples counter]
  (let [zoom-control-name (str "zoom-control-" counter) ; Random id so pan/zoom is independent.
        ;; NOTE: This is a temporary hack to that forces the x-channel in the plot to be "numerical"
        ;; and the y-channel to be "nominal". The rest of the code remains nuetral to the order so that
        ;; it can be used by the iql-viz query language later regardless of column type order.
        first-col-nominal (= "nominal" (vega-type (first cols)))
        cols-to-draw (cond->> (take 2 cols)
                              first-col-nominal (reverse))

        [x-field y-field] cols-to-draw
        [x-type y-type] (map vega-type cols-to-draw)
        quant-dimension (if (= x-type "quantitative") :x :y)
        [width height] (map (comp strip-plot-size-helper vega-type) cols-to-draw)

        x-vals (map x-field samples)
        x-min (apply min x-vals)
        x-max (apply max x-vals)
        f-sum (filtering-summary cols vega-type n-cats samples)
        y-cats (sort (get-in f-sum [:top-cats y-field]))
        title-limit (* (count y-cats) 25)]
    {:resolve {:scale {:x "shared" :y "shared"}}
     :spacing 0
     :bounds "flush"
     :transform [;; Filtering for top categories
                 {:filter {:field y-field :oneOf y-cats}}
                 {:window [{:op "row_number", :as "row_number_subplot"}]
                  :groupby ["collection"]}
                 ;; Displaying an equal number of virtual data points as observed datapoints.
                 ;; Filtering virtual and observed datapoints based on user-set limit.
                 {:filter {:or [{:and [{:field "collection" :equal "observed"}
                                       {:field "row_number_subplot" :lte {:expr "numObservedPoints"}}]}
                                {:and [{:field "collection" :equal "virtual"}
                                       {:field "row_number_subplot" :lte (:num-valid f-sum)}
                                       {:field "row_number_subplot" :lte {:expr "numVirtualPoints"}}]}]}}]
     :width width
     :height height
     :mark {:type "tick"
            :tooltip {:content "data"}
            :color unselected-color}
     :params [{:name zoom-control-name
               :bind "scales"
               :select {:type "interval"
                        :on "[mousedown[event.shiftKey], window:mouseup] > window:mousemove"
                        :translate "[mousedown[event.shiftKey], window:mouseup] > window:mousemove"
                        :clear "dblclick[event.shiftKey]"
                        :encodings [quant-dimension]
                        :zoom "wheel![event.shiftKey]"}}
              {:name "brush-all"
               :select  {:type "point"
                         :nearest true
                         :toggle "true"
                         :on "click[!event.shiftKey]"
                         :resolve "union"
                         :fields [y-field "collection"]
                         :clear "dblclick[!event.shiftKey]"}}]
     :encoding {:y {:field y-field
                    :type y-type
                    :scale {:domain y-cats}
                    :axis {:titleLimit title-limit}}
                :x {:field x-field
                    :type x-type
                    :axis {:grid true :gridDash [2 2]
                           :orient "top"}
                    :scale {:zero false
                            :domain [x-min x-max]}}
                :row {:field "collection"
                      :type "nominal"
                      :header {:title nil
                               :labelPadding 0
                               :labelLimit title-limit}}
                :order {:condition {:param "brush-all"
                                    :value 1}
                        :value 0}
                :color {:condition [{:test {:and ["datum[view] == cluster"
                                                  (format "indexof(view_columns, '%s') != -1" (name x-field))
                                                  (format "indexof(view_columns, '%s') != -1" (name y-field))]}
                                     :value cluster-selected-color}
                                    {:test {:and [{:field "collection" :equal "observed"}
                                                  {:param "brush-all"}
                                                  "cluster == null"]}
                                     :value obs-data-color}
                                    {:test {:and [{:field "collection" :equal "virtual"}
                                                  {:param "brush-all"}
                                                  "cluster == null"]}
                                     :value virtual-data-color}
                                    {:test "true"
                                     :value unselected-color}]
                        :field "collection" ; Dummy field. Never gets used.
                        :scale {:domain ["observed", "virtual"]
                                :range [obs-data-color virtual-data-color]}
                        :legend {:orient "top"
                                 :title nil
                                 :offset 10}}}}))


(defn- table-bubble-plot
  "Generates vega-lite spec for a table-bubble plot.
  Useful for comparing nominal-nominal data."
  [cols vega-type n-cats samples]
  (let [[x-field y-field] cols
        f-sum (filtering-summary cols vega-type n-cats samples)
        x-cats (sort (get-in f-sum [:top-cats x-field]))
        y-cats (sort (get-in f-sum [:top-cats y-field]))
        title-limit (* (count x-cats) 25)]
    {:spacing 0
     :bounds "flush"
     :transform [;; Filtering for top categories
                 {:filter {:field x-field :oneOf x-cats}}
                 {:filter {:field y-field :oneOf y-cats}}
                 {:window [{:op "row_number", :as "row_number_subplot"}]
                  :groupby ["collection"]}
                 ;; Displaying an equal number of virtual data points as observed datapoints.
                 ;; Filtering virtual and observed datapoints based on user-set limit.
                 {:filter {:or [{:and [{:field "collection" :equal "observed"}
                                       {:field "row_number_subplot" :lte {:expr "numObservedPoints"}}]}
                                {:and [{:field "collection" :equal "virtual"}
                                       {:field "row_number_subplot" :lte (:num-valid f-sum)}
                                       {:field "row_number_subplot" :lte {:expr "numVirtualPoints"}}]}]}}]
     :width {:step 20}
     :height {:step 20}
     :facet {:column {:field "collection"
                      :type "nominal"
                      :header {:title nil
                               :labelPadding 0
                               :labelLimit title-limit}}},
     :spec {:layer [{:mark {:type "circle"
                            :tooltip {:content "data"}
                            :color unselected-color}
                     :params [{:name "brush-all"
                               :select {:type "point"
                                        :nearest true
                                        :toggle "true"
                                        :on "click[!event.shiftKey]"
                                        :resolve "union"
                                        :fields [y-field x-field "collection"]
                                        :clear "dblclick[!event.shiftKey]"}}]
                     :encoding {:y {:field y-field
                                    :type "nominal"
                                    :axis {:titleOrient "left"
                                           :titleAnchor "center"}
                                    :scale {:domain y-cats}}
                                :x {:field x-field
                                    :type "nominal"
                                    :axis {:orient "top"
                                           :labelAngle 315
                                           :titleLimit title-limit}
                                    :scale {:domain x-cats}}
                                :size {:aggregate "count"
                                       :type "quantitative"
                                       :legend nil}
                                :color {:condition [{:test {:and [{:field "collection" :equal "observed"}
                                                                  {:param "brush-all"}
                                                                  "cluster == null"]}
                                                     :value obs-data-color}
                                                    {:test {:and [{:field "collection" :equal "virtual"}
                                                                  {:param "brush-all"}
                                                                  "cluster == null"]}
                                                     :value virtual-data-color}
                                                    {:test "true"
                                                     :value unselected-color}]
                                        :field "collection" ; Dummy field. Never gets used.
                                        :scale {:domain ["observed", "virtual"]
                                                :range [obs-data-color virtual-data-color]}
                                        :legend {:orient "top"
                                                 :title nil
                                                 :offset 10}}}}
                    {:mark {:type "circle"
                            :color cluster-selected-color}
                     :transform [{:filter {:and ["datum[view] == cluster"
                                                 (format "indexof(view_columns, '%s') != -1" (name x-field))
                                                 (format "indexof(view_columns, '%s') != -1" (name y-field))]}}]
                     :encoding {:y {:field y-field
                                    :type "nominal"
                                    :axis {:titleOrient "left"
                                           :titleAnchor "center"}
                                    :scale {:domain y-cats}}
                                :x {:field x-field
                                    :type "nominal"
                                    :axis {:orient "top"
                                           :labelAngle 315
                                           :titleLimit title-limit}
                                    :scale {:domain x-cats}}
                                :size {:aggregate "count"
                                       :type "quantitative"
                                       :legend nil}}}]}}))

(defn histogram-quant-section [cols vega-type samples]
  (when (seq cols)
    (let [specs (for [col cols] (histogram-quant col vega-type samples))]
      {:concat specs
       :columns 2
       :spacing {:column 50 :row 50}})))

(defn histogram-nom-section [cols vega-type samples]
  (when (seq cols)
    (let [specs (for [col cols] (histogram-nom col vega-type samples))]
      {:concat specs
       :columns 2
       :spacing {:column 100 :row 50}})))

(defn scatter-plot-section [cols vega-type correlation samples counter]
  (when (seq cols)
    (let [specs (for [[col-1 col-2] cols] (scatter-plot col-1 col-2 vega-type correlation samples counter))]
      {:concat specs
       :columns 2
       :spacing {:column 50 :row 50}
       :resolve {:legend {:color "shared"}}})))

(defn bubble-plot-section [cols vega-type n-cats samples]
  (when (seq cols)
    (let [specs (for [col-pair cols]
                  (let [[col-1 col-2] col-pair
                        col-1-vals (count (distinct (map #(get % col-1) samples)))
                        col-2-vals (count (distinct (map #(get % col-2) samples)))
                        col-pair (if (>= col-1-vals col-2-vals)
                                   [col-2 col-1]
                                   [col-1 col-2])]
                    ;; Produce the bubble plot with the more optionful column on the x-dim.
                    (table-bubble-plot col-pair vega-type n-cats samples)))]
      {:concat specs
       :columns 2
       :spacing {:column 50 :row 50}})))

(defn strip-plot-section [cols vega-type n-cats samples counter]
  (when (seq cols)
    (let [specs (for [col-pair cols]
                  (strip-plot col-pair vega-type n-cats samples counter))]
      {:concat specs
       :columns 2
       :spacing {:column 100 :row 50}})))

(defn top-level-spec [data num-observed num-virtual sections]
  (let [spec {:$schema vl5-schema
              :autosize {:resize true}
              :vconcat sections
              :spacing 100
              :data {:name "rows"}
              :params [{:name "iter"
                        :value 0}
                       {:name "view"
                        :value "view_1"}
                       {:name "view_columns"
                        :value ["BMI" "age" "blah"]}
                       {:name "cluster"
                        :value 1}
                       {:name "splomAlphaObserved"
                        :value 0.7
                        :bind {:input "range" :min 0 :max 1 :step 0.05
                               :name "scatter plots - alpha (observed data)"}}
                       {:name "splomAlphaVirtual"
                        :value 0.7
                        :bind {:input "range" :min 0 :max 1 :step 0.05
                               :name "scatter plots - alpha (virtual data)"}}
                       {:name "splomPointSize"
                        :value 30
                        :bind {:input "range" :min 1 :max 100 :step 1
                               :name "scatter plots - point size"}}
                       {:name "numObservedPoints"
                        :value num-observed
                        :bind {:input "range" :min 1 :max num-observed :step 1
                               :name "number of points (observed data)"}}
                       {:name "numVirtualPoints"
                        :value num-virtual
                        :bind {:input "range" :min 1 :max num-virtual :step 1
                               :name "number of points (virtual data)"}}
                       {:name "showRegression"
                        :value false
                        :bind {:input "checkbox"
                               :name "regression lines"}}]
              :transform [{:window [{:op "row_number", :as "row_number"}]
                           :groupby ["collection"]}
                          {:filter {:field "iter" :lte {:expr "iter"}}}]
              :config {:countTitle "Count"
                       :axisY {:minExtent 10}}
              :resolve {:legend {:size "independent"
                                 :color "independent"}
                        :scale {:color "independent"}}}]
    (update spec :params bind-to-element "#controls")))

(defn spec
  "Produces a vega-lite spec for the QC Dashboard app.
  Paths to samples and schema are required.
  Path to correlation data is optional.
  Category limit is the max number of options to include for categorical variable.
  It can be set to nil for no limit."
  [samples schema correlation cols category-limit marginal-types]
  (when (and (seq marginal-types) (seq cols))
    (let [num-observed (-> (group-by :collection samples)
                           (get "observed")
                           (count))
          num-virtual (-> (group-by :collection samples)
                          (get "virtual")
                          (count))

          vega-type (vega-type-fn schema)

          ;; Visualize the columns passed in.
          ;; If not specified, visualize columns found in schema.
          cols (->> (or cols
                        (keys schema))
                 (map keyword)
                 (take 8) ; Either way we will visualize at most 8 columns.
                 (filter vega-type)) ; Only keep the columns that we can determine a vega-type for.

          cols-by-type (group-by vega-type cols)

          counter (let [c (atom 0)]
                    (swap! c inc)
                    @c)

          histograms-quant (histogram-quant-section (get cols-by-type "quantitative")
                                                    vega-type
                                                    samples)
          histograms-nom (histogram-nom-section (get cols-by-type "nominal")
                                                vega-type
                                                samples)

          select-pairs (for [x cols y cols :while (not= x y)] [x y])
          pair-types (group-by #(set (map vega-type %)) select-pairs)

          scatter-plots (scatter-plot-section (get pair-types #{"quantitative"})
                                              vega-type
                                              correlation
                                              samples
                                              counter)
          bubble-plots (bubble-plot-section (get pair-types #{"nominal"})
                                            vega-type
                                            category-limit
                                            samples)
          strip-plots (strip-plot-section (get pair-types #{"quantitative" "nominal"})
                                          vega-type
                                          category-limit
                                          samples
                                          counter)
          sections-1D (remove nil? [histograms-quant histograms-nom])
          sections-2D (remove nil? [scatter-plots strip-plots bubble-plots])
          sections (cond-> []
                     (:1D marginal-types) (concat sections-1D)
                     (:2D marginal-types) (concat sections-2D))]
      (top-level-spec samples num-observed num-virtual sections))))
