(ns inferdb.charts.select-simulate
  (:require [clojure.walk :as walk]
            [cheshire.core :as cheshire]
            [inferdb.cgpm.main :as cgpm]
            [inferdb.spreadsheets.data :as data]
            [inferdb.spreadsheets.model :as model]))

(defn -main
  []
  (let [columns ["percent_black" "percent_college"]
        select-values (mapv #(assoc % :origin "select")
                            data/nyt-data)
        simulate-values (->> (cgpm/cgpm-simulate model/census-cgpm
                                                 (mapv keyword columns)
                                                 {}
                                                 {}
                                                 (count data/nyt-data))
                             (map #(assoc % :origin "simulate"))
                             (walk/stringify-keys))
        values (into select-values simulate-values)]
    (println
     (cheshire/generate-string
      {:$schema "https://vega.github.io/schema/vega-lite/v3.json"
       :background "white"
       :data {:values values}
       :mark "circle"
       :encoding (reduce-kv (fn [acc k field]
                              (assoc acc k {:field field
                                            :type "quantitative"}))
                            {:color {:field "origin"
                                     :type "nominal"}}
                            (zipmap [:x :y] columns))}))))
