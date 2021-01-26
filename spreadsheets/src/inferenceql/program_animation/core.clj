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

;; Starting number in model filenames.
;(def meta-csv (csv/read-csv (slurp "raw-data/meta-data.csv")))
;(def low-index  (int (first (second meta-csv))))
;(def high-index (int (second (second meta-csv))))
;; One above the final number in model filnames.
;; Starting number in model filenames.
(def low-index 1)
;; One above the final number in model filnames.
(def high-index 15)

;; How many rows from the dataset to incorporate into each model based on the model number.
;; (Difference between the number of rows to incorporate and the model number)
;;
;; For example model-10 will have 11 rows from the dataset incorporated when rows-offset is 1.
;; And model-11 will have 12 rows incorporated...etc.
;; Normally this should just be set to 0. But my models were a little offset in that way.
(def rows-offset 1)

;---------------------------

(def csv (csv/read-csv (slurp "raw-data/data.csv")))
(def model-dir "raw-data/models/")

(def model-filenames (for [i (range low-index high-index)]
                       (let [pad-i (format "%03d" i)]
                         (str model-dir "model-t-" pad-i  ".json"))))
(def bayesdb-dumps (map #(json/read-str (slurp %)) model-filenames))

(defn get-schema [bdb-dump]
  (let [bdb-types (get bdb-dump "column-statistical-types")

        columns (map keyword (keys bdb-types))
        iql-type {"numerical" :gaussian
                  "nominal" :categorical}
        iql-types (map iql-type (vals bdb-types))]
    (zipmap columns iql-types)))

(def schema (get-schema (first bayesdb-dumps)))
(def all-rows (csv-utils/csv-data->clean-maps schema csv {:keywordize-cols true}))

(def rows-for-models (for [i (range low-index high-index)]
                       (let [num-rows (+ i rows-offset)]
                         (take num-rows all-rows))))

(println "")
(println "")
(println "")
(println "rows-for-models")
(println rows-for-models)
(println "first bayesdb-dumps")
(println (first bayesdb-dumps))
(println "")
(println "")
(println "")

(def programs (for [[bdb-dump rows] (map vector bayesdb-dumps rows-for-models)]
                (->> (bayesdb-import/xcat-gpms bdb-dump rows)
                     (first)
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
  (doseq [[i p] (map vector (range low-index high-index) programs)]
    (println (str "Writing model: " i))
    (let [svg-file (str "images-svg/model-" i ".svg")
          jpg-file (str "images-jpeg/model-" i ".jpg")]
      (spit svg-file (clygments/highlight p :js :svg {:style "xcode"}))
      (sh "svgexport" svg-file jpg-file "pad" "2x" "100%")))

  ;; Resize all images to the max width and height between all of them.
  (let [file-name-wildcard "images-jpeg/model-*.jpg"
        file-sizes (:out (sh "identify" "-format" "%w %h\n" file-name-wildcard))
        max-size (:out (sh "awk" "($1>w){w=$1} ($2>h){h=$2} END{print w\"x\"h}" :in file-sizes))
        size-string (str (str/trim-newline max-size)
                         "+20+20")]
    (println "Resizing jpegs.")
    (sh "mogrify" "-gravity" "NorthWest" "-extent" max-size "-background" "white"
        "-border" "20x20" "-bordercolor" "white" "-colorspace" "RGB" file-name-wildcard)))
