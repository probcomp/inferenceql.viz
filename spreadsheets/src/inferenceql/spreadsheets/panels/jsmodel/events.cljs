(ns inferenceql.spreadsheets.panels.jsmodel.events
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]))

(defn ^:event-db toggle-show-model
  "Toggles whether the panel displaying the js-model source is displayed or not.

  Triggered when:
    The user clicks the 'show-model' button in the UI."
  [db [_]]
  (update-in db [:show-model] not))

(rf/reg-event-db :jsmodel/toggle-show-model
                 event-interceptors
                 toggle-show-model)
