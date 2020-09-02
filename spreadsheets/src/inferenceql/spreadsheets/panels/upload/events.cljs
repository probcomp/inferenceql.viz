(ns inferenceql.spreadsheets.panels.upload.events
   "Contains events related to the upload panel."
  (:require [re-frame.core :as rf]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]
           [clojure.edn :as edn]))

(rf/reg-event-db
 :upload/set-display
 event-interceptors
 (fn [db [_ new-val]]
   (assoc-in db [:upload-panel :display] new-val)))

(rf/reg-event-fx
 :upload/read-schema-file
 event-interceptors
 (fn [{:keys [_db]} [_ form-data]]
   (let [{:keys [dataset-schema-file]} form-data]
     {:upload/read [{:file dataset-schema-file
                     :on-success [:upload/read-other-files form-data]
                     :on-failure [:upload/read-failed "schema-file"]}]
      :dispatch [:upload/set-display false]})))

(rf/reg-event-fx
 :upload/read-other-files
 event-interceptors
 (fn [{:keys [_db]} [_ form-data schema-data]]
   (let [{:keys [dataset-name dataset-file _dataset-schema-file model-name model-file]} form-data
         dataset-name (keyword dataset-name)
         model-name (keyword model-name)
         schema (edn/read-string schema-data)]
     {:upload/read [{:file dataset-file
                     :on-success [:store/dataset dataset-name schema model-name]
                     :on-failure [:upload/read-failed dataset-name]}
                    {:file model-file
                     :on-success [:store/model model-name]
                     :on-failure [:upload/read-failed model-name]}]
      :dispatch [:upload/set-display false]})))

(rf/reg-event-fx
  :upload/read-failed
  event-interceptors
  (fn [{:keys [_db]} [_ name error]]
    {:js/console-error (str "error: could not read " name "\n" error)}))