(ns inferdb.spreadsheets.views
  (:require [oz.core :as oz]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [inferdb.spreadsheets.events :as events]
            [inferdb.spreadsheets.handsontable :as hot]))

(def default-hot-settings
  {:settings {:data                []
              :colHeaders          []
              :rowHeaders          true
              :columnSorting       true
              :manualColumnMove    true
              :filters             true
              :bindRowsWithHeaders true
              :selectionMode       :multiple
              :readOnly            false
              :height              "30vh"
              :width               "100vw"
              :stretchH            "all"
              :licenseKey          "non-commercial-and-evaluation"}
   :hooks events/hooks})

(def ^:private default-search-string
  (pr-str {"percent_black" 0.40}))

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

(defn app
  []
  (let [hot-props @(rf/subscribe [:hot-props])
        selected-maps @(rf/subscribe [:selections])
        vega-lite-spec @(rf/subscribe [:vega-lite-spec])
        scores @(rf/subscribe [:scores])
        db @(rf/subscribe [:whole-db])
        selected-row @(rf/subscribe [:selected-row])]
    [:div
     [hot/handsontable {:style {:overflow "hidden"}} hot-props]
     [search-form "Zane"]
     [:div {:style {:display "flex"
                    :justify-content "center"}}
      (when vega-lite-spec
        [oz/vega-lite vega-lite-spec])]]))
