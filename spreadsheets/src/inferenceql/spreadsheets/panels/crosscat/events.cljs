(ns inferenceql.spreadsheets.panels.crosscat.events
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]))

(defn ^:event-db set-option
  "Sets a new value for option

  Args:
    `new-val` - (keyword) The new value to set for option.

  Triggered when:
    The user selects a different option in the dropdown menu for option.
    The user presses the set option to gamma buttton."
  [db [_ new-val]]
  (assoc-in db [:crosscat-panel :option] new-val))
(rf/reg-event-db :crosscat/set-option
                 event-interceptors
                 set-option)

