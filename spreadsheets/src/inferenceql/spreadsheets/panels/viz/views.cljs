(ns inferenceql.spreadsheets.panels.viz.views
  "Views for displaying vega-lite specs"
  (:require [yarn.vega-embed]
            [reagent.core :as r]))

(def ^:private default-vega-embed-options
  {:renderer "svg"
   :mode "vega-lite"
   :config {:axis {:labelFontSize 14 :titleFontSize 14 :titlePadding 5}
            :legend {:labelFontSize 12 :titleFontSize 12}
            :header {:labelFontSize 14}
            :mark {:tooltip true}
            ;; Remove title from faceted plots.
            :headerFacet {:title nil}
            :concat {:spacing 50}}})

(defn vega-lite
  "vega-lite reagent component"
  [spec opt generator]
  (let [run (atom 0)
        embed (fn [this spec opt generator]
                (when spec
                  (let [spec (clj->js spec)
                        opt (clj->js (merge default-vega-embed-options
                                            opt))]
                    (cond-> (js/vegaEmbed (r/dom-node this)
                                          spec
                                          opt)
                      true (.catch (fn [err]
                                     (js/console.error err)))))))]
    (r/create-class
     {:display-name "vega-lite"

      :component-did-mount
      (fn [this]
        (embed this spec opt generator))

      :component-will-update
      (fn [this [_ new-spec new-opt new-generator]]
        (embed this new-spec new-opt new-generator))

      :component-will-unmount
      (fn [this]
        (swap! run inc))

      :reagent-render
      (fn [spec]
        [:div#vis])})))
