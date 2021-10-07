(ns inferenceql.viz.panels.viz.views
  (:require [vega-embed$default :as yarn-vega-embed]
            [vega-embed$vega :as vega]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [clojure.data :refer [diff]]
            [clojure.math.combinatorics :refer [combinations]]
            [inferenceql.viz.panels.viz.dashboard :as dashboard]
            [inferenceql.viz.panels.viz.circle :refer [circle-viz-spec]]
            [inferenceql.viz.model.xcat-util :refer [columns-in-view all-row-assignments]]
            [inferenceql.viz.store :refer [schema xcat-models col-ordering
                                           all-samples observed-samples virtual-samples]]))

(def ^:private log-level-default
  (.-Error vega))

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

(defn mi-plot
  "Reagent component for circle viz for mutual info."
  [mi-data iteration]
  (when mi-data
    (let [mi-threshold @(rf/subscribe [:control/mi-threshold])
          mi-data (-> mi-data (nth iteration) :mi)
          nodes (-> (set (keys mi-data))
                    ;; Get nodes in consistent order by picking from col-ordering.
                    (keep col-ordering))
          edges (filter (fn [[col-1 col-2]]
                          (>= (get-in mi-data [col-1 col-2])
                              mi-threshold))
                        ;; All potential edges
                        (combinations nodes 2))
          circle-spec (circle-viz-spec nodes edges)]
      ;; TODO: make this faster by passing in nodes and edges as datasets.
      [vega-lite circle-spec {:actions false :mode "vega"} nil nil nil nil])))

(defn select-vs-simulate-plot
  "Reagent component for select-vs-simulate plot."
  [cluster-selected iteration]
  (let [viz-cols @(rf/subscribe [:control/col-selection])
        marginal-types @(rf/subscribe [:control/marginal-types])

        xcat-model (nth xcat-models iteration)
        ;; Merge in the view-cluster information only when we have to.
        all-samples (if cluster-selected
                      (let [view-cluster-assignments (concat (all-row-assignments xcat-model)
                                                             (repeat {}))]
                        (concat (map merge observed-samples view-cluster-assignments)
                                virtual-samples))
                      all-samples)
        qc-spec (dashboard/spec all-samples schema nil viz-cols 10 marginal-types)
        cols-in-view (set (columns-in-view xcat-model (:view-id cluster-selected)))]
    [vega-lite qc-spec {:actions false} nil nil all-samples
     {:iter iteration
      :cluster (:cluster-id cluster-selected)
      :view_columns (clj->js (map name cols-in-view))
      :view (some->> (:view-id cluster-selected) (str "view_"))}]))
