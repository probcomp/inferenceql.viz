(ns inferdb.spreadsheets.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [inferdb.spreadsheets.data :as data]
            [inferdb.spreadsheets.events :as events]
            [inferdb.spreadsheets.handsontable :as hot]
            [yarn.vega-embed]))

(def default-hot-settings
  {:settings {:data                []
              :colHeaders          []
              :columns             []
              :rowHeaders          true
              :multiColumnSorting  true
              :manualColumnMove    true
              :beforeColumnMove    hot/freeze-col-1-2-fn
              :filters             true
              :bindRowsWithHeaders true
              :selectionMode       :multiple
              :readOnly            true
              :height              "32vh"
              :width               "100vw"
              :stretchH            "all"
              :licenseKey          "non-commercial-and-evaluation"}
   :hooks []})

(def real-hot-settings (assoc default-hot-settings :hooks events/real-hot-hooks))
(def virtual-hot-settings (assoc default-hot-settings :hooks events/virtual-hot-hooks))

(def ^:private default-search-string "GENERATE ROW")

(defn search-form
  [name]
  (let [input-text (r/atom default-search-string)]
    (fn []
      [:div {:style {:display "flex"}}
       [:input {:type "search"
                :style {:width "100%"}
                :on-change #(reset! input-text (-> % .-target .-value))
                :on-key-press (fn [e] (if (= (.-key e) "Enter")
                                        (rf/dispatch [:search @input-text])))
                :value @input-text}]
       [:button {:on-click #(rf/dispatch [:search @input-text])
                 :style {:float "right"}}
        "Run InferenceQL"]
       [:button {:on-click #(rf/dispatch [:clear-simulations])
                 :style {:float "right"}}
        "Delete virtual data"]])))

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
  (let [real-hot-props      @(rf/subscribe [:real-hot-props])
        virtual-hot-props @(rf/subscribe [:virtual-hot-props])
        selected-maps  @(rf/subscribe [:selections])
        vega-lite-spec @(rf/subscribe [:vega-lite-spec])
        scores         @(rf/subscribe [:scores])
        generator      @(rf/subscribe [:generator])]
    [:div
     [:h1 "Real Data"]
     [:h3 "rows: real developers"]
     [:h3 "columns: real answers to survey questions"]
     [hot/handsontable {:style {:overflow "hidden"}}  real-hot-props]
     [:h1 "Virtual Data"]
     [:h3 "rows: virtual developers"]
     [:h3 "columns: virtual answers to survey questions"]
     [hot/handsontable {:style {:overflow "hidden"} :class "virtual-hot"} virtual-hot-props]
     [search-form "Zane"]
     [:div {:style {:display "flex"
                    :justify-content "center"}}
      (when vega-lite-spec
        [vega-lite vega-lite-spec {:actions false} generator])]]))
