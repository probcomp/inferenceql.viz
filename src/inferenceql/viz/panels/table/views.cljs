(ns inferenceql.viz.panels.table.views
  "Reagent components related the table panel in the full iql.viz application."
  (:require [re-frame.core :as rf]
            [medley.core :refer [filter-kv]]
            [inferenceql.viz.panels.table.views-base :refer [handsontable-base]]))

(defn handsontable
  "A Reagent component for Handsontable.
  Uses re-frame app-db as the data-store for the Handsontable instance used by the component.
  Only one instance of the component is supported at a time in the full iql.viz application."
  [attributes props]
  (let [hot-instance (rf/subscribe [:table/hot-instance])
        hot-reset (fn [new-val]
                    (if new-val
                      (rf/dispatch [:table/set-hot-instance new-val])
                      (rf/dispatch [:table/unset-hot-instance])))]
    (fn [attributes props]
      [handsontable-base hot-instance hot-reset attributes props])))

(defn controls
  "Button controls for editing a Handsontable instance.
  Shows 3 buttons: toggle the label column, add a new row, and delete a user-added row."
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
