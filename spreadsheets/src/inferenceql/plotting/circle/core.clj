(ns inferenceql.plotting.circle.core
  (:require [medley.core :as medley]
            [inferenceql.spreadsheets.panels.viz.circle :as circle]
            [clojure.data.csv :as csv]
            [clojure.data.json :as json]
            [clojure.java.shell :refer [sh]]
            [clojure.java.io :as io]
            [me.raynes.fs :as fs]
            [clojure.string :as str]))

(def nodes-dir "raw-data/nodes")
(def edges-dir "raw-data/edges")

(def nodes-filenames (mapv str (filter #(.isFile %) (file-seq (clojure.java.io/file nodes-dir)))))

(def spec-dir "output/vega-specs")
(def png-dir "output/images-png")
(def jpeg-dir "output/images-jpeg")

;-----------------------------------

(defn circle-viz-spec [nodes-filename filename-base]
  (println nodes-filename)
  (let [node-names (->> (slurp nodes-filename)
                        (str/split-lines)
                        (distinct)
                        (map keyword))
        _ (println node-names)
        tree (circle/tree node-names)

        edges-filename (format "%s/%s.edges.txt" edges-dir filename-base)
        edges (-> (slurp edges-filename)
                  (csv/read-csv))

        edges-clean (let [edges (for [edge edges]
                                  (set (map keyword edge)))]
                      (->> edges
                           (remove #(= (count %) 1))
                           (distinct)))

        dependencies (let [tree (remove (comp #(= % -1) :id) tree) ;; Remove the root node.
                           col-ids (zipmap (map :name tree) (map :id tree))
                           proto-dependencies (for [[node-1 node-2] (map seq edges-clean)]
                                                {:source-id (get col-ids node-1)
                                                 :target-id (get col-ids node-2)
                                                 :source-name node-1
                                                 :target-name node-2
                                                 :edge-val nil})]
                       (circle/dependencies proto-dependencies))]
    (circle/spec tree dependencies 360 0)))

(defn make-jpeg-image [spec filename-base]
  (let [spec-filename (format "%s/%s.vg.json" spec-dir filename-base)
        png-filename (format "%s/%s.png" png-dir filename-base)
        jpeg-filename (format "%s/%s.jpg" jpeg-dir filename-base)]
    (spit spec-filename (json/write-str spec))
    (sh "vg2png" spec-filename png-filename)
    (sh "magick" "convert" png-filename
        "-background" "white" "-flatten" "-alpha" "off" "-quality" "100"
        jpeg-filename)))

(defn clean-output-dirs []
  (let [output-dirs ["output/images-png/" "output/images-jpeg" "output/vega-specs"]]
    (doseq [dir output-dirs]
      ;; Delete dirs if the exist.
      (if (fs/directory? dir) (fs/delete-dir dir))
      ;; Create new dir.
      (fs/mkdirs dir))))

(defn -main []
  (clean-output-dirs)
  (doseq [filename nodes-filenames]
    (let [filename-base (-> (.getName (io/file filename))
                            (str/split #"\.")
                            (first))]
      (println (str "converting: " filename-base))

      (let [spec (circle-viz-spec filename filename-base)]
        (make-jpeg-image spec filename-base))))
  ;; Deals with program hanging due to futures.
  ;; https://clojuredocs.org/clojure.java.shell/sh
  (System/exit 0))