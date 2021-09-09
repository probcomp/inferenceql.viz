(ns inferenceql.viz.js.components.plot.views
  (:require [vega-embed$default :as yarn-vega-embed]
            [reagent.core :as r]
            [inferenceql.viz.panels.viz.views :refer [default-vega-embed-options]]))

(defn vega-lite
  "Simplified Reagent component for displaying vega-lite specs in Observable"
  [spec opt generators pts-store]
  (let [run (atom 0)
        dom-nodes (r/atom {})
        vega-embed-result (r/atom nil)

        free-resources (fn [] (swap! run inc) ; Turn off any running generators.
                              (when @vega-embed-result
                                ;; Free resources used by vega-embed.
                                ;; See https://github.com/vega/vega-embed#api-reference
                                (.finalize @vega-embed-result)))
        embed (fn [this spec opt generators pts-store]
                (if-not (:vega-node @dom-nodes)
                  (free-resources)
                  (let [spec (clj->js spec)
                        opt (clj->js (merge default-vega-embed-options
                                            opt))]
                    (doto (yarn-vega-embed (:vega-node @dom-nodes)
                                           spec
                                           opt)
                      ;; Store the result of vega-embed.
                      (.then (fn [res]
                               (reset! vega-embed-result res)))
                      (.catch (fn [err]
                                (js/console.error err)))))))]
    (r/create-class
     {:display-name "vega-lite"

      :component-did-mount
      (fn [this]
        (embed this spec opt generators pts-store))

      :component-did-update
      (fn [this old-argv]
        (let [[_ old-spec old-opt old-generators _old-pts-store] old-argv
              [_ new-spec new-opt new-generators current-pts-store] (r/argv this)]
          ;; Only perform the update when it was due to one of these args changing.
          ;; We do not want to update when it is just `pts-store` that changed.
          (when (not= [old-spec old-opt old-generators]
                      [new-spec new-opt new-generators])
            (embed this new-spec new-opt new-generators current-pts-store))))

      :component-will-unmount
      (fn [this]
        (free-resources))

      :reagent-render
      (fn [spec opt generators pts-store]
        (when spec
          [:div#viz-container {:style {:overflow-x "auto"
                                       :min-width "720px"}}
           [:div {:ref #(swap! dom-nodes assoc :vega-node %)}]]))})))
