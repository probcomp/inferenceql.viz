(ns inferenceql.spreadsheets.clojure-conj.table-plotting-views
  (:require
   [clojure.data.json :as json]
   [clojure.java.io :as io]
   [clojure.walk :as walk]
   [medley.core :as medley]
   [clojure.java.shell :refer [sh]]
   [com.evocomputing.colors :as colors]
   [inferenceql.spreadsheets.clojure-conj.color-palette :as palette]
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

(defn generate-colors [cluster-ids]
  (let [gen-colors (fn [num-colors]
                     (let [dark-colors (palette/rainbow-hsl num-colors :start 120 :s 100 :l 50)
                           light-colors (map #(colors/lighten % 40) dark-colors)
                           dark-colors (map colors/rgb-hexstr dark-colors)
                           light-colors (map colors/rgb-hexstr light-colors)]
                       (map vector dark-colors light-colors)))

        grab-colors (fn [cids-in-view]
                      (let [color-list (gen-colors (count cids-in-view))]
                        (zipmap cids-in-view color-list)))]
    (medley/map-vals grab-colors cluster-ids)))

(def viz-dir "viz/")
(def spec-dir "specs/")
(def view-png-dir "views/")
(def view-comp-png-dir "comp/")
(def gif-dir "anim/")

(defn write-specs [model-dir filename-prefix view-ids cluster-ids view-col-assignments so-data]
  (let [colors (generate-colors cluster-ids)
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

        view-col-assignments (->> (get iter "view-partitions")
                                  (medley/map-keys #(Integer/parseInt %))
                                  (medley/map-keys #(get column-mapping %))
                                  (group-by second)
                                  (medley/map-vals #(map first %)))

        views (->> (get iter "view-row-partitions")
                   (medley/map-keys #(Integer/parseInt %)))

        view-ids (keys views)
        cluster-ids (medley/map-vals #(vec (distinct %)) views)

        assign-partitions-to-rows
        (fn [rows views]
          (let [add-view-info (fn [rows view-id cluster-assignments]
                                (let [view-name (str "view-" view-id)]
                                  (map (fn [row c-assignment] (assoc row view-name c-assignment)) rows cluster-assignments)))]
            (reduce-kv add-view-info rows views)))

        clustered-so-data (assign-partitions-to-rows so-data views)

        ;; TEMP hack for testing
        clustered-so-data (take 50 clustered-so-data)

        filename-prefix (str "iter-" iter-num)]
    (write-specs model-dir filename-prefix view-ids cluster-ids view-col-assignments clustered-so-data)))

(defn make-model-dir [base-path]
  ;; Make clean model directory.
  (if (fs/directory? base-path)
    (fs/delete-dir base-path))
  (fs/mkdirs (str base-path spec-dir))
  (fs/mkdirs (str base-path view-png-dir))
  (fs/mkdirs (str base-path view-comp-png-dir))
  (fs/mkdirs (str base-path gif-dir)))

(defn viz-model [partition-data so-data column-mapping model-num]
  (let [model-dir (str viz-dir "model-" model-num "/")]
    (make-model-dir model-dir)

    (doseq [iter-num (range 10)]
      (viz-iter model-num (str iter-num) model-dir partition-data so-data column-mapping))

    (let [iter-png-wildcard (str model-dir view-comp-png-dir "iter-*.png")
          anim-loc (str model-dir gif-dir "model-" model-num ".gif")]

      ;; mogrify -gravity South -extent 3000x3000 -background white -colorspace RGB *png
      ;; identify -format '%w %h\n' *.png | awk '($1>w){w=$1} ($2>h){h=$2} END{print w"x"h}'

      ;; Resize all images to the max width and height between all of them.
      (let [file-sizes (:out (sh "identify" "-format" "%w %h\n" iter-png-wildcard))
            max-size (:out (sh "awk" "($1>w){w=$1} ($2>h){h=$2} END{print w\"x\"h}" :in file-sizes))]
        (sh "mogrify" "-gravity" "North" "-extent" max-size "-background" "white" "-colorspace" "RGB" iter-png-wildcard))

      (sh "convert" "-dispose" "previous" "-delay" "200" iter-png-wildcard "-loop" "1" anim-loc))))
