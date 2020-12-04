(ns inferenceql.spreadsheets.panels.more.events
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]))

(defn ^:event-db toggle-show-menu
  "Toggles whether the more menu is displayed or not.

  Triggered when:
    The user clicks a particular option in the more menu.
    The user clicks the background underlay while the more menu is open."
  [db [_]]
  (update-in db [:more-panel :show-menu] not))
(rf/reg-event-db :more/toggle-show-menu
                 event-interceptors
                 toggle-show-menu)

(defn ^:event-db set-show-menu
  "Sets whether the more menu is displayed or not.
  Args:
    `new-val` - The boolean value that determines whether the menu is
      displayed or not.

  Triggered when:
    The user clicks the 'more' button (kebab icon) in the UI."
  [db [_ new-val]]
  (assoc-in db [:more-panel :show-menu] new-val))
(rf/reg-event-db :more/set-show-menu
                 event-interceptors
                 set-show-menu)
