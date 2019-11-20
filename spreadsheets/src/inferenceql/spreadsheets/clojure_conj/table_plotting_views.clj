(ns inferenceql.spreadsheets.clojure-conj.table-plotting-views
  (:require
   [clojure.data.json :as json]
   [clojure.java.io :as io]
   [clojure.walk :as walk]
   [medley.core :as medley]))

(def spec
 {:$schema "https://vega.github.io/schema/vega/v5.json",
  :width 770,
  :height 770,
  :padding 2,

  :signals
  [{:name "cellSizeX", :value 40}
   {:name "cellSizeY", :value 10}
   {:name "count", :update "length(data('rowset'))"}
   {:name "width", :update "span(range('position'))"}
   {:name "height", :update "width"},]

  :data
  [{:name "rowset",
    :values []}
   {:name "column-names",
    :source "rowset",
    :transform [{:type "aggregate", :groupby ["col" "col-name"]}]}
   {:name "row-names",
    :source "rowset",
    ;; id -- original row number
    ;; row -- row number in vega plot (accounts for separators)
    :transform [{:type "aggregate", :groupby ["id" "row"]}]}]

  :scales
  [{:name "xpos",
    :type "band",
    :domain {:data "rowset", :field "col", :sort true},
    :range {:step {:signal "cellSizeX"}}}
   {:name "ypos",
    :type "band",
    :domain {:data "rowset", :field "row", :sort true},
    :range {:step {:signal "cellSizeY"}}}],

  :marks
  [{:type "text",
     :name "columns",
     :from {:data "column-names"},
     :encode
     {:update
      {:x {:scale "xpos", :field "col", :band 0.5},
       :y {:offset -2},
       :text {:field "col-name"},
       :fontSize {:value 16},
       :angle {:value -90},
       :align {:value "left"},
       :baseline {:value "middle"},
       :fill [{:value "black"}]}}}
   {:type "text",
     :name "rows",
     :from {:data "row-names"},
     :encode
     {:update
      {:y {:scale "ypos", :field "row", :band 0.5},
       :x {:offset -2},
       :text {:field "id"},
       :fontSize {:value 10},
       :angle {:value 0},
       :align {:value "right"},
       :baseline {:value "middle"},
       :fill [{:value "black"}]}}}]})

(defn add-colors [spec colors]
  (let [add-color (fn [spec group-id color-group]
                    (let [[true-col false-col] color-group
                          new-scale {:name (str "group-color-" group-id),
                                     :type "ordinal",
                                     :range [true-col false-col "#c1c1c1"],
                                     :domain ["True" "False" "NA"]}]
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
                                    id (get row "id")
                                    group-id (get row cluster-key)]
                                {:row y-pos :col col-idx :val (get row col-name) :col-name col-name :id id :separator sep-cell :group group-id}))))]
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

(defn spec-for-view [vid cluster-ids view-col-assignments so-data colors]
  (let [cids (get cluster-ids vid)
        cols (get view-col-assignments vid)
        colors (get colors vid)

        final-spec (-> spec
                       (add-colors colors)
                       (add-primary-data so-data cols vid cids)

                       (add-secondary-data cids)
                       (add-marks-sections cids))]

                       ;; TODO write these
                       ;;(add-columns cols-in-view))]
    (json/write-str final-spec)))

(defn generate-colors [cluster-ids]
  (let [color-list [["blue" "lightblue"] ["green" "lightgreen"] ["firebrick" "salmon"]]
        grab-colors (fn [cids-in-view]
                      (assert (<= (count cids-in-view) (count color-list)))
                      (zipmap cids-in-view color-list))]
    (medley/map-vals grab-colors cluster-ids)))

(def spec-dir "specs/")

(defn write-specs [filename-prefix view-ids cluster-ids view-col-assignments so-data]
  (let [colors (generate-colors cluster-ids)]
    (for [vid view-ids]
      (let [spec (spec-for-view vid cluster-ids view-col-assignments so-data colors)
            spec-file-name (str filename-prefix "view-" vid ".vg.json")]
        (spit (str spec-dir spec-file-name) spec)))))
