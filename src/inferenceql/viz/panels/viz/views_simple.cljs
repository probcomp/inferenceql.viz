(ns inferenceql.viz.panels.viz.views-simple
  "Views for displaying vega-lite specs"
  (:require [vega-embed$vega :as yarn-vega]
            [vega-embed$default :as yarn-vega-embed]
            [reagent.core :as r]))

(def ^:private log-level-default
  (.-Error yarn-vega))

(def ^:private log-level-debug
  (.-Warn yarn-vega))

(def default-vega-embed-options
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

(defn vega-lite
  "vega-lite reagent component"
  [spec opt init-fn data params]
  (let [dom-nodes (r/atom {})
        vega-inst (r/atom nil) ; vega-embed instance.
        free-vega (fn []
                    (when @vega-inst
                      ;; Free resources used by vega-embed.
                      ;; See https://github.com/vega/vega-embed#api-reference
                      (.finalize @vega-inst)))

        update-data (fn [vega data]
                      (when (and vega (seq data))
                        (let [view (.-view vega)]
                          (doseq [[k v] data]
                            (let [cs (.changeset yarn-vega)]
                              (.insert cs (clj->js v))
                              (.remove cs (fn [] true))
                              (.change view (name k) cs)))
                          (.run view))))

        update-params (fn [vega params]
                        (when (and vega (seq params))
                          (let [view (.-view vega)]
                            (doseq [[k v] params]
                              (.signal view (name k) (clj->js v)))
                            (.run view))))

        embed (fn [spec opt init-fn data params]
                (if-not (:vega-node @dom-nodes)
                  (free-vega)
                  (let [spec (clj->js spec)
                        opt (clj->js (merge default-vega-embed-options
                                            opt))]
                    (doto (yarn-vega-embed (:vega-node @dom-nodes)
                                           spec
                                           opt)
                      (.then (fn [res]
                               (update-data res data)
                               (update-params res params)
                               (when init-fn
                                 (init-fn res))))
                      ;; Store the result of vega-embed.
                      (.then (fn [res]
                               (reset! vega-inst res)))
                      (.catch (fn [err]
                                (js/console.error err)))))))]
    (r/create-class
     {:display-name "vega-lite"

      :component-did-mount
      (fn [_]
        (embed spec opt init-fn data params))

      :component-did-update
      (fn [this argv-old]
        (let [[_ spec opt init-fn data params] (r/argv this)
              [_ spec-old opt-old init-fn-old data-old params-old] argv-old]
          (if (not= [spec opt init-fn] [spec-old opt-old init-fn-old])
            ;; When the spec, options, or init-fn changed, we want to completely reset the
            ;; component by calling embed again which creates a new instance of vega-embed.
            (embed spec opt init-fn data params)
            ;; Otherwise, we update the data or params in the current instance of vega-embed
            ;; if needed.
            (do
              (when (not= data data-old)
                (update-data @vega-inst data))

              (when (not= params params-old)
                (update-params @vega-inst params))))))

      :component-will-unmount
      (fn [_]
        (free-vega))

      :reagent-render
      (fn [spec _ _ _ _]
        (when spec
          [:div#viz-container
           [:div {:ref #(swap! dom-nodes assoc :vega-node %)}]]))})))