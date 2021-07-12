(ns inferenceql.viz.panels.viz.views
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
   :tooltip {:theme "custom"}
   :config {:axis {:labelFontSize 14 :titleFontSize 14 :titlePadding 5}
            :legend {:labelFontSize 12 :titleFontSize 12}
            :header {:labelFontSize 14}
            ;; Remove title from faceted plots.
            :headerFacet {:title nil}
            :concat {:spacing 50}}})

(defn set-pts-store
  [[embed-obj new-val]]
  (let [view-obj (.-view embed-obj)
        spec-has-pts-store (try (some? (.data view-obj "pts_store"))
                                (catch :default e false))]
    (when spec-has-pts-store
      ;; Update value of pts_store in view object to the last value
      ;; we had saved.
      (.data view-obj "pts_store" (clj->js new-val))
      (.run view-obj))))
(rf/reg-fx :viz/set-pts-store set-pts-store)

(defn vega-lite
  "vega-lite reagent component"
  [spec opt id]
  (let [run (atom 0)
        dom-nodes (r/atom {})
        vega-embed-result (r/atom nil)

        ;; NOTE: This assumes id never changes.
        pts-store (rf/subscribe [:viz/pts-store id])

        free-resources (fn [] (swap! run inc) ; Turn off any running generators.
                              (when @vega-embed-result
                                ;; Free resources used by vega-embed.
                                ;; See https://github.com/vega/vega-embed#api-reference
                                (.finalize @vega-embed-result))
                              (rf/dispatch [:viz/clear-instance id]))

        embed (fn [this spec opt id init-pts-store]
                (if-not (:vega-node @dom-nodes)
                  (free-resources)
                  (let [spec (clj->js spec)
                        opt (clj->js (merge default-vega-embed-options
                                            opt))]
                    (doto (js/vegaEmbed (:vega-node @dom-nodes)
                                        spec
                                        opt)

                      ;; Update value of pts_store and attach a listener to it.
                      (.then (fn [res]
                               (let [view-obj (.-view res)
                                     spec-has-pts-store (try (some? (.data view-obj "pts_store"))
                                                             (catch :default e false))]
                                 (when spec-has-pts-store
                                   (set-pts-store [res init-pts-store])
                                   ;; Listen to future updates to pts_store that come from interactions
                                   ;; in the visualization itself. Stage changes.
                                   (.addDataListener view-obj "pts_store"
                                                     (fn [_ds-name data]
                                                       (rf/dispatch [:viz/set-pts-store id data])))))))
                      ;; Store the result of vega-embed.
                      (.then (fn [res]
                               (reset! vega-embed-result res)
                               (rf/dispatch [:viz/set-instance id res])))
                      (.catch (fn [err]
                                (js/console.error err)))))))]
    (r/create-class
     {:display-name "vega-lite"

      :component-did-mount
      (fn [this]
        (embed this spec opt id @pts-store))

      :component-did-update
      (fn [this old-argv]
        (let [[_ old-spec old-opt old-id] old-argv
              [_ new-spec new-opt new-id] (r/argv this)]
          ;; Only perform the update when it was due to one of these args changing.
          ;; We do not want to update when it is just `pts-store` that changed.
          (when (not= [old-spec old-opt old-id]
                      [new-spec new-opt new-id])
            (embed this new-spec new-opt new-id nil))))

      :component-will-unmount
      (fn [this]
        (free-resources))

      :reagent-render
      (fn [spec opt id]
        (when spec
          [:div#viz-container
           [:div.flex-box-space-filler-20]
           [:div {:ref #(swap! dom-nodes assoc :vega-node %)}]
           [:div.flex-box-space-filler-20]]))})))
