(ns inferdb.spreadsheets.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [inferdb.spreadsheets.events :as events]
            [inferdb.spreadsheets.handsontable :as hot]
            [yarn.vega-embed]))

(def default-hot-settings
  {:settings {:data                []
              :colHeaders          []
              :rowHeaders          true
              :columnSorting       true
              :manualColumnMove    true
              :filters             true
              :bindRowsWithHeaders true
              :selectionMode       :multiple
              :readOnly            true
              :height              "30vh"
              :width               "100vw"
              :stretchH            "all"
              :licenseKey          "non-commercial-and-evaluation"}
   :hooks events/hooks})

(def ^:private default-search-string
  (pr-str {"percent_white" 0.40}))

(defn search-form
  [name]
  (let [input-text (r/atom default-search-string)]
    (fn []
      [:div {:style {:display "flex"}}
       [:input {:type "search"
                :style {:width "100%"}
                :on-change #(reset! input-text (-> % .-target .-value))
                :value @input-text}]
       [:button {:on-click #(rf/dispatch [:search @input-text])
                 :style {:float "right"}}
        "Search"]])))

(defn vega-lite
  [spec opt generator]
  (let [run (atom 0)
        embed (fn [this spec opt generator]
                (when spec
                  (let [spec (clj->js spec)
                        opt (clj->js (merge {:renderer "canvas"
                                             :mode "vega-lite"}
                                            opt))]
                    (cond-> (js/vegaEmbed (r/dom-node this)
                                          spec
                                          opt)
                      generator (.then (fn [res]
                                         (let [current-run (swap! run inc)]
                                           (js/requestAnimationFrame
                                            (fn send []
                                              (when (= current-run @run)
                                                (let [datum (generator)
                                                      changeset (.. js/vega
                                                                    (changeset)
                                                                    (insert (clj->js datum)))]
                                                  (.run (.change (.-view res) "data" changeset)))
                                                (js/requestAnimationFrame send)))))))
                      true (.catch (fn [err]
                                     (js/console.error err)))))))]
    (r/create-class
     {:display-name "vega-lite"

      :component-did-mount
      (fn [this]
        (embed this spec opt generator))

      :component-will-update
      (fn [this [_ new-spec new-opt new-generator]]
        (embed this new-spec new-opt new-generator))

      :component-will-unmount
      (fn [this]
        (swap! run inc))

      :reagent-render
      (fn [spec]
        [:div#vis])})))

(defn app
  []
  (let [hot-props      @(rf/subscribe [:hot-props])
        selected-maps  @(rf/subscribe [:selections])
        vega-lite-spec @(rf/subscribe [:vega-lite-spec])
        scores         @(rf/subscribe [:scores])
        generator      @(rf/subscribe [:generator])]
    [:div
     [hot/handsontable {:style {:overflow "hidden"}} hot-props]
     [search-form "Zane"]
     [:div {:style {:display "flex"
                    :justify-content "center"}}
      (when vega-lite-spec
        [vega-lite vega-lite-spec {:actions false} generator])]]))
