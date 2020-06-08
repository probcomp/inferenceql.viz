(ns inferenceql.spreadsheets.panels.viz.init
  "Code for initializing vega"
  (:require [yarn.vega-embed]))

(def nyt-color-scheme
  "Color scale used in NYTimes choropleths."
  (let [factory (.interpolate js/vega "rgb-basis")]
    ;(factory "#f2f2f2" "#ff6502")))
    (factory (clj->js ["#f2f2f2" "#f4e5d2" "#fed79c" "#fca52a" "#ff6502"]))))

(defn add-custom-vega-color-schemes
  "Adds custom color schemes to vega runtime."
  []
  (.scheme js/vega "nyt" nyt-color-scheme))

