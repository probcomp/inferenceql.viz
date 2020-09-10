(ns inferenceql.spreadsheets.panels.upload.events
   "Contains events related to the upload panel."
  (:require [clojure.edn :as edn]
            [re-frame.core :as rf]
            [goog.labs.format.csv :as csv]
            [inferenceql.inference.gpm :as gpm]
            [inferenceql.spreadsheets.events.interceptors :refer [event-interceptors]]
            [inferenceql.spreadsheets.csv :as csv-utils]
            [inferenceql.spreadsheets.bayesdb-import :as bayesdb-import]
            [clojure.set :as set]
            [clojure.string :as str]))

(rf/reg-event-db
 :upload/set-display
 event-interceptors
 (fn [db [_ new-val]]
   (assoc-in db [:upload-panel :display] new-val)))

(rf/reg-event-fx
 :upload/read-files
 event-interceptors
 (fn [{:keys [_db]} [_ form-data]]
   (let [names (select-keys form-data [:dataset-name :model-name])
         file-objs (-> form-data
                       (select-keys [:dataset-file :dataset-schema-file :model-file])
                       (set/rename-keys {:dataset-file :dataset
                                         :dataset-schema-file :dataset-schema
                                         :model-file :model}))]
     {:upload/read-files-effect {:files file-objs
                                 :on-success [:upload/process-files names]
                                 :on-failure [:upload/read-failed]}
      :dispatch-n [[:upload/set-display false]
                   [:table/clear]]})))

(rf/reg-event-fx
 :upload/read-url
 event-interceptors
 (fn [{:keys [_db]} [_ form-data]]
   (let [{:keys [url username password]} form-data
         names {:dataset-name "data" :model-name "model"}]
     {:upload/read-url-effect {:url url
                               :username username
                               :password password
                               :on-success [:upload/process-files names]
                               :on-failure [:upload/read-failed]}
      :dispatch-n [[:upload/set-display false]
                   [:table/clear]]})))

(rf/reg-event-fx
 :upload/process-files
 event-interceptors
 (fn [{:keys [_db]} [_ names raw-file-data]]
   (let [{:keys [dataset-name model-name]} names
         dataset-name (keyword dataset-name)
         model-name (keyword model-name)

         dataset-raw (get-in raw-file-data [:dataset :data])
         dataset-schema (get-in raw-file-data [:dataset-schema :data])
         model-raw (get-in raw-file-data [:model :data])
         model-extension (last (str/split (get-in raw-file-data [:model :filename]) #"\."))

         schema (edn/read-string dataset-schema)
         dataset-csv (csv/parse dataset-raw)
         dataset (csv-utils/csv-data->clean-maps schema dataset-csv {:keywordize-cols true})

         model (case model-extension
                 "edn" (gpm/Multimixture (edn/read-string model-raw))
                 ;; TODO: Fix bayesdb-import code by adding a cljs gamma function.
                 "json" (gpm/Multimixture (bayesdb-import/multimix-spec model-raw dataset-csv)))]
     ;; TODO: catch conversion errors.
     (if true
       {:dispatch-n [[:store/dataset dataset-name dataset schema model-name]
                     [:store/model model-name model]]}
       {:dispatch [:upload/read-failed "TODO: write error message for conversion."]}))))

(rf/reg-event-fx
  :upload/read-failed
  event-interceptors
  (fn [{:keys [_db]} [_ error]]
    {:js/console-error error}))