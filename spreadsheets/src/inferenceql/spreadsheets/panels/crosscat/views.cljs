(ns inferenceql.spreadsheets.panels.crosscat.views
  (:require [re-frame.core :as rf]
            [re-com.core :refer [box h-box v-box gap single-dropdown]]))

(defn viz
  "A reagant component for displaying a Crossscat visualization."
  []
  (let [option @(rf/subscribe [:crosscat/option])]
    [v-box
     :gap "10px"
     :margin "10px 10px 10px 10px"
     :children [[:h4 "Crosscat Viz"]
                [:span (str "Current value for option is " option)]
                [single-dropdown
                 :choices   [{:id :alpha :label "Alpha"}
                             {:id :beta :label "Beta"}
                             {:id :gamma :label "Gamma"}]
                 :model     option
                 :width     "300px"
                 :on-change #(rf/dispatch [:crosscat/set-option %])]]]))