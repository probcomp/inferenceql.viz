(ns inferenceql.plotting.circle.core
  (:require [medley.core :as medley]
            [inferenceql.spreadsheets.panels.viz.circle :as circle]
            [clojure.data.csv :as csv]
            [clojure.data.json :as json]
            [clojure.java.shell :refer [sh]]
            [me.raynes.fs :as fs]))

;; TODO: read all the files in the raw-data folder.

(def edges (-> (slurp "raw-data/edges.txt")
               (csv/read-csv)))

(def node-names (->> edges
                     (flatten)
                     (distinct)
                     (map keyword)))
(def tree (circle/tree node-names))

(def edges-clean
  (let [edges (for [edge edges]
                (set (map keyword edge)))]
    (->> edges
         (remove #(= (count %) 1))
         (distinct))))

(def dependencies
  (let [tree (remove (comp #(= % -1) :id) tree) ;; Remove the root node.
        col-ids (zipmap (map :name tree) (map :id tree))
        proto-dependencies (for [[node-1 node-2] (map seq edges-clean)]
                             {:source-id (get col-ids node-1)
                              :target-id (get col-ids node-2)
                              :source-name node-1
                              :target-name node-2
                              :edge-val nil})]
    (circle/dependencies proto-dependencies)))

(def spec (circle/spec tree dependencies 360 0))

(spit "output/vega-specs/edges.vg.json" (json/write-str spec))

(sh "vg2png" "output/vega-specs/edges.vg.json" "output/images-png/edges.png")

(defn clear-output-dirs []
  (let [output-dirs ["output/images-png/" "output/images-jpeg" "output/vega-specs"]]
    (doseq [dir output-dirs]
      ;; Delete dirs if the exist.
      (if (fs/directory? dir) (fs/delete-dir dir))
      ;; Create new dir.
      (fs/mkdirs dir))))

(defn -main []
  (clear-output))