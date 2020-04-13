(ns inferenceql.spreadsheets.query.main
  (:require [clojure.data.csv :as csv]
            [clojure.edn :as edn]
            [clojure.main :as main]
            [clojure.pprint :as pprint]
            [clojure.repl :as repl]
            [clojure.tools.cli :as cli]
            [instaparse.core :as insta]
            [inferenceql.spreadsheets.query :as query]
            [inferenceql.multimixture.search :as search]))

(def cli-options
  [["-d" "--data DATA" "data CSV path"]
   ["-m" "--model MODEL" "model EDN path"]
   ["-h" "--help"]])

(defn slurp-model
  [x]
  (-> (slurp x) (edn/read-string) (search/optimized-row-generator)))

(defn slurp-csv
  [x]
  (let [data (csv/read-csv (slurp x))
        headers (map keyword (first data))
        rows (rest data)]
    (mapv #(zipmap headers %)
          rows)))

(defn p
  [result]
  (cond (insta/failure? result) (print result)
        (instance? Exception result) (repl/pst result)
        :else (let [columns (:iql/columns (meta result))]
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
                      :eval #(try (query/q % data models)
                                  (catch Exception e
                                    e))
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
          (let [data (slurp-csv data)
                model (slurp-model model)]
            (repl data {:model model})))))

(comment

  (p (query/q "select elephant as e, rain as r, (probability of elephant given rain under model as p) from data order by p asc limit 20"
              (slurp-csv "https://bcomp.pro/elephantdata")
              {:model (slurp-model "https://bcomp.pro/elephantmodel")}))

  (p (query/q "select * from (generate elephant under model) limit 10"
              (slurp-csv "https://bcomp.pro/elephantdata")
              {:model (slurp-model "https://bcomp.pro/elephantmodel")}))

  (p (query/q "select elephant, rain, (probability of elephant given rain under model as p) from data order by p limit 10"
              (slurp-csv "https://bcomp.pro/elephantdata")
              {:model (slurp-model "https://bcomp.pro/elephantmodel")}))

  (p (query/q "select * from data order by elephant"
              (slurp-csv "https://bcomp.pro/elephantdata")
              {:model (slurp-model "https://bcomp.pro/elephantmodel")}))

  (p (query/q "select * from data;"
              (slurp-csv "https://bcomp.pro/elephantdata")
              {:model (slurp-model "https://bcomp.pro/elephantmodel")}))

  (pprint/print-table
   '({:elephant "no", :rain "no", :student_happy "yes", :teacher_sick "no"} {:elephant "yes", :rain "yes", :student_happy "yes", :teacher_sick "no"} {:elephant "yes", :rain "yes", :student_happy "yes", :teacher_sick "no"} {:elephant "no", :rain "no", :student_happy "yes", :teacher_sick "no"} {:elephant "no", :rain "no", :student_happy "yes", :teacher_sick "no"} {:elephant "yes", :rain "yes", :student_happy "no", :teacher_sick "no"} {:elephant "no", :rain "yes", :student_happy "no", :teacher_sick "no"} {:elephant "yes", :rain "yes", :student_happy "yes", :teacher_sick "no"} {:elephant "no", :rain "no", :student_happy "yes", :teacher_sick "no"} {:elephant "no", :rain "no", :student_happy "yes", :teacher_sick "no"} {:elephant "yes", :rain "yes", :student_happy "no", :teacher_sick "no"} {:elephant "yes", :rain "yes", :student_happy "yes", :teacher_sick "no"} {:elephant "no", :rain "no", :student_happy "yes", :teacher_sick "no"} {:elephant "yes", :rain "yes", :student_happy "yes", :teacher_sick "no"} {:elephant "yes", :rain "yes", :student_happy "yes", :teacher_sick "no"} {:elephant "no", :rain "no", :student_happy "yes", :teacher_sick "no"} {:elephant "no", :rain "no", :student_happy "yes", :teacher_sick "no"} {:elephant "no", :rain "no", :student_happy "yes", :teacher_sick "no"} {:elephant "yes", :rain "yes", :student_happy "yes", :teacher_sick "no"} {:elephant "no", :rain "no", :student_happy "no", :teacher_sick "no"} {:elephant "no", :rain "no", :student_happy "yes", :teacher_sick "no"} {:elephant "yes", :rain "no", :student_happy "yes", :teacher_sick "no"} {:elephant "yes", :rain "yes", :student_happy "no", :teacher_sick "no"} {:elephant "yes", :rain "yes", :student_happy "yes", :teacher_sick "no"} {:elephant "no", :rain "no", :student_happy "yes", :teacher_sick "no"} {:elephant "no", :rain "no", :student_happy "yes", :teacher_sick "no"} {:elephant "no", :rain "no", :student_happy "yes", :teacher_sick "no"} {:elephant "yes", :rain "no", :student_happy "no", :teacher_sick "no"} {:elephant "no", :rain "no", :student_happy "yes", :teacher_sick "no"} {:elephant "no", :rain "no", :student_happy "yes", :teacher_sick "no"} {:elephant "no", :rain "no", :student_happy "yes", :teacher_sick "no"} {:elephant "yes", :rain "yes", :student_happy "yes", :teacher_sick "no"} {:elephant "no", :rain "no", :student_happy "yes", :teacher_sick "no"} {:elephant "yes", :rain "yes", :student_happy "yes", :teacher_sick "no"} {:elephant "no", :rain "no", :student_happy "no", :teacher_sick "no"} {:elephant "no", :rain "no", :student_happy "yes", :teacher_sick "no"} {:elephant "no", :rain "no", :student_happy "yes", :teacher_sick "no"} {:elephant "yes", :rain "yes", :student_happy "no", :teacher_sick "no"} {:elephant "no", :rain "no", :student_happy "no", :teacher_sick "no"} {:elephant "yes", :rain "no", :student_happy "yes", :teacher_sick "no"} {:elephant "no", :rain "no", :student_happy "yes", :teacher_sick "no"} {:elephant "yes", :rain "no", :student_happy "yes", :teacher_sick "no"} {:elephant "yes", :rain "yes", :student_happy "yes", :teacher_sick "no"} {:elephant "no", :rain "no", :student_happy "yes", :teacher_sick "no"} {:elephant "yes", :rain "yes", :student_happy "no", :teacher_sick "no"} {:elephant "yes", :rain "yes", :student_happy "no", :teacher_sick "no"} {:elephant "no", :rain "yes", :student_happy "yes", :teacher_sick "no"} {:elephant "yes", :rain "yes", :student_happy "yes", :teacher_sick "no"} {:elephant "no", :rain "no", :student_happy "yes", :teacher_sick "no"} {:elephant "yes", :rain "no", :student_happy "yes", :teacher_sick "no"}))

  )
