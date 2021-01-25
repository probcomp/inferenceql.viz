(ns inferenceql.viz.resize-images
  (:require [clojure.java.shell :refer [sh]]
            [clojure.pprint :refer [pprint]]
            [clojure.string :as str]))

(defn -main []
  ;; Resize all images to the max width and height between all of them.
  (let [file-name-wildcard "table-pngs/table*.png"
        file-sizes (:out (sh "identify" "-format" "%w %h\n" file-name-wildcard))
        max-size (:out (sh "awk" "($1>w){w=$1} ($2>h){h=$2} END{print w\"x\"h}" :in file-sizes))
        size-string (str/trim-newline max-size)]
    (println (str "Resizing pngs to max common size: " size-string))
    (sh "mogrify" "-monitor" "-gravity" "NorthWest" "-extent" max-size "-background" "#f5f5f5"
        "-quality" "100" file-name-wildcard))

  ;; Deals with program hanging due to futures.
  ;; https://clojuredocs.org/clojure.java.shell/sh
  (System/exit 0))
