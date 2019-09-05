(ns inferdb.spreadsheets.views
  (:require [reagent.core :as r]
            [re-frame.core :as rf]
            [inferdb.spreadsheets.data :as data]
            [inferdb.spreadsheets.events :as events]
            [inferdb.spreadsheets.handsontable :as hot]
            [inferdb.spreadsheets.vega :as vega]))

(def default-hot-settings
  {:settings {:data                []
              :colHeaders          []
              :columns             []
              :cells               hot/cell-style-fn
              :rowHeaders          true
              :multiColumnSorting  true
              :manualColumnMove    true
              :beforeColumnMove    hot/freeze-col-1-2-fn
              :manualColumnResize  true
              :autoWrapCol         false
              :autoWrapRow         false
              :filters             true
              :dropdownMenu        true
              :bindRowsWithHeaders true
              :selectionMode       :range
              :outsideClickDeselects false
              :readOnly            true
              :height              "32vh"
              :width               "100vw"
              :stretchH            "all"
              :licenseKey          "non-commercial-and-evaluation"}
   :hooks []})

(def real-hot-settings (assoc default-hot-settings
                         :hooks events/real-hot-hooks
                         :name "real-table"))
(def virtual-hot-settings (assoc default-hot-settings
                            :hooks events/virtual-hot-hooks
                            :name "virtual-table"))

(def ^:private default-search-string "GENERATE ROW")

(defn confidence-slider []
  (let [cur-val @(rf/subscribe [:confidence-threshold])]
    [:div
      [:span "Confidence Threshold: "]
      [:input {:type :range :name :confidence-threshold
               :min 0 :max 1 :step 0.01
                       :value cur-val
                       :on-change (fn [e]
                                    ;; TODO: find a way to debounce this callback
                                    (let [new-val (js/parseFloat (-> e .-target .-value))]
                                      (rf/dispatch [:set-confidence-threshold new-val])))}]
      [:label cur-val]]))

(defn search-form
  [name]
  (let [input-text (r/atom default-search-string)]
    (fn []
      [:div {:style {:display "flex"}}
       [:input {:type "search"
                :style {:width "40%"}
                :on-change #(reset! input-text (-> % .-target .-value))
                :on-key-press (fn [e] (if (= (.-key e) "Enter")
                                        (rf/dispatch [:run-inference-ql @input-text])))
                :value @input-text}]
       [:button {:on-click #(rf/dispatch [:run-inference-ql @input-text])
                 :style {:float "right"}}
        "Run InferenceQL"]
       [:button {:on-click #(rf/dispatch [:clear-virtual-data])
                 :style {:float "right"}}
        "Delete virtual data"]
       [confidence-slider]])))

(defn app
  []
  (let [real-hot-props      @(rf/subscribe [:real-hot-props])
        virtual-hot-props @(rf/subscribe [:virtual-hot-props])
        vega-lite-spec @(rf/subscribe [:vega-lite-spec])
        scores         @(rf/subscribe [:scores])
        generator      @(rf/subscribe [:generator])]
    [:div
     [search-form "Zane"]
     [:div.table-title
       [:div.main-title
         [:span "Real Data"]]
       (comment
         [:div.sub-title
             [:pre "    rows: real developers    columns: real survey answers"]])]
     [hot/handsontable {:style {:overflow "hidden"}}  real-hot-props]
     [:div.table-title
       [:div.main-title
         [:span "Virtual Data"]]
       (comment
         [:div.sub-title
           [:pre "    rows: virtual developers    columns: virtual survey answers"]])]
     [hot/handsontable {:style {:overflow "hidden"} :class "virtual-hot"} virtual-hot-props]
     [:div {:style {:display "flex"
                    :justify-content "center"}}
      (when vega-lite-spec
        [vega/vega-lite vega-lite-spec {:actions false} generator])]]))
