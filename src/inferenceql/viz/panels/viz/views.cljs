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
  [spec opt options]
  (let [dom-nodes (r/atom {})
        vega-embed-result (r/atom nil)

        free-resources (fn [teardown-fn]
                         (when @vega-embed-result
                           ;; Run the user-provided teardown first.
                           (when teardown-fn
                             (teardown-fn @vega-embed-result))

                           ;; Free resources used by vega-embed.
                           ;; See https://github.com/vega/vega-embed#api-reference
                           (.finalize @vega-embed-result)))

        embed (fn [this spec opt options]
                (if-not (:vega-node @dom-nodes)
                  (free-resources (:teardown-fn options))
                  (let [spec (clj->js spec)
                        opt (clj->js (merge default-vega-embed-options
                                            opt))]
                    (doto (yarn-vega-embed (:vega-node @dom-nodes)
                                           spec
                                           opt)
                      (.then (fn [res]
                               (when-let [init-fn (:init-fn options)]
                                 (init-fn res))))
                      ;; Store the result of vega-embed.
                      (.then (fn [res]
                               (reset! vega-embed-result res)))
                      (.catch (fn [err]
                                (js/console.error err)))))))]
    (r/create-class
     {:display-name "vega-lite"

      :component-did-mount
      (fn [this]
        (embed this spec opt options))

      :component-did-update
      (fn [this old-argv]
        (let [[_ old-spec old-opt _old-options] old-argv
              [_ new-spec new-opt new-options] (r/argv this)]
          ;; Only perform the update when it was due to one of these args changing.
          (when (not= [old-spec old-opt] [new-spec new-opt])
            (embed this new-spec new-opt new-options))))

      :component-will-unmount
      (fn [this]
        (let [[_ spec opt options] (r/argv this)]
          (free-resources (:teardown-fn options))))

      :reagent-render
      (fn [spec opt options]
        (when spec
          [:div#viz-container
           [:div {:ref #(swap! dom-nodes assoc :vega-node %)}]]))})))


(defn vega-lite-2
  "vega-lite reagent component"
  [spec opt generators pts-store]
  (let [run (atom 0)

        ;; Used to set the pts-store whenever the mouse click is lifted.
        mouseup-handler (fn [] (rf/dispatch [:viz/set-pts-store]))

        ;; Uses generator functions in map `generators` to generate new rows and
        ;; insert them into `vega-instance`.
        gen-and-insert (fn [generators vega-instance]
                         (doall (for [[dataset-name gen-fn] (seq generators)]
                                  (let [datum (gen-fn)
                                        changeset (.. yarn-vega
                                                      (changeset)
                                                      (insert (clj->js datum)))]
                                    (.. vega-instance
                                        -view
                                        (change (name dataset-name) changeset)
                                        (resize)
                                        (run))))))

        ;; Start generators for inserting data in simulation plots.
        start-gen (fn [generators res]
                    (when (seq generators)
                      (let [current-run (swap! run inc)]
                        (js/requestAnimationFrame
                         (fn send []
                           (when (= current-run @run)
                             (gen-and-insert generators res)
                             (js/requestAnimationFrame send)))))))

        ;; Update value of pts_store and attach a listener to it.
        pts-store-setup (fn [pts-store res]
                         (let [view-obj (.-view res)
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
      :component-did-mount (fn [this]
                             ;; Add global listener for mouseup.
                             (.addEventListener js/window "mouseup" mouseup-handler))
      :component-will-unmount (fn [this]
                                ;; Turn off any running generators.
                                (swap! run inc)

                                ;; Remove global listener for mouseup.
                                (.removeEventListener js/window "mouseup" mouseup-handler))
      :reagent-render (fn [spec opt generators pts-store]
                        (let [init (fn [res]
                                     (pts-store-setup pts-store res)
                                     (start-gen generators res))]
                          [vega-lite spec opt {:init-fn init}]))})))

