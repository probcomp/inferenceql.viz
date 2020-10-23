(ns inferenceql.spreadsheets.panels.crosscat.views
  (:require [re-frame.core :as rf]
            [re-com.core :refer [box v-box]]))

(defn viz
  "A reagant component for displaying a Crossscat visualization."
  []
  (let [option @(rf/subscribe [:crosscat/option])]
    [box :child [:div
                 [:h4 "Crosscat Viz"]
                 [:span (str "Current value for option is " option)]]]))
