(ns inferenceql.spreadsheets.panels.viz.views
  "Views for displaying vega-lite specs"
  (:require [yarn.vega-embed]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [goog.functions :as gfn]))

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
  [spec opt generators]
  (let [run (atom 0)
        ;; Uses generator functions in map `generators` to generate new rows and
        ;; insert them into `vega-instance`.
        gen-and-insert (fn [generators vega-instance]
                         (doall (for [[dataset-name gen-fn] (seq generators)]
                                  (let [datum (gen-fn)
                                        changeset (.. js/vega
                                                      (changeset)
                                                      (insert (clj->js datum)))]
                                    (.. vega-instance
                                        -view
                                        (change (name dataset-name) changeset)
                                        (resize)
                                        (run))))))
        embed (fn [this spec opt generators]
                (when spec
                  (let [spec (clj->js spec)
                        opt (clj->js (merge default-vega-embed-options
                                            opt))]
                    (cond-> (js/vegaEmbed (r/dom-node this)
                                          spec
                                          opt)
                      (seq generators) (.then (fn [res]
                                               (let [current-run (swap! run inc)]
                                                 (js/requestAnimationFrame
                                                  (fn send []
                                                    (when (= current-run @run)
                                                      (gen-and-insert generators res)
                                                      (js/requestAnimationFrame send)))))))
                      :always (.then (fn [res]
                                       ;; TODO: should check that pts_store exists or make this optional
                                       ;; in a different way.
                                       (.addDataListener (.-view res) "pts_store"
                                                         (gfn/debounce
                                                           (fn [_ds-name data]
                                                             (rf/dispatch [:viz/set-pts-store data]))
                                                           150))))
                      :always (.catch (fn [err]
                                        (js/console.error err)))))))]
    (r/create-class
     {:display-name "vega-lite"

      :component-did-mount
      (fn [this]
        (embed this spec opt generators))

      :component-will-update
      (fn [this [_ new-spec new-opt new-generators]]
        (embed this new-spec new-opt new-generators))

      :component-will-unmount
      (fn [this]
        (swap! run inc))

      :reagent-render
      (fn [spec]
        [:div#vis])})))
