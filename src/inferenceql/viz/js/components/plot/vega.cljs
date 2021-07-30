(ns inferenceql.viz.js.components.plot.vega
  (:require [clojure.set :refer [rename-keys]]
            [inferenceql.viz.panels.viz.vega :as app-vega]))

(defn generate-spec
  "Simplified interface for using spreadsheet app's vega-lite spec generating code"
  [schema data column-lists]
  (let [selection-layers (for [cl column-lists]
                           {:selections data
                            :selected-columns cl})
        spec (app-vega/generate-spec schema nil nil nil selection-layers)]
    (-> spec
        (rename-keys {:hconcat :concat})
        (assoc :columns 2))))
