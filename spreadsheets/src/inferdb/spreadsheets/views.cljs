(ns inferdb.spreadsheets.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [inferdb.spreadsheets.data :as data]
            [inferdb.spreadsheets.events :as events]
            [inferdb.spreadsheets.handsontable :as hot]
            [yarn.vega-embed]))

(defn before-column-move-fn [columns-moving target]
  """Prevents the movement of the first two columns in the table.
  Also prevents other columns from moving into those frist two spots."""
  (let [first-unfrozen-index 2
        first-col-moving (first (js->clj columns-moving))]
    (not (or (< first-col-moving first-unfrozen-index)
             (< target first-unfrozen-index)))))

(def default-hot-settings
  {:settings {:data                []
              :colHeaders          []
              :columns             []
              :rowHeaders          true
              :multiColumnSorting  true
              :manualColumnMove    true
              :beforeColumnMove    before-column-move-fn
              :filters             true
              :bindRowsWithHeaders true
              :selectionMode       :multiple
              :readOnly            true
              :height              "32vh"
              :width               "100vw"
              :stretchH            "all"
              :licenseKey          "non-commercial-and-evaluation"}
   :hooks events/hooks})

(def virtual-hot-settings (assoc default-hot-settings :hooks events/virtual-hot-hooks))

(def ^:private default-search-string
  (pr-str (select-keys (rand-nth data/nyt-data) [(rand-nth (keys (first data/nyt-data)))])))

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
  (let [hot-props      @(rf/subscribe [:hot-props])
        virtual-hot-props @(rf/subscribe [:virtual-hot-props])
        selected-maps  @(rf/subscribe [:selections])
        vega-lite-spec @(rf/subscribe [:vega-lite-spec])
        scores         @(rf/subscribe [:scores])
        generator      @(rf/subscribe [:generator])
        left-scroll-pos @(rf/subscribe [:left-scroll-pos])
        pos-emmiter @(rf/subscribe [:pos-emmitter])]
    [:div
     [:h1 "Real Data"]
     [:h3 "rows: real developers"]
     [:h3 "columns: real answers to survey questions"]
     [hot/handsontable {:style {:overflow "hidden"}} [pos-emmiter left-scroll-pos] hot-props]
     [:h1 "Virtual Data"]
     [:h3 "rows: virtual developers"]
     [:h3 "columns: virtual answers to survey questions"]
     [hot/handsontable {:style {:overflow "hidden"} :class "virtual-hot"} [pos-emmiter left-scroll-pos] virtual-hot-props]
     [search-form "Zane"]
     [:div {:style {:display "flex"
                    :justify-content "center"}}
      (when vega-lite-spec
        [vega-lite vega-lite-spec {:actions false} generator])]]))
