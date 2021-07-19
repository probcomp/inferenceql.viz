(ns inferenceql.viz.vega
  "Code for initializing vega"
  (:require [vega :as yarn-vega]))

(defn add-custom-vega-color-schemes
  "Adds custom color schemes to vega runtime."
  []
  (let [factory (.interpolate yarn-vega "rgb-basis")

        ;; Color scale used in NYTimes choropleths.
        nyt-color-scheme (factory #js ["#f2f2f2" "#f4e5d2" "#fed79c" "#fca52a" "#ff6502"])]

    ;; "nyt" will be available as a colorscheme name in vega-lite specs.
    (.scheme yarn-vega "nyt" nyt-color-scheme)))
