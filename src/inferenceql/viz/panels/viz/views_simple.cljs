(ns inferenceql.viz.panels.viz.views-simple
  "Reagent component for displaying vega-lite specs. Can be used independently from iql.viz app."
  (:require [vega-embed$vega :as yarn-vega]
            [vega-embed$default :as yarn-vega-embed]
            [reagent.core :as r]
            [medley.core :as medley]))

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
  "Reagent component for displaying vega-lite specs. Can be used independently of the iql.viz app.

  Args:
    spec -- a vega-lite spec.
    opt -- (map) options to pass to vega-embed runtime.
    init-fn -- (function) Ran after the vega-embed instance in initialized and data and
      params have been updated. Recieves an instance of vega-embed. Used to perform additional
      setup on the vega-embed instance. Can be nil.
    data -- (map) Dataset name to data. Will be used to update the datasets in the vega-embed
      instance.
    params -- (map) Param name to value. Will be used to update values of the params in the
      vega-embed instance.

  Only changing the `spec` will create a new vega-embed instance. Changes to `opt` and `init-fn` are
  ignored. Changes to `data` and `params` are applied to the existing vega-embed instance."
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

        ;; Creates a new instance of vega-embed.
        embed (fn [spec opt init-fn data params]
                (when spec
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
                      (.then (fn [res]
                               ;; Store the result of vega-embed.
                               (reset! vega-inst res)))
                      (.catch (fn [err]
                                (js/console.error err)))))))]
    (r/create-class
     {:display-name "vega-lite-simple"

      :component-did-mount
      (fn [_]
        (embed spec opt init-fn data params))

      :component-did-update
      (fn [this argv-old]
        (let [[_ spec opt init-fn data params] (r/argv this)
              [_ spec-old _ _ data-old params-old] argv-old]
          (if (not= spec spec-old)
            (do
              ;; When the spec changed, we want to completely reset the component by calling embed
              ;; again which creates a new instance of vega-embed.
              (free-vega)
              (embed spec opt init-fn data params))
            (do
              ;; Otherwise, we update the data or params in the current instance of vega-embed
              ;; if needed.
              (when (not= data data-old)
                ;; Only update the datasets that have changed.
                (let [data-changed (medley/filter-kv (fn [k v]
                                                       (and (contains? data-old k)
                                                            (not= v (get data-old k))))
                                                     data)]
                  (update-data @vega-inst data-changed)))
              (when (not= params params-old)
                ;; Only update the params that have changed.
                (let [params-changed (medley/filter-kv (fn [k v]
                                                         (and (contains? params-old k)
                                                              (not= v (get params-old k))))
                                                       params)]
                  (update-params @vega-inst params-changed)))))))

      :component-will-unmount
      (fn [_]
        (free-vega))

      :reagent-render
      (fn [_ _ _ _ _]
        [:div#viz-container
         [:div {:ref #(swap! dom-nodes assoc :vega-node %)}]])})))
