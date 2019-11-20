(ns inferenceql.spreadsheets.clojure-conj.table-plotting-views
  (:require
   [clojure.data.json :as json]
   [clojure.java.io :as io]
   [clojure.walk :as walk]))

(def spec
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
    :transform [{:type "aggregate", :groupby ["col" "col-name"]}]}]

  :scales
  [{:name "xpos",
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
       :fill [{:value "black"}]}}}]})

(defn add-colors [spec colors]
  (let [add-color (fn [spec group-id color-group]
                    (let [[true-col false-col] color-group
                          new-scale {:name (str "group-color-" group-id),
                                     :type "ordinal",
                                     :range [true-col false-col "#c1c1c1"],
                                     :domain [true false "NA"]}]
                      (update-in spec [:scales] conj new-scale)))]
    (reduce-kv add-color spec colors)))

(defn create-primary-data [data cols vid cids]
  (let [cluster-key (str "view-" vid)
        rows-by-cid (group-by #(get % cluster-key) data)

        separator-group [(zipmap cols (repeat nil))]

        joined-clusters (interpose separator-group (vals rows-by-cid))
        all-rows (flatten joined-clusters)


        make-cell-elems (fn [y-pos row]
                          (let [col-pos (map vector (range) cols)]
                            (for [[col-idx col-name] col-pos]
                              (let [sep-cell (nil? (get row col-name))
                                    group-id (get row cluster-key)]
                                {:row y-pos :col col-idx :val (get row col-name) :col-name col-name :separator sep-cell :group group-id}))))]
    (mapcat make-cell-elems (range) all-rows)))

(defn add-primary-data [spec data cols vid cids]
  (let [data-section (create-primary-data data cols vid cids)]
    (assoc-in spec [:data 0 :values] data-section)))

(defn add-secondary-data [spec cids]
  (let [make-section (fn [group-id]
                       {:name (str "group-" group-id)
                        :source "rowset"
                        :transform [{:type "filter", :expr (str "datum.group == " group-id)}]})
        add-section (fn [spec group-id]
                      (let [new-section (make-section group-id)]
                        (update-in spec [:data] conj new-section)))]
    (reduce add-section spec cids)))

(defn add-marks-sections [spec cids]
  (let [make-section (fn [group-id]
                       {:type "rect",
                        :from {:data (str "group-" group-id),}
                        :encode
                        {:update
                         {:x {:scale "xpos", :field "col"},
                          :y {:scale "ypos", :field "row"},
                          :width {:scale "xpos", :band 1, :offset -1},
                          :height {:scale "ypos", :band 1, :offset -1},
                          :fill {:scale (str "group-color-" group-id), :field "val"}}}})
        add-section (fn [spec group-id]
                      (let [new-section (make-section group-id)]
                        (update-in spec [:marks] conj new-section)))]
    (reduce add-section spec cids)))

(defn spec-mult-views [view-ids cluster-ids view-col-assignments so-data colors]
  (let [vid (first view-ids)
        cids (get cluster-ids vid)
        cols (get view-col-assignments vid)
        colors (get colors vid)

        final-spec (-> spec
                       (add-colors colors)
                       (add-primary-data so-data cols vid cids)

                       (add-secondary-data cids)
                       (add-marks-sections cids))]

                       ;; TODO write these
                       ;;(add-columns cols-in-view))]
    (print (json/write-str final-spec))))
