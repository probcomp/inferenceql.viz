(ns inferenceql.viz.js.components.plot.views
  (:require [vega-embed$default :as yarn-vega-embed]
            [vega-embed$vega :as vega]
            [reagent.core :as r]
            [inferenceql.viz.panels.viz.views :refer [default-vega-embed-options]]
            [clojure.data :refer [diff]]))

(defn vega-lite
  "Simplified Reagent component for displaying vega-lite specs in Observable"
  [spec opt generators pts-store data params]
  (let [run (atom 0)
        dom-nodes (r/atom {})
        vega-embed-result (r/atom nil)

        free-resources (fn [] (swap! run inc) ; Turn off any running generators.
                              (when @vega-embed-result
                                ;; Free resources used by vega-embed.
                                ;; See https://github.com/vega/vega-embed#api-reference
                                (.finalize @vega-embed-result)))
        embed (fn [this spec opt generators pts-store data params]
                (free-resources)
                (when (:vega-node @dom-nodes)
                  (let [spec (clj->js spec)
                        opt (clj->js (merge default-vega-embed-options
                                            opt))]
                    (doto (yarn-vega-embed (:vega-node @dom-nodes)
                                           spec
                                           opt)
                      ;; Store the result of vega-embed.
                      (.then (fn [res]
                               (reset! vega-embed-result res)))
                      (.then (fn [res]
                               (when (seq data)
                                 (let [view (.-view res)]
                                   (.insert view "rows" (clj->js data))
                                   (.run view)))))
                      (.then (fn [res]
                               (when (seq params)
                                 (let [view (.-view res)]
                                   (doseq [[k v] params]
                                     (.signal view (name k) v))
                                   (.run view)))))
                      (.catch (fn [err]
                                (js/console.error err)))))))]
    (r/create-class
     {:display-name "vega-lite"

      :component-did-mount
      (fn [this]
        (embed this spec opt generators pts-store data params))

      :component-did-update
      (fn [this old-argv]
        (let [[_ old-spec old-opt old-generators _old-pts-store old-data old-params] old-argv
              [_ new-spec new-opt new-generators current-pts-store new-data new-params] (r/argv this)]
          ;; Only perform the update when it was due to one of these args changing.
          ;; We do not want to update when it is just `pts-store` that changed.
          (when (not= [old-spec old-opt old-generators]
                      [new-spec new-opt new-generators])
            (embed this new-spec new-opt new-generators current-pts-store new-data new-params))

          (when (not= old-data new-data)
            (when-let [v @vega-embed-result]
              (let [cs (.changeset vega)
                    view (.-view v)]
                (.insert cs (clj->js new-data))
                (.remove cs (fn [] true))
                (.change view "rows" cs)
                (.run view))))

          (when (not= old-params new-params)
            (when-let [v @vega-embed-result]
              (let [view (.-view v)]
                (doseq [[k v] new-params]
                  (.signal view (name k) v))
                (.run view))))))

      :component-will-unmount
      (fn [this]
        (free-resources))

      :reagent-render
      (fn [spec opt generators pts-store data params]
        (when spec
          [:div#viz-container {:style {:min-width "720px"}}
           [:div {:ref #(swap! dom-nodes assoc :vega-node %)}]]))})))
