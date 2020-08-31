(ns inferenceql.spreadsheets.panels.upload.events
   "Contains events related to the upload panel."
   (:require [re-frame.core :as rf]
             [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]))

(rf/reg-event-db
 :upload/set-display
 event-interceptors
 (fn [db [_ new-val]]
   (assoc-in db [:upload-panel :display] new-val)))

(rf/reg-event-fx
 :upload/read-files
 event-interceptors
 (fn [{:keys [_db]} [_ form-data]]
   (let [{:keys [dataset-name dataset-file model-name model-file]} form-data]
     {:upload/read [{:file dataset-file
                     :on-success [:upload/store-dataset dataset-name]
                     :on-failure [:upload/read-failed dataset-name]}
                    {:file model-file
                     :on-success [:upload/store-model model-name]
                     :on-failure [:upload/read-failed model-name]}]
      :dispatch [:upload/set-display false]})))

(rf/reg-event-db
  :upload/store-dataset
  event-interceptors
  (fn [db [_ dataset-name dataset-data]]
    (assoc-in db [:datasets (keyword dataset-name)] dataset-data)))

(rf/reg-event-db
 :upload/store-model
 event-interceptors
 (fn [db [_ model-name model-data]]
   (assoc-in db [:models (keyword model-name)] model-data)))

(rf/reg-event-fx
  :upload/read-failed
  event-interceptors
  (fn [{:keys [_db]} [_ name error]]
    {:js/console-error (str "error: could not read " name "\n" error)}))
