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

;; TODO: add :js/console-warning effect.
;; TODO: add real parsing functions.
;; TODO: add comments to keep this sane.

(rf/reg-event-fx
 :upload/read-files-phase-1
 event-interceptors
 (fn [{:keys [_db]} [_ form-data]]
   (let [{:keys [dataset-schema-file]} form-data]
     {:upload/read [{:file dataset-schema-file
                     :on-success [:upload/read-files-phase-2 form-data]
                     :on-failure [:upload/read-failed "schema-file"]}]
      :dispatch-n [[:upload/set-display false]]})))

(rf/reg-event-fx
 :upload/read-files-phase-2
 event-interceptors
 (fn [{:keys [_db]} [_ form-data schema-data]]
   (let [{:keys [model-file]} form-data
         schema (edn/read-string schema-data)]
         ;; TODO: handle exception if edn parsing fails.
     {:js/console-warning "parsed schema file ok"
      :upload/read [{:file model-file
                     :on-success [:upload/read-files-phase-3 form-data schema]
                     :on-failure [:upload/read-failed "model-file"]}]})))

(rf/reg-event-fx
 :upload/read-files-phase-3
 event-interceptors
 (fn [{:keys [_db]} [_ form-data schema model-data]]
   (let [{:keys [dataset-file]} form-data
         model (do-stuff model-data)]
     ;; TODO: handle exception if model parsing fails.
     {:js/console-warning "parsed model file ok"
      :upload/read [{:file dataset-file
                     :on-success [:upload/read-files-phase-4 form-data schema model]
                     :on-failure [:upload/read-failed "dataset-file"]}]})))

(rf/reg-event-fx
 :upload/read-files-phase-4
 event-interceptors
 (fn [{:keys [_db]} [_ form-data schema model dataset-data]]
   (let [{:keys [dataset-name model-name]} form-data
         dataset-name (keyword dataset-name)
         model-name (keyword model-name)
         dataset (do-stuff dataset-data)]
     ;; TODO: handle exception if dataset parsing fails.
     {:js/console-warning "parsed dataset file ok"
      :dispatch-n [[:upload/set-display false]
                   [:store/dataset dataset-name schema model-name dataset]
                   [:store/model model-name model]
                   [:table/clear]]})))

(rf/reg-event-fx
  :upload/read-failed
  event-interceptors
  (fn [{:keys [_db]} [_ name error]]
    {:js/console-error (str "error: could not read " name "\n" error)}))