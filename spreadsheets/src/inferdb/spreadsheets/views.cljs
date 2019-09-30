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
              :rowHeaders          true
              :multiColumnSorting  true
              :manualColumnMove    true
              :beforeColumnMove    hot/freeze-col-1-2-fn
              :manualColumnResize  true
              :autoWrapCol         false
              :autoWrapRow         false
              :filters             true
              ;; TODO: investigate more closely what each of
              ;; these options adds. And if they can be put
              ;; in the context-menu instead.
              :dropdownMenu        ["filter_by_condition"
                                    "filter_operators"
                                    "filter_by_condition2"
                                    "filter_by_value"
                                    "filter_action_bar"]
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

(defn search-form
  [name]
  (let [input-text (r/atom default-search-string)]
    (fn []
      [:div {:style {:display "flex"}}
       [:input {:type "search"
                :style {:width "100%"}
                :on-change #(reset! input-text (-> % .-target .-value))
                :on-key-press (fn [e] (if (= (.-key e) "Enter")
                                        (rf/dispatch [:parse-query @input-text])))
                :value @input-text}]
       [:button {:on-click #(rf/dispatch [:parse-query @input-text])
                 :style {:float "right"}}
        "Run InferenceQL"]
       [:button {:on-click #(rf/dispatch [:clear-virtual-data])
                 :style {:float "right"}}
        "Delete virtual data"]])))

(defn app
  []
  (let [real-hot-props      @(rf/subscribe [:real-hot-props])
        virtual-hot-props @(rf/subscribe [:virtual-hot-props])
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
        [vega/vega-lite vega-lite-spec {:actions false} generator])]]))
