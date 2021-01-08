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

(def low-index 10)
(def high-index 100)

(def csv (csv/read-csv (slurp "raw-data/data.csv")))
(def model-dir "raw-data/models/")

(def model-filenames (for [i (range low-index high-index)]
                       (let [pad-i (format "%04d" i)]
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
(def rows (csv-utils/csv-data->clean-maps schema csv {:keywordize-cols true}))

(def programs (->> bayesdb-dumps
                   (map #(bayesdb-import/xcat-gpms % rows))
                   (map first)
                   (map crosscat/xcat->mmix)
                   (map #(render (:js-model-template config) (multimix/template-data %)))))

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


;-------------------------

;; Holds two XCat records. One from model 27 and one from model 28.
(def xcat-recs (->> bayesdb-dumps
                    (map #(bayesdb-import/xcat-gpms % csv))
                    (map first)))

;; Dumping out the Xcat records to .edn files to inspect by hand.
#_(for [[i program] (map vector [27 28] xcat-recs)]
    (let [program-nice (with-out-str (pprint (into {} program)))]
      (spit (str "program_" i ".edn") program-nice)))

;; Model 27 converts to multi mix just fine.
#_(crosscat/xcat->mmix (first xcat-recs))

;; Model 28 fails however.
#_(crosscat/xcat->mmix (second xcat-recs))

;;-------------------------

;; Trying original model.

(comment
  (def csv (csv/read-csv (slurp "raw-data-2/data.csv")))
  (def model "raw-data-2/data_models.json")

  (def bayesdb-dump (#(json/read-str (slurp %)) model))

  (def xcat (-> bayesdb-dump
                (bayesdb-import/xcat-gpms csv)
                (first)))

  (crosscat/xcat->mmix xcat))



