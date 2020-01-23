(ns inferenceql.spreadsheets.views
  (:require [re-frame.core :as rf]
            [reagent-forms.core :as forms]
            [inferenceql.spreadsheets.events :as events]
            [inferenceql.spreadsheets.handsontable :as hot]
            [inferenceql.spreadsheets.modal :as modal]
            [inferenceql.spreadsheets.panels.control.views :as control]
            [inferenceql.spreadsheets.panels.viz.views :as viz]))

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

(def real-hot-settings (-> default-hot-settings
                           (assoc-in [:hooks] events/real-hot-hooks)
                           (assoc-in [:name] "real-table")))
(def virtual-hot-settings (-> default-hot-settings
                              (assoc-in [:hooks] events/virtual-hot-hooks)
                              (assoc-in [:name] "virtual-table")
                              (assoc-in [:settings :height] "20vh")))

(defn app
  []
  (let [real-hot-props      @(rf/subscribe [:real-hot-props])
        virtual-hot-props @(rf/subscribe [:virtual-hot-props])
        vega-lite-spec @(rf/subscribe [:vega-lite-spec])
        vega-lite-log-level @(rf/subscribe [:vega-lite-log-level])
        generator      @(rf/subscribe [:generator])]
    [:div
     [control/panel]
     [:div.table-title [:span "Real Data"]]
     [hot/handsontable {:style {:overflow "hidden"}}  real-hot-props]
     [:div.table-title [:span "Virtual Data"]]
     [hot/handsontable {:style {:overflow "hidden"} :class "virtual-hot"} virtual-hot-props]
     [:div#viz-container
      (when vega-lite-spec
        [viz/vega-lite vega-lite-spec {:actions false :logLevel vega-lite-log-level} generator])]
     [modal/modal]]))
