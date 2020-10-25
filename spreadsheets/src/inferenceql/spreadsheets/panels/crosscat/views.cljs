(ns inferenceql.spreadsheets.panels.crosscat.views
  (:require [re-frame.core :as rf]
            [re-com.core :refer [box h-box v-box gap single-dropdown button]]
            [inferenceql.spreadsheets.panels.viz.views :refer [vega-lite]]
            [inferenceql.inference.gpm :as gpm]
            ))

;;; Vega-lite specs.

(def base-spec {:$schema "https://vega.github.io/schema/vega-lite/v4.json",
                :description "A simple bar chart with embedded data.",
                :data {:values [{:a "A", :b 28}
                                {:a "B", :b 55}
                                {:a "C", :b 43}
                                {:a "D", :b 91}
                                {:a "E", :b 81}
                                {:a "F", :b 53}
                                {:a "G", :b 19}
                                {:a "H", :b 87}
                                {:a "I", :b 52}]},
                :mark "bar",
                :encoding {:x {:field "a", :type "nominal", :axis {:labelAngle 0}},
                           :y {:field "b", :type "quantitative"}}})
                           
(defn probability-density
  [row_id, column_name]
  (rand) ;; TODO: return something that looks up a real data value with IQL
  )

;; expand each assignment row to have 1 row/1 metric
;; assumes there is at least 1 row in assignments
;; need to calculate at view level so that percentiles are calculated across every cluster, not just within a cluster.
(defn explode-assignment-rows
  [assignment-rows]
  (let [headers (keys (first (first assignment-rows))) ;; this assumes every row in assignment-rows has the same schema.
        ]
    ;; JS flatmap
    (mapcat (fn [[csvRow, metadataRow]] (map  (fn [header] (let [rowId (first (get metadataRow :row-ids))
                                                                 rowValue (probability-density rowId header)]
                                                             {:row_id rowId 
                                                              :metric header 
                                                              :value rowValue} ;; TODO: figure out if header needs to turn to string.
                                                             )) headers))
            assignment-rows)))


(defn make-vega-layer-for-cluster
  [clusterRows]
  (let [
      mappedValues (mapcat (fn [clusterRow] (let [
                                    explodedRows (explode-assignment-rows [clusterRow])
                                  ]
                                  ;; (.log js/console :metadataRow metadataRow) ;; is it possible to have more than 1 row id per row??
                                   (map (fn [exploded-row] {
                                                            :row_id (get exploded-row :row_id) 
                                                            :metric (get exploded-row :metric) 
                                                            :value (get exploded-row :value) ;; probability density, not raw data value
                                                            :percentRank 0 ;; TODO: make this a part of the assignment row higher in the call chain
                                                            :averageProbability 0 ;; TODO: average the values in explodedRows
                                                            }) explodedRows)
                                   ))  clusterRows)
      ]
    
   (.log js/console :clusterRows clusterRows)
   (.log js/console :mappedValues mappedValues)
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
                       :type "nominal" :sort {:op "mean" :field "value" :order "descending"}}
                    :y {:field "row_id"
                        :type "nominal" :sort {:op "sum" :field "value" :order "descending"}}
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
  [view]
  (let [;; nested property access: https://stackoverflow.com/a/15639446/5129731
        clusterIds (keys (-> view :latents :counts))
        assignments (get view :assignments)

        rowsByCluster (map (fn [clusterId] (filter (fn [[csvRow, metadataRow]] (let []
                                                               (not= nil (get (get metadataRow :categories) clusterId))))
                                                   assignments)) clusterIds)]
    (.log js/console :view view)
    (.log js/console :assign (explode-assignment-rows assignments))
    ;; (.log js/console :clusterIds clusterIds)
    ;; (.log js/console :assignments assignments)
    ;; (.log js/console :rowsByCluster rowsByCluster)
    {:vconcat (map make-vega-layer-for-cluster rowsByCluster)}))

(defn make-crosscat-vega-spec
  "A crosscat visualization in vega-lite"
  [views]
   {:$schema "https://vega.github.io/schema/vega-lite/v4.json"
    :description "A crosscat visualization"
    :config {:axisX {:grid false :labels false :ticks false :title nil}
             :axisY {:title nil}}
    :hconcat (map make-vega-layer-for-gpm-view views)
    }
  )

(def specs {:alpha base-spec
            :beta (assoc-in base-spec [:data :values] [{:a "C" :b 30000}
                                                       {:a "D" :b 4000}])
            :gamma (assoc-in base-spec [:data :values] [{:a "A" :b 99}
                                                        {:a "B" :b 300}])})

;;; Main reagent component for Crosscat Viz.

(defn viz
  "A reagent component for displaying a Crosscat visualization."
  []
  (let [option @(rf/subscribe [:crosscat/option])
        ;; spec (get specs option)

        model @(rf/subscribe [:query/model])
        dataset @(rf/subscribe [:query/dataset])
        visual-headers @(rf/subscribe [:table/visual-headers])
        visual-rows @(rf/subscribe [:table/visual-rows])
        selection-layers @(rf/subscribe [:table/selection-layers])
        
        views (vals (get model :views))
        spec (make-crosscat-vega-spec views)
        ]

    ;; Logging various subs for learning purposes.
    (.log js/console "------------Logging Misc Subs Test--------------------")
    
    ;; Having a hard time getting the clusters out of model
    ;; https://github.com/probcomp/inferenceql.inference/blob/9c9527bde1cb8476863aa9a2899e47d157a71572/src/inferenceql/inference/gpm/multimixture/search/enumerative.cljc
    (.log js/console :model model)
    (.log js/console :views views )
    ;; (.log js/console :views (type views) )
    ;; (.log js/console :views (keys (first views)))
    ;; (.log js/console :views (get (first views) :latents) )
    ;; (.log js/console :vega-spec (vals (get model :views)))
    ;; (.log js/console :vega-spec (make-crosscat-vega-spec views) )

    ;; (.log js/console :dataset dataset)
    ;; (.log js/console :visual-headers visual-headers)
    ;; (.log js/console :visual-rows visual-rows)
    ;; (.log js/console :selection-layers selection-layers)

    ;; The actual component returned.
    [v-box
     :gap "10px"
     :margin "10px 10px 10px 10px"
     :children [[:h4 "Crosscat Viz"]
                [:h5 (str "Current value for option is " option)]
                [single-dropdown
                 :choices   [{:id :alpha :label "Alpha"}
                             {:id :beta :label "Beta"}
                             {:id :gamma :label "Gamma"}]
                 :model     option
                 :width     "100px"
                 :on-change #(rf/dispatch [:crosscat/set-option %])]
                [button
                 :label "Set option to :gamma"
                 :on-click #(rf/dispatch [:crosscat/set-option :gamma])]
                [vega-lite spec {:actions false} nil nil]]]))
