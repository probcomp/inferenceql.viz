(ns inferenceql.viz.panels.viz.views
  "Views for displaying vega-lite specs"
  (:require [vega :as yarn-vega]
            [vega-embed$default :as yarn-vega-embed]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [re-com.core :refer [h-box gap]]
            [goog.functions :as gfn]
            [inferenceql.viz.panels.viz.views-simple
             :refer [vega-lite]
             :rename {vega-lite vega-lite-simple}]))

(defn vega-lite
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
                          [h-box :children [[gap :size "1 0 20px"]
                                            [vega-lite-simple spec opt init nil nil]
                                            [gap :size "1 0 20px"]]]))})))
