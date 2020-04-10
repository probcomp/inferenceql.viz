(ns inferenceql.spreadsheets.clojure-conj.animation.plot
  (:require
   [clojure.data.json :as json]
   [clojure.java.io :as io]
   [clojure.walk :as walk]
   [clojure.set :as set]
   [medley.core :as medley]
   [clojure.java.shell :refer [sh]]
   [me.raynes.fs :as fs]))


(def spec
 {:$schema "https://vega.github.io/schema/vega/v5.json",
  :width 2000,
  :height 2000,
  :padding 2,

  :signals
  [{:name "cellSizeX", :value 40}
   {:name "cellSizeY", :value 10}
   {:name "count", :update "length(data('rowset'))"}
   {:name "width", :update "span(range('position'))"}
   {:name "height", :update "width"},
   ;; TODO make this get used in other parts of the code.
   {:name "yOffset", :value 200},]

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
      ;; TODO don't hard code this.
       :y {:offset 198},
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
      ;; TODO don't hard code this.
      {:y {:scale "ypos", :field "row", :band 0.5 :offset 200},
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
        rows-by-cid (sort-by first rows-by-cid)
        rows-to-use (map second rows-by-cid)

        separator-group [(zipmap cols (repeat nil))]

        joined-clusters (interpose separator-group rows-to-use)
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
                          ;; TODO don't hard code this.
                          :y {:scale "ypos", :field "row" :offset 200},
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

(defn generate-colors [cluster-ids data-with-cluster-ids]
  (let [colors ["#1f77b4" "#aec7e8" "#ff7f0e" "#ffbb78" "#2ca02c"
                "#98df8a" "#d62728" "#ff9896" "#9467bd" "#c5b0d5"
                "#8c564b" "#c49c94" "#e377c2" "#f7b6d2" "#7f7f7f"
                "#c7c7c7" "#bcbd22" "#dbdb8d" "#17becf" "#9edae5"]
        color-list (cycle (partition 2 colors))


        grab-colors (fn [vid cids-in-view]
                      (let [view-key-str (str "view-" vid)
                            cids-used (distinct (map #(get % view-key-str) data-with-cluster-ids))
                            cids-not-used (set/difference (set cids-in-view) (set cids-used))

                            sorted-cids (concat (sort cids-used) (sort cids-not-used))]
                        (comment
                          (println "cids: ")
                          (println cids-in-view)
                          (println "cids actually used: ")
                          (println cids-used)
                          (println "cids all: ")
                          (println sorted-cids)
                          (println  "cids not used: ")
                          (println cids-not-used))

                        (zipmap sorted-cids color-list)))]
    (medley/map-kv-vals grab-colors cluster-ids)))


(def viz-dir "viz/")
(def spec-dir "specs/")
(def view-png-dir "views/")
(def view-comp-png-dir "comp/")
(def gif-dir "anim/")

(defn write-specs [model-dir filename-prefix view-ids cluster-ids view-col-assignments so-data]
  (let [colors (generate-colors cluster-ids so-data)
        filenames (map #(str filename-prefix "-view-" %) view-ids)
        filenames-vega (map #(str model-dir spec-dir % ".vg.json") filenames)
        filenames-images (map #(str model-dir view-png-dir % ".png") filenames)

        specs (map #(spec-for-view % cluster-ids view-col-assignments so-data colors) view-ids)

        spec-to-png (fn [spec-path img-path]
                      (sh "vg2png" "-s 2" spec-path img-path))]

    ;; Write specs.
    (doseq [[path spec] (map vector filenames-vega specs)]
      (spit path spec))

    ;; TODO Why does writing the previous block like this output specs that are clojure maps instead
    ;; of json objects that returned by `json/write-str`?
    ;(doall (map (fn [path filename] (spit path spec)) filenames-vega specs))

    ;; Convert specs to pngs.
    (doseq [[spec-path img-path] (map vector filenames-vega filenames-images)]
      (spec-to-png spec-path img-path))

    ;; Horizontally concatentate all view pngs.
    (let [output-filename (str model-dir view-comp-png-dir filename-prefix ".png")
          arg-list (concat ["montage"]
                           filenames-images
                           ["-tile" "x1" "-geometry" "+50+50" "-gravity" "North" output-filename])]
      (apply sh arg-list))))

(defn viz-iter [model-num iter-num model-dir partition-data so-data column-mapping]
  (let [model (get partition-data model-num)
        iter (get model iter-num)

        ;; A map: {view-id: [col-name-1 col-name-2 ...]}
        view-col-assignments (->> (get iter "view-partitions")
                                  (medley/map-keys #(Integer/parseInt %))
                                  (medley/map-keys #(get column-mapping %))
                                  (group-by second)
                                  (medley/map-vals #(map first %)))

        ;; A map: {view-id: [cluster-id-for-row-1 cluster-id-for-row-2 ... cluster-id-for-row-n]}
        views (->> (get iter "view-row-partitions")
                   (medley/map-keys #(Integer/parseInt %)))

        ;; Unique view-ids
        view-ids (keys views)
        ;; A maps: {view-id: [cluster-id cluster-id] }
        cluster-ids (medley/map-vals #(vec (distinct %)) views)

        ;; Assigns to each row its cluster-id in the given view, view-id
        assign-view-cids-to-rows (fn [rows view-id cluster-assignments]
                                   (let [view-name (str "view-" view-id)
                                         assign-cid (fn [row cid] (assoc row view-name cid))]
                                     (map assign-cid rows cluster-assignments)))

        data-with-cids (reduce-kv assign-view-cids-to-rows so-data views)

        ;; TEMP only take 50 rows.
        data-with-cids (take 50 data-with-cids)

        filename-prefix (str "iter-" iter-num)]
    (write-specs model-dir filename-prefix view-ids cluster-ids view-col-assignments data-with-cids)))

(defn make-model-dir [base-path]
  ;; Make clean model directory.
  (if (fs/directory? base-path)
    (fs/delete-dir base-path))
  (fs/mkdirs (str base-path spec-dir))
  (fs/mkdirs (str base-path view-png-dir))
  (fs/mkdirs (str base-path view-comp-png-dir))
  (fs/mkdirs (str base-path gif-dir)))

(defn viz-model [partition-data so-data column-mapping model-num]
  "Plots all iterations and an animated gif of the given `model-num`."
  (let [model-dir (str viz-dir "model-" model-num "/")

        ;; number of iterations of this model that we have data for
        num-iters (count (keys (get partition-data model-num)))]
    (make-model-dir model-dir)

    (doseq [iter-num (range num-iters)]
      (viz-iter model-num (str iter-num) model-dir partition-data so-data column-mapping))

    (let [iter-png-wildcard (str model-dir view-comp-png-dir "iter-*.png")
          anim-loc (str model-dir gif-dir "model-" model-num ".gif")]

      ;; Resize all images to the max width and height between all of them.
      (let [file-sizes (:out (sh "identify" "-format" "%w %h\n" iter-png-wildcard))
            max-size (:out (sh "awk" "($1>w){w=$1} ($2>h){h=$2} END{print w\"x\"h}" :in file-sizes))]
        (sh "mogrify" "-gravity" "North" "-extent" max-size "-background" "white" "-colorspace" "RGB" iter-png-wildcard))

      (sh "convert" "-dispose" "previous" "-delay" "10" iter-png-wildcard "-loop" "0" anim-loc))))