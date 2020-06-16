(ns inferenceql.spreadsheets.views
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.panels.override.views :as modal]
            [inferenceql.spreadsheets.panels.control.views :as control]
            [inferenceql.spreadsheets.panels.viz.views :as viz]
            [inferenceql.spreadsheets.panels.table.views :as table]))

;;;; Views are expressed in Hiccup-like syntax. See the Reagent docs for more info.

(defn image-table [images]
  [:div.image-table
   [:div.table-title  "NORAD_Num selection"]
   [:div.table
    (for [i images]
      ^{:key (:Name i)}
      [:div.row
       [:div.cell {:style {:background (when (get i "selected--") "palegoldenrod")}}
        [:a {:href (:info-url i) :target "_blank"}
         [:span (:Name i)]]]
       [:div.pic-cell
        [:a {:href (:big-url i) :target "_blank"}
         [:img {:src (:url i)}]]]])]])

(defn app
  []
  (let [real-hot-props @(rf/subscribe [:table/real-hot-props])
        vega-lite-spec @(rf/subscribe [:viz/vega-lite-spec])
        generators      @(rf/subscribe [:viz/generators])
        pts-store @(rf/subscribe [:viz/pts-store])
        virtual @(rf/subscribe [:table/virtual])
        highlight-class @(rf/subscribe [:table/highlight-class])
        images @(rf/subscribe [:viz/images])]
    [:div
     [control/panel]
     [:div#table-container {:class [highlight-class (when virtual "virtual")]}
       [table/handsontable {} real-hot-props]]
     [:div#viz-container
      [:div.flex-box-space-filler-20]
      (when vega-lite-spec
        [viz/vega-lite vega-lite-spec {:actions false} generators pts-store])
      (when images
        [image-table images])
      [:div.flex-box-space-filler-20]]
     [modal/modal]]))
