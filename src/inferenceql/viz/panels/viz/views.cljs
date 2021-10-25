(ns inferenceql.viz.panels.viz.views
  "Views for displaying vega-lite specs"
  (:require [vega :as yarn-vega]
            [vega-embed$default :as yarn-vega-embed]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [goog.functions :as gfn]))

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
                            (let [cs (.changeset vega)]
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

(defn vega-lite-2
  "Vega-lite reagent component for the full spreadsheets app.
  Dispatches re-frame events for saving information about selections."
  [spec opt generators pts-store]
  (let [;; Used to stop generators functions.
        run (atom 0)

        ;; Uses generator functions in map `generators` to generate new rows and
        ;; insert them into a vega-instance.
        gen-and-insert (fn [vega generators]
                         (doall (for [[dataset-name gen-fn] (seq generators)]
                                  (let [datum (gen-fn)
                                        changeset (.. yarn-vega
                                                      (changeset)
                                                      (insert (clj->js datum)))]
                                    (.. vega
                                        -view
                                        (change (name dataset-name) changeset)
                                        (resize)
                                        (run))))))

        ;; Start generators for inserting data in simulation plots.
        start-gen (fn [vega generators]
                    (when (seq generators)
                      (let [current-run (swap! run inc)]
                        (js/requestAnimationFrame
                         (fn send []
                           (when (= current-run @run)
                             (gen-and-insert vega generators)
                             (js/requestAnimationFrame send)))))))

        ;; Used to set the pts-store whenever the mouse click is lifted.
        mouseup-handler (fn [] (rf/dispatch [:viz/set-pts-store]))

        ;; Update value of pts_store and attach a listener to it.
        pts-store-setup (fn [vega pts-store]
                         (let [view-obj (.-view vega)
                               spec-has-pts-store (try (some? (.data view-obj "pts_store"))
                                                       (catch :default e false))]
                           (when spec-has-pts-store
                             ;; Update value of pts_store in view object to the last value
                             ;; we had saved.
                             (when pts-store
                               (.data view-obj "pts_store" (clj->js pts-store))
                               (.run view-obj))

                             ;; Listen to future updates to pts_store that come from interactions
                             ;; in the visualization itself. Stage changes.
                             (.addDataListener view-obj "pts_store"
                                               (fn [_ds-name data]
                                                 (rf/dispatch [:viz/stage-pts-store data])))

                             ;; Used to set the pts-store shortly after the mouse wheel is
                             ;; done scrolling on a selection rectangle (pts_brush) within
                             ;; a vega-lite visualization.
                             (.addEventListener view-obj "wheel"
                                                (gfn/debounce
                                                 (fn [_event item]
                                                   (when (= "pts_brush" (.. item -mark -name))
                                                     (rf/dispatch [:viz/set-pts-store])))
                                                 500)))))]
    (r/create-class
     {:display-name "vega-lite-wrapper"
      :component-did-mount (fn [_]
                             ;; Add global listener for mouseup.
                             (.addEventListener js/window "mouseup" mouseup-handler))
      :component-will-unmount (fn [_]
                                ;; Turn off any running generators.
                                (swap! run inc)

                                ;; Remove global listener for mouseup.
                                (.removeEventListener js/window "mouseup" mouseup-handler))
      :reagent-render (fn [spec opt generators pts-store]
                        (let [init (fn [vega]
                                     (pts-store-setup vega pts-store)
                                     (start-gen vega generators))]
                          [vega-lite spec opt init nil nil]))})))

