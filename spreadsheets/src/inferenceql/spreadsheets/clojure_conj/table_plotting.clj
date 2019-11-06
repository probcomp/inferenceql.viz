(ns inferenceql.spreadsheets.clojure-conj.table-plotting
  (:require
   [clojure.data.json :as json]
   [clojure.java.io :as io]
   [clojure.walk :as walk]))

;(def foo (json/read (io/reader "spreadsheets/src/inferenceql/spreadsheets/clojure_conj/table.vg.json")))
;(walk/keywordize-keys foo)

(def table-plot-spec
 {:$schema "https://vega.github.io/schema/vega/v5.json",
  :width 770,
  :height 770,
  :padding 2,

  :signals
  [{:name "cellSize", :value 10}
   {:name "count", :update "length(data('rowset'))"}
   {:name "width", :update "span(range('position'))"}
   {:name "height", :update "width"}],

  :data
  [{:name "rowset",
    :values []}
   {:name "column-names",
    :source "rowset",
    :transform [{:type "aggregate", :groupby ["col" "col-name"]}]}],

  :scales
  [{:name "binary-color",
    :type "ordinal",
    :range ["blue" "lightblue" "#c1c1c1"],
    :domain [true false "NA"]}
   {:name "xpos-bak",
    :type "band",
    :domain {:data "rowset", :field "col", :sort true},
    :range {:step {:signal "cellSize"}}}
   {:name "xpos",
    :type "band",
    :domain {:data "rowset", :field "col", :sort true},
    :range {:step {:signal "cellSize"}}}
   {:name "ypos",
    :type "band",
    :domain {:data "rowset", :field "row", :sort true},
    :range {:step {:signal "cellSize"}}}],

  :marks
  [{:type "text",
     :name "columns",
     :from {:data "column-names"},
     :encode
     {:update
      {:x {:scale "xpos", :field "col", :band 0.5},
       :y {:offset -2},
       :text {:field "col-name"},
       :fontSize {:value 10},
       :angle {:value -90},
       :align {:value "left"},
       :baseline {:value "middle"},
       :fill [{:value "black"}]}}}
   {:type "rect",
    :from {:data "rowset"},
    :encode
    {:update
     {:x {:scale "xpos", :field "col"},
      :y {:scale "ypos", :field "row"},
      :width {:scale "xpos", :band 1, :offset -1},
      :height {:scale "ypos", :band 1, :offset -1},
      :fill {:scale "binary-color", :field "val"}}}}]})

(defn spec-with-data [data]
  (let [complete-spec (assoc-in table-plot-spec [:data 0 :values] data)]
    (print (json/write-str complete-spec))))
