(ns inferenceql.spreadsheets.query.main
  (:require [clojure.data.csv :as csv]
            [clojure.edn :as edn]
            [clojure.main :as main]
            [clojure.pprint :as pprint]
            [clojure.tools.cli :as cli]
            [instaparse.core :as insta]
            [inferenceql.spreadsheets.query :as query]
            [inferenceql.multimixture.search :as search]))

(def cli-options
  [["-d" "--data DATA" "data CSV path"]
   ["-m" "--model MODEL" "model EDN path"]
   ["-h" "--help"]])

(defn read-model
  [x]
  (-> (slurp x) (edn/read-string) (search/optimized-row-generator)))

(defn read-csv
  [x]
  (let [data (csv/read-csv (slurp x))
        headers (map keyword (first data))
        rows (rest data)]
    (mapv #(zipmap headers %)
          rows)))

(defn p
  [result]
  (let [columns (:iql/columns (meta result))]
    (if (insta/failure? result)
      (print result)
      (pprint/print-table
       (map name columns)
       (for [row result]
         (reduce-kv (fn [m k v]
                      (assoc m (name k) v))
                    {}
                    row))))))

(defn repl
  [data models]
  (let [repl-options [:prompt #(print "iql> ")
                      :read (fn [request-prompt request-exit]
                              (case (main/skip-whitespace *in*)
                                :line-start request-prompt
                                :stream-end request-exit
                                (read-line)))
                      :eval #(query/q % data models)
                      :print p]]
    (apply main/repl repl-options)))

(defn errorln
  [& args]
  (binding [*out* *err*]
    (apply println args)))

(defn -main [& args]
  (let [{:keys [options errors summary]} (cli/parse-opts args cli-options)
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
