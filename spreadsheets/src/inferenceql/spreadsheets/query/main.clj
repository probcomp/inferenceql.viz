(ns inferenceql.spreadsheets.query.main
  (:require [clojure.data.csv :as csv]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.main :as main]
            [clojure.pprint :as pprint]
            [clojure.tools.cli :as cli]
            [inferenceql.spreadsheets.query :as query]
            [inferenceql.multimixture.search :as search]))

(def cli-options
  [["-d" "--data DATA" "data CSV path"
    :parse-fn io/file]
   ["-m" "--model MODEL" "model EDN path"
    :parse-fn io/file]
   ["-h" "--help"]])

(defn read-model
  [file]
  (-> file (slurp) (edn/read-string) (search/optimized-row-generator)))

(defn read-csv
  [file]
  (clojure.core/with-open [reader (io/reader file)]
    (let [data (csv/read-csv reader)
          headers (map keyword (first data))
          rows (rest data)]
      (mapv #(zipmap headers %)
            rows))))

(defn repl
  [data models]
  (let [repl-options [:prompt #(print "iql> ")
                      :read (fn [request-prompt request-exit]
                              (case (main/skip-whitespace *in*)
                                :line-start request-prompt
                                :stream-end request-exit
                                (read-line)))
                      :eval #(query/q % data models)
                      :print query/p]]
    (apply main/repl repl-options)))

(defn errorln
  [& args]
  (binding [*out* *err*]
    (apply println args)))

(defn -main [& args]
  (let [{:keys [options errors summary] :as opts} (cli/parse-opts args cli-options)
        {:keys [data model help]} options]
    (cond (seq errors)
          (doseq [error errors]
            (errorln error))

          (or help (nil? data) (nil? model))
          (errorln summary)

          :else
          (let [data (read-csv data)
                model (read-model model)]
            (repl data {:model model})))))

(comment

  (require 'clojure.string)
  (apply -main (clojure.string/split "" #"\s+"))
  (apply -main (clojure.string/split "-d test.csv" #"\s+"))
  (apply -main (clojure.string/split "-m test.edn" #"\s+"))
  (apply -main (clojure.string/split "-h" #"\s+"))

  (.isFile (io/file "/Users/zane/Desktop/"))

  )
