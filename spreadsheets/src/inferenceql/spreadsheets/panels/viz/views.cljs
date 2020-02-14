(ns inferenceql.spreadsheets.panels.viz.views
  "Views for displaying vega-lite specs"
  (:require [yarn.vega-embed]
            [reagent.core :as r]))

(def ^:private log-level-default
  (.-Error js/vega))

(def ^:private log-level-debug
  (.-Warn js/vega))

(def ^:private default-vega-embed-options
  {:renderer "svg"
   :mode "vega-lite"
   :logLevel log-level-default
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
        ;; Uses generator functions in map `generators` to generate new rows and
        ;; insert them into `vega-instance`.
        gen-and-insert (fn [generators vega-instance]
                         (doall (for [[dataset-name gen-fn] (seq generators)]
                                  (let [datum (gen-fn)
                                        changeset (.. js/vega
                                                      (changeset)
                                                      (insert (clj->js datum)))]
                                    (.run (.change (.-view vega-instance) (name dataset-name) changeset))))))
        embed (fn [this spec opt generator]
                (when spec
                  (let [spec (clj->js spec)
                        opt (clj->js (merge default-vega-embed-options
                                            opt))]
                    (cond-> (js/vegaEmbed (r/dom-node this)
                                          spec
                                          opt)
                      (seq generator) (.then (fn [res]
                                               (let [current-run (swap! run inc)]
                                                 (js/requestAnimationFrame
                                                  (fn send []
                                                    (when (= current-run @run)
                                                      (gen-and-insert generator res)
                                                      (js/requestAnimationFrame send)))))))
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
