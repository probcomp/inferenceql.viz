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
            [clojure.string :as str]
            [medley.core :as medley]))

(rf/reg-event-db
 :upload/set-display
 event-interceptors
 (fn [db [_ new-val]]
   (assoc-in db [:upload-panel :display] new-val)))

(rf/reg-event-fx
 :upload/read-files
 event-interceptors
 (fn [{:keys [_db]} [_ form-data]]
   (let [{:keys [dataset-name model-name]} form-data
         file-objs (select-keys form-data [:dataset-file :dataset-schema-file :model-file])]
     {:upload/read-files-effect {:files file-objs
                                 :dataset-name dataset-name
                                 :model-name model-name
                                 :on-success [:upload/process-config]
                                 :on-failure [:upload/read-failed]}
      :dispatch-n [[:upload/set-display false]
                   [:table/clear]]})))

(rf/reg-event-fx
 :upload/read-url
 event-interceptors
 (fn [{:keys [_db]} [_ form-data]]
   (let [{:keys [url use-creds]} form-data]
     {:upload/read-url-effect {:url url
                               :use-creds use-creds
                               :on-success [:upload/process-config]
                               :on-failure [:upload/read-failed]}
      :dispatch-n [[:upload/set-display false]
                   [:table/clear]]})))

(rf/reg-event-fx
 :upload/process-config
 event-interceptors
 (fn [{:keys [_db]} [_ config]]
   (let [datasets (medley/map-vals (fn [dataset]
                                     (let [schema (edn/read-string (:schema dataset))
                                           csv-data (csv/parse (:data dataset))
                                           rows (csv-utils/csv-data->clean-maps
                                                 schema csv-data {:keywordize-cols true})]
                                       (merge dataset {:schema schema
                                                       :csv-data csv-data
                                                       :rows rows})))
                                   (:datasets config))

         models (medley/map-vals (fn [model]
                                   (let [model-extension (last (str/split (:filename model) #"\."))
                                         csv-data (get-in datasets [(:dataset model) :rows])
                                         model-obj (case model-extension
                                                     "edn"
                                                     (gpm/Multimixture (edn/read-string (:data model)))
                                                     "json"
                                                     (bayesdb-import/xcat
                                                      (js->clj (.parse js/JSON (:data model)))
                                                      csv-data))]
                                     (.log js/console :model--------------see-next-line)
                                     (.log js/console model-obj)
                                     (assoc model :model-obj model-obj)))
                                 (:models config))

         geodata (medley/map-vals (fn [geodatum] (update geodatum :data #(.parse js/JSON %)))
                                  (:geodata config))]

     ;; TODO: catch conversion errors.
     (if true
       {:dispatch-n [[:store/datasets datasets]
                     [:store/models models]
                     [:store/geodata geodata]]}
       {:dispatch [:upload/read-failed "TODO: write error message for conversion."]}))))

(rf/reg-event-fx
  :upload/read-failed
  event-interceptors
  (fn [{:keys [_db]} [_ error]]
    {:js/console-error error}))