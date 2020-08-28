(ns inferenceql.spreadsheets.panels.upload.events
   "Contains events related to the upload panel."
   (:require [re-frame.core :as rf]
             [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]))

(rf/reg-event-db
 :upload/set-display
 event-interceptors
 (fn [db [_ new-val]]
   (assoc-in db [:upload-panel :display] new-val)))
