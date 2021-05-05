(ns inferenceql.program-animation.core
  (:require [cljstache.core :refer [render]]
            [inferenceql.spreadsheets.config :refer [config]]
            [inferenceql.spreadsheets.model :as model]
            [inferenceql.spreadsheets.panels.jsmodel.multimix :as multimix]

            [inferenceql.spreadsheets.csv :as csv-utils]
            [inferenceql.auto-modeling.bayesdb-import :as bayesdb-import]
            [inferenceql.inference.gpm.crosscat :as crosscat]
            [clojure.string :as str]
            [medley.core :as medley]

            [clojure.data.csv :as csv]
            [clojure.data.json :as json]
            [clojure.edn :as edn]
            [clygments.core :as clygments]
            [clojure.java.shell :refer [sh]]
            [me.raynes.fs :as fs]
            [clojure.pprint :refer [pprint]]))

(def csv (csv/read-csv (slurp "raw-data/data.csv")))
(def bayesdb-dump (json/read-str (slurp "raw-data/data_models.json")))

(defn get-schema [bdb-dump]
  (let [bdb-types (get bdb-dump "column-statistical-types")

        columns (map keyword (keys bdb-types))
        iql-type {"numerical" :gaussian
                  "nominal" :categorical}
        iql-types (map iql-type (vals bdb-types))]
    (zipmap columns iql-types)))

(def schema (get-schema bayesdb-dump))
;; TODO: schema will not have all columns here. Use schema from file?

(def rows (csv-utils/csv-data->clean-maps schema csv {:keywordize-cols true}))

(def xcat-models (bayesdb-import/xcat-gpms bayesdb-dump rows))

(def programs
  (for [model xcat-models]
    (->> model
         (crosscat/xcat->mmix)
         (multimix/template-data)
         (render (:js-model-template config)))))

(defn clean-output-dirs
  "Make clean output directories."
  []
  (let [svg-dir "images-svg/"
        jpeg-dir "images-jpeg/"]
    ;; Delete dirs if the exist.
    (if (fs/directory? svg-dir) (fs/delete-dir svg-dir))
    (if (fs/directory? jpeg-dir) (fs/delete-dir jpeg-dir))
    ;; Create new dirs.
    (fs/mkdirs svg-dir)
    (fs/mkdirs jpeg-dir)))

(defn -main []
  (clean-output-dirs)

  (doseq [[i program] (map-indexed vector programs)]
    (println (str "Writing model " i))
    (let [svg-file (str "images-svg/model-" i ".svg")
          jpg-file (str "images-jpeg/model-" i ".jpg")]
      (spit svg-file (clygments/highlight program :js :svg {:style "xcode"}))
      (sh "svgexport" svg-file jpg-file "pad" "2x" "100%")))

  ;; Resize all images to the max width and height between all of them.
  (let [file-name-wildcard "images-jpeg/model-*.jpg"
        file-sizes (:out (sh "identify" "-format" "%w %h\n" file-name-wildcard))
        max-size (:out (sh "awk" "($1>w){w=$1} ($2>h){h=$2} END{print w\"x\"h}" :in file-sizes))
        size-string (str (str/trim-newline max-size)
                         "+20+20")]
    (println "Resizing jpegs.")
    (sh "mogrify" "-gravity" "NorthWest" "-extent" max-size "-background" "white"
        "-border" "20x20" "-bordercolor" "white" "-colorspace" "RGB" file-name-wildcard))

  ;; Deals with program hanging due to futures.
  ;; https://clojuredocs.org/clojure.java.shell/sh

  (System/exit 0))

;;---------------------------

(comment

  (def ensemble (slurp "raw-data/ensemble.txt"))

  (let [svg-file "images-svg/ensemble.svg"
        jpg-file "images-jpeg/ensemble.jpg"]
    (spit svg-file (clygments/highlight ensemble :js :svg {:style "xcode"}))
    (sh "svgexport" svg-file jpg-file "pad" "2x" "100%")))

;;---------------------------

(comment

  (-main))