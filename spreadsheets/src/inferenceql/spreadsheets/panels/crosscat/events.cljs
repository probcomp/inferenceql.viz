(ns inferenceql.spreadsheets.panels.crosscat.events
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]))

(defn ^:event-db set-option
  "Sets a new value for option

  Args:
    `new-val` - (keyword) The new value to set for option.

  Triggered when:
    The user selects a different option in the Crosscat viz dropdown menu for option."
  [db [_ new-val]]
  (assoc-in db [:crosscat-panel :option] new-val))
(rf/reg-event-db :crosscat/set-option
                 event-interceptors
                 set-option)

