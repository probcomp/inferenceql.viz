(ns inferenceql.spreadsheets.panels.modal.events
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]))

(defn ^:event-db set-content
  "Set the content of the modal panel.
  When the content is set to something not nil, the modal panel will be displayed with the content.

  Args:
    `content` - the hiccup to set as the content of the modal.

  Triggered when:
    The user clicks a button that displays new content in a model."
  [db [_ content]]
  (assoc-in db [:modal-panel :content] content))
(rf/reg-event-db :modal/set-content
                 event-interceptors
                 set-content)

(defn ^:event-db clear
  "Stops the modal panel from being displayed and clears its content.

  Triggered when:
    The user clicks the greyed-out backdrop of the modal.
    The user clicks a button in the modal content like (submit or cancel)."
  [db [_]]
  (update-in db [:modal-panel] dissoc :content))
(rf/reg-event-db :modal/clear
                 event-interceptors
                 clear)
