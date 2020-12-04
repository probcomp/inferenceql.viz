(ns inferenceql.spreadsheets.vega
  "Code for initializing vega"
  (:require [yarn.vega-embed]))

(def nyt-color-scheme
  "This is a name for the NYTimes color scheme.
  It can be used to as a colorscheme name in vega-lite specs."
  "nyt")

(def nyt-color-scheme-object
  "Color scale used in NYTimes choropleths."
  (let [factory (.interpolate js/vega "rgb-basis")]
    (factory #js ["#f2f2f2" "#f4e5d2" "#fed79c" "#fca52a" "#ff6502"])))

(defn add-custom-vega-color-schemes
  "Adds custom color schemes to vega runtime."
  []
  (.scheme js/vega nyt-color-scheme nyt-color-scheme-object))
