(ns inferenceql.viz.panels.table.views_app
  ""
  (:require [re-frame.core :as rf]
            [medley.core :refer [filter-kv]]
            [inferenceql.viz.panels.table.views :refer [handsontable-base]]))

(defn handsontable
  ""
  [attributes props]
  (let [hot-instance (rf/subscribe [:table/hot-instance])
        hot-reset (fn [new-val]
                    (if new-val
                      (rf/dispatch [:table/set-hot-instance new-val])
                      (rf/dispatch [:table/unset-hot-instance])))]
    (fn [attributes props]
      [handsontable-base hot-instance hot-reset attributes props])))

(defn controls
  "Controls for a handsontable instance."
  [show-table-controls]
  [:div#table-controls {:style {:visibility show-table-controls}}
   [:button.table-button.pure-button
    {:on-click (fn [e]
                 (rf/dispatch [:table/toggle-label-column])
                 (.blur (.-target e)))}
    "labels"]
   [:button.table-button.pure-button
    {:on-click (fn [e]
                 (rf/dispatch [:table/add-row])
                 (.blur (.-target e)))}
    "+row"]
   [:button.table-button.pure-button
    {:on-click (fn [e]
                 (rf/dispatch [:table/delete-row])
                 (.blur (.-target e)))
     :disabled true}
    "-row"]])
