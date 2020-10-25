(ns inferenceql.spreadsheets.panels.crosscat.views
  (:require [re-frame.core :as rf]
            [re-com.core :refer [box h-box v-box gap single-dropdown button]]
            [inferenceql.spreadsheets.panels.viz.views :refer [vega-lite]]
            [inferenceql.query :as query]
            [reagent.core :as reagent]
            [inferenceql.inference.gpm :as gpm]
            [clojure.string :as string]
            ))

;;; Vega-lite specs.

;; (def base-spec {:$schema "https://vega.github.io/schema/vega-lite/v4.json",
;;                 :description "A simple bar chart with embedded data.",
;;                 :data {:values [{:a "A", :b 28}
;;                                 {:a "B", :b 55}
;;                                 {:a "C", :b 43}
;;                                 {:a "D", :b 91}
;;                                 {:a "E", :b 81}
;;                                 {:a "F", :b 53}
;;                                 {:a "G", :b 19}
;;                                 {:a "H", :b 87}
;;                                 {:a "I", :b 52}]},
;;                 :mark "bar",
;;                 :encoding {:x {:field "a", :type "nominal", :axis {:labelAngle 0}},
;;                            :y {:field "b", :type "quantitative"}}})

(defn probability-density
  [row_id, column_name, probabilities]
  ;; we're going to assume that row_id - 1 gets you correct index into the probabilities.
  (get (nth probabilities row_id) column_name)
  ;; (rand) ;; TODO: how to run an IQL query? or batch fill this in at the beginning?
  )

(defn percent-rank
  "arr: number[]
   value: number
   
   Return percentile of value within arr
   "
  [arr, value]
  (let [
        ;; https://clojuredocs.org/clojure.core/compare
        numBelow (count (filter (fn [arrValue] (= 1 (compare value arrValue ))) arr))
        numEqual (count (filter (fn [arrValue] (= 0 (compare value arrValue))) arr))
        numerator (* 100 (+ numBelow (* 0.5 numEqual)))
        denominator (count arr)
        rank (/ numerator denominator)
        ]
    ;; (.log js/console value)
    rank
    )
)

;; expand each assignment row to have 1 row/1 metric
;; assumes there is at least 1 row in assignments
;; need to calculate at view level so that percentiles are calculated across every cluster, not just within a cluster.
(defn explode-assignment-rows
  [assignment-rows]
  (let [headers (keys (first (first assignment-rows))) ;; this assumes every row in assignment-rows has the same schema.
        ]
    ;; JS equivalent to flatmap
    (mapcat (fn [[csvRow, metadataRow, probabilityDensityRow, percentileRankRow]] (map  (fn [header] (let [
                                                                ;; (.log js/console :metadataRow metadataRow) ;; is it possible to have more than 1 row id per row??
                                                                 rowId (first (get metadataRow :row-ids))
                                                                 dataValue (get csvRow header)
                                                                 probabilityValue (get probabilityDensityRow header)
                                                                 percentRank (get percentileRankRow header)
                                                                 ]
                                                             {:row_id rowId 
                                                              :metric header             ;; does this need to be stringified, or fine to leave like this?
                                                              :probabilityValue probabilityValue ;; probabilityDensity, from IQL 
                                                              :dataValue dataValue 
                                                              :percentRank percentRank
                                                              }
                                                             )) headers))
            assignment-rows)))


(defn get-row-average-probability
  [[csvRow, metadataRow, probabilityDensityRow, percentileRankRow]]
  (let [headers (keys probabilityDensityRow)
        numHeaders (count headers)
        sumHeaders (reduce + (map (fn [header] (get probabilityDensityRow header)) headers))
      ]
    (/ sumHeaders numHeaders)
   )
  )


(defn make-vega-layer-for-cluster
  [clusterRows]
  (let [
      mappedValues (mapcat (fn [clusterRow] (let [
                                    explodedRows (explode-assignment-rows [clusterRow])
                                    averageProbability (get-row-average-probability clusterRow)
                                  ]
                                   (map (fn [exploded-row] {
                                                            :row_id (get exploded-row :row_id) 
                                                            :metric (get exploded-row :metric) 
                                                            :probabilityValue (get exploded-row :probabilityValue)
                                                            :dataValue (get exploded-row :dataValue) 
                                                            :percentRank (get exploded-row :percentRank) 
                                                            :averageProbability averageProbability
                                                            }) explodedRows)
                                   ))  clusterRows)
      ]
    
  ;;  (.log js/console :clusterRows clusterRows)
  ;;  (.log js/console :mappedValues mappedValues)
  {
    :data {
      :values mappedValues
      }
    :encoding {
               :color {    
                       :field "percentRank"
                        :scale {:scheme "greens"}
                        :type "quantitative"
                                              
                       }
                   :x {:field "metric"
                       :type "nominal" 
                       :sort {:op "mean" :field "averageProbability" :order "descending"}
                      }
                    :y {:field "row_id"
                        :type "nominal" 
                        :sort {:op "sum" :field "averageProbability" :order "descending"}
                        }
    }
    :mark {
           :type "rect",
           :tooltip { :content "data"}
           :clip true
           }
    }
  )
)


(defn make-vega-layer-for-gpm-view
  "Given array of Views from an XCat model (see crosscat/construct-xcat-from-latents)"
  [[view probabilities]]
  (let [;; nested property access: https://stackoverflow.com/a/15639446/5129731
        clusterIds (keys (-> view :latents :counts))
        assignments (get view :assignments)
        headers (keys (first (first assignments))) ;; assume every row is same
        ;; do some pre filtering for percentile calculation
        
        metricValueArrayMap (zipmap
                             headers
                             (map (fn [header] (map (fn [[csvRow, metadataRow]] (get csvRow header)) assignments)) headers))
        
        
        ;; Insert probability densities into each row as 3rd uple of every entry
        assignmentsWithProbabilities (map (fn [[csvRow, metadataRow]]
                                            (let [
                                                rowId (first (get metadataRow :row-ids))
                                                probabilityDensityRow (zipmap
                                                                       headers
                                                                       (map (fn [header] (probability-density rowId header probabilities)) headers))
                                              ]
                                             ;; create copies to prevent mutation, unlike in JS version.
                                            ;; (.log js/console rowId)
                                             [
                                              csvRow
                                              metadataRow
                                              probabilityDensityRow
                                              ]
                                            ))
                                      assignments)
        
        ;; Lastly, lets add percentile rank for every node
        ;; Can't compute this until now because probability density was not available earlier.
        metricValueArrayMap (zipmap
                             headers
                             (map (fn [header] (map (fn [[csvRow, metadataRow, probabilityDensityRow]] (get probabilityDensityRow header)) assignmentsWithProbabilities)) headers))
        
        assignmentsWithPercentileRanks (map (fn [[csvRow, metadataRow, probabilityDensityRow]]
                                            (let [rowId (first (get metadataRow :row-ids))
                                                  percentileRankRow (zipmap
                                                                     headers
                                                                     (map (fn [header] (percent-rank (get metricValueArrayMap header)
                                                                                                     (get probabilityDensityRow header))) headers))]
                                              [csvRow
                                               metadataRow
                                               probabilityDensityRow
                                               percentileRankRow
                                               ]))
                                          assignmentsWithProbabilities)

        ;; inline fucniton to reverse sort order
        rowsByCluster (sort-by count #(compare %2 %1) (map (fn [clusterId] (filter (fn [[csvRow, metadataRow]] (let []
                                                               (not= nil (get (get metadataRow :categories) clusterId))))
                                                   assignmentsWithPercentileRanks)) clusterIds))
                
        ]
    
    (.log js/console :view view)
    ;; (.log js/console :exploded (explode-assignment-rows assignmentsWithProbabilities))
    ;; (.log js/console :clusterIds clusterIds)
    ;; (.log js/console :assignments assignments)
    ;; (.log js/console :rowsByCluster rowsByCluster)
    {:vconcat (map make-vega-layer-for-cluster rowsByCluster)}))

(defn make-crosscat-vega-spec
  "A crosscat visualization in vega-lite"
  [views probabilities]
   {:$schema "https://vega.github.io/schema/vega-lite/v4.json"
    :description "A crosscat visualization"
    :config {:axisX {:grid false :labels false :ticks false :title nil}
             :axisY {:title nil}}
    :hconcat (map make-vega-layer-for-gpm-view (map (fn [view] [view probabilities]) views)) ;; TODO: this is a hacky way to do prop drilling for "probabilities".
    }
  )

;; (def specs {:alpha base-spec
;;             :beta (assoc-in base-spec [:data :values] [{:a "C" :b 30000}
;;                                                        {:a "D" :b 4000}])
;;             :gamma (assoc-in base-spec [:data :values] [{:a "A" :b 99}
;;                                                         {:a "B" :b 300}])})

;;; Main reagent component for Crosscat Viz.

(defn viz-render
  []
  (let [option @(rf/subscribe [:crosscat/option])

        model @(rf/subscribe [:query/model])
        dataset @(rf/subscribe [:query/dataset])

        ;; visual-headers @(rf/subscribe [:table/visual-headers])
        visual-rows @(rf/subscribe [:table/visual-rows])
        ;; table-rows @(rf/subscribe [:table/table-rows]) ;; There may be a bug where if the columns get sorted, we don't know which row_id each row goes with.
        selection-layers @(rf/subscribe [:table/selection-layers])

        views (vals (get model :views))
        spec (make-crosscat-vega-spec views visual-rows) ;; TODO: what to do if user changes their query to something else?       
        ]

    ;; Logging various subs for learning purposes.
    (.log js/console "------------Logging Misc Subs Test--------------------")

    ;; Having a hard time getting the clusters out of model
    ;; https://github.com/probcomp/inferenceql.inference/blob/9c9527bde1cb8476863aa9a2899e47d157a71572/src/inferenceql/inference/gpm/multimixture/search/enumerative.cljc
    (.log js/console :model model)
    (.log js/console :views views)
    ;; (.log js/console :visual-rows visual-rows)
    ;; (.log js/console :visual-headers visual-headers)


    ;; The actual component returned.
    [v-box
     :gap "10px"
     :margin "10px 10px 10px 10px"
     :children [[:h4 "Crosscat State Visualization. Row IDs are 0 indexed."]
                ;; [:h5 (str "Current value for option is " option)]
                ;; [single-dropdown
                ;;  :choices   [{:id :alpha :label "Alpha"}
                ;;              {:id :beta :label "Beta"}
                ;;              {:id :gamma :label "Gamma"}]
                ;;  :model     option
                ;;  :width     "100px"
                ;;  :on-change #(rf/dispatch [:crosscat/set-option %])]
                ;; [button
                ;;  :label "Set option to :gamma"
                ;;  :on-click #(rf/dispatch [:crosscat/set-option :gamma])]
                [vega-lite spec {:actions false} nil nil]]])
)

(defn viz-old
  "A reagent component for displaying a Crosscat visualization."
  []
  viz-render)

;; Trying to approximate JS template literals.
;; // https://andersmurphy.com/2019/01/15/clojure-string-interpolation.html
(defn replace-several [s & {:as replacements}]
  (reduce (fn [s [match replacement]]
            (string/replace s match replacement))
          s replacements))


;; Converted to lifecycle method because we need data fetching on component mount. 
(defn viz
  []
  (let [
        ;; Data for probability density query
        datasets (rf/subscribe [:store/datasets])
        models (rf/subscribe [:store/models])
        model @(rf/subscribe [:query/model])
      ]
           
    (reagent/create-class                 ;; <-- expects a map of functions 
     {:display-name  "crosscat-viz"      ;; for more helpful warnings & errors
      
      ;; https://purelyfunctional.tv/guide/re-frame-lifecycle/#shouldComponentUpdate
      ;; :should-component-update (fn [this old-argv new-argv] false ) ;; TODO: revise this later. For now, prevent table interactions from causing random jumps.

      :component-did-mount               ;; the name of a lifecycle function
      (fn [this]
        (let [
              headers (keys (-> model :latents :z))
              probabilitySubQueries (string/join ", " (map (fn [header] (replace-several "(PROBABILITY OF $header UNDER model AS $header)"
                                                                          "$header" (name header) ;; name instead of str because this is a clojure keyword
                                                                       ) 
                                           ) headers ))
              probabilityQuery (str "SELECT " probabilitySubQueries " FROM data;" )
              ]
          
                  ;; (println "component-did-mount") ;; data fetching
          (.log js/console "------------Component Mounted--------------------")
          (rf/dispatch [:query/parse-query probabilityQuery @datasets @models])) ;; your implementation
         )

      :reagent-render
      viz-render
    })))
