(ns inferenceql.spreadsheets.panels.upload.events
   "Contains events related to the upload panel."
  (:require [clojure.edn :as edn]
            [re-frame.core :as rf]
            [goog.labs.format.csv :as csv]
            [inferenceql.inference.gpm :as gpm]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]
            [inferenceql.spreadsheets.csv :as csv-utils]))

(rf/reg-event-db
 :upload/set-display
 event-interceptors
 (fn [db [_ new-val]]
   (assoc-in db [:upload-panel :display] new-val)))

(rf/reg-event-fx
 :upload/read-files
 event-interceptors
 (fn [{:keys [_db]} [_ form-data]]
   (let [file-objs (select-keys form-data [:dataset-file :dataset-schema-file :model-file])]
     {:upload/read {:files file-objs
                    :on-success [:upload/process-files form-data]
                    :on-failure [:upload/read-failed]}
      :dispatch-n [[:upload/set-display false]
                   [:table/clear]]})))

(rf/reg-event-fx
 :upload/read-web-url
 event-interceptors
 (fn [{:keys [_db]} [_ form-data]]
   {}))

(rf/reg-event-fx
 :upload/process-files
 event-interceptors
 (fn [{:keys [_db]} [_ form-data file-data]]
   (let [{:keys [dataset-name model-name]} form-data
         dataset-name (keyword dataset-name)
         model-name (keyword model-name)

         {:keys [dataset-file dataset-schema-file model-file]} file-data
         schema (edn/read-string dataset-schema-file)
         dataset-csv (csv/parse dataset-file)
         dataset (csv-utils/csv-data->clean-maps schema dataset-csv {:keywordize-cols true})

         model (gpm/Multimixture (edn/read-string model-file))]
     ;; TODO: catch converrsion errors
     (if true
       {:dispatch-n [[:store/dataset dataset-name dataset schema model-name]
                     [:store/model model-name model]]}
       {:dispatch [:upload/read-failed "TODO: write error message for conversion."]}))))

(rf/reg-event-fx
  :upload/read-failed
  event-interceptors
  (fn [{:keys [_db]} [_ error]]
    {:js/console-error error}))